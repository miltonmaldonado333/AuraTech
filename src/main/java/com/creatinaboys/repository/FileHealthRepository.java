package com.creatinaboys.repository;

import com.creatinaboys.model.HealthRecord;
import com.creatinaboys.exceptions.PersistenceException;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileHealthRepository implements IHealthRepository {

    private static final String DEFAULT_FILE_PATH = "data/health_records.txt";
    private final String filePath;
    private final HealthRecordParser parser;

    public FileHealthRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FileHealthRepository(String filePath) {
        this.filePath = filePath;
        this.parser = new HealthRecordParser();
    }

    @Override
    public boolean save(HealthRecord record) throws PersistenceException {
        List<HealthRecord> records = findAll();
        // Si ya existe, lo reemplazamos
        records.removeIf(r -> r.getAthleteId().equals(record.getAthleteId()));
        records.add(record);
        saveAll(records);
        return true;
    }

    @Override
    public Optional<HealthRecord> findByAthleteId(String athleteId) throws PersistenceException {
        return findAll().stream()
                .filter(r -> r.getAthleteId().equalsIgnoreCase(athleteId))
                .findFirst();
    }

    @Override
    public List<HealthRecord> findAll() throws PersistenceException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<HealthRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || !line.startsWith("HEALTH;")) continue;
                String[] fields = line.split(";");
                records.add(parser.fromTextFields(fields));
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al leer el archivo de registros de salud: " + filePath, e);
        }
        return records;
    }

    @Override
    public boolean deleteByAthleteId(String athleteId) throws PersistenceException {
        List<HealthRecord> records = findAll();
        boolean removed = records.removeIf(r -> r.getAthleteId().equalsIgnoreCase(athleteId));
        if (removed) {
            saveAll(records);
        }
        return removed;
    }

    @Override
    public boolean update(HealthRecord record) throws PersistenceException {
        return save(record);
    }

    private void saveAll(List<HealthRecord> records) throws PersistenceException {
        Path path = Paths.get(filePath);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (HealthRecord record : records) {
                    writer.write(parser.toTextLine(record));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al guardar en el archivo de registros de salud: " + filePath, e);
        }
    }
}