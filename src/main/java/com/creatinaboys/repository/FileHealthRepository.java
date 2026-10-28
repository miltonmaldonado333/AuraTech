package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.HealthRecord;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FileHealthRepository implements IHealthRepository {

    private static final String DEFAULT_FILE_PATH = "data/health_records.txt";
    private final Path path;
    private final HealthRecordParser parser;

    public FileHealthRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FileHealthRepository(String filePath) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "filePath cannot be null."));
        this.parser = new HealthRecordParser();
    }

    /** Guarda la ficha; si el atleta ya tenía una, la reemplaza. */
    @Override
    public boolean save(HealthRecord record) throws PersistenceException {
        if (record == null) {
            throw new IllegalArgumentException("La ficha de salud no puede ser nula.");
        }
        List<HealthRecord> records = findAll();
        records.removeIf(r -> r.getAthleteId().equalsIgnoreCase(record.getAthleteId()));
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

    /** Una línea dañada se omite (y se informa) en lugar de impedir cargar al resto. */
    @Override
    public List<HealthRecord> findAll() throws PersistenceException {
        List<HealthRecord> records = new ArrayList<>();
        List<String> lines = FileStorage.readLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                String[] fields = TextCodec.split(line, ';');
                if (!"HEALTH".equals(fields[0].trim())) {
                    continue;
                }
                records.add(parser.fromTextFields(fields));
            } catch (RuntimeException e) {
                System.err.println("Línea " + (i + 1) + " omitida en " + path + ": " + e.getMessage());
            }
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

    /** Actualiza una ficha existente. Devuelve false si el atleta no tiene ficha. */
    @Override
    public boolean update(HealthRecord record) throws PersistenceException {
        if (record == null) {
            throw new IllegalArgumentException("La ficha de salud no puede ser nula.");
        }
        if (findByAthleteId(record.getAthleteId()).isEmpty()) {
            return false;
        }
        return save(record);
    }

    private void saveAll(List<HealthRecord> records) throws PersistenceException {
        List<String> lines = new ArrayList<>(records.size());
        for (HealthRecord record : records) {
            lines.add(parser.toTextLine(record));
        }
        FileStorage.writeLines(path, lines);
    }
}
