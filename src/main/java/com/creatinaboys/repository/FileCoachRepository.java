package com.creatinaboys.repository;

import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Coach;
import com.creatinaboys.exceptions.PersistenceException;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileCoachRepository implements ICoachRepository {

    private static final String DEFAULT_FILE_PATH = "data/coaches.txt";
    private final String filePath;
    private final CoachParser parser;

    public FileCoachRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FileCoachRepository(String filePath) {
        this.filePath = filePath;
        this.parser = new CoachParser();
    }

    @Override
    public boolean save(Coach coach) throws PersistenceException {
        List<Coach> coaches = findAll();
        if (coaches.stream().anyMatch(c -> c.getId().equalsIgnoreCase(coach.getId()))) {
            return false; // Ya existe un entrenador con ese ID
        }
        coaches.add(coach);
        saveAll(coaches);
        return true;
    }

    @Override
    public Optional<Coach> findById(String id) throws PersistenceException {
        return findAll().stream()
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<Coach> findAll() throws PersistenceException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<Coach> coaches = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || !line.startsWith("COACH;")) continue;
                String[] fields = line.split(";");
                coaches.add(parser.fromTextFields(fields));
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al leer el archivo de entrenadores: " + filePath, e);
        }
        return coaches;
    }

    @Override
    public List<Coach> findByDisciplineAndCategory(String discipline, CategoryEnum category) throws PersistenceException {
        return findAll().stream()
                .filter(c -> c.getDiscipline().equalsIgnoreCase(discipline) && c.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(String id) throws PersistenceException {
        List<Coach> coaches = findAll();
        boolean removed = coaches.removeIf(c -> c.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(coaches);
        }
        return removed;
    }

    @Override
    public boolean update(Coach coach) throws PersistenceException {
        List<Coach> coaches = findAll();
        int index = -1;
        for (int i = 0; i < coaches.size(); i++) {
            if (coaches.get(i).getId().equalsIgnoreCase(coach.getId())) {
                index = i;
                break;
            }
        }

        if (index == -1) return false;

        coaches.set(index, coach);
        saveAll(coaches);
        return true;
    }

    private void saveAll(List<Coach> coaches) throws PersistenceException {
        Path path = Paths.get(filePath);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (Coach coach : coaches) {
                    writer.write(parser.toTextLine(coach));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al guardar en el archivo de entrenadores: " + filePath, e);
        }
    }
}