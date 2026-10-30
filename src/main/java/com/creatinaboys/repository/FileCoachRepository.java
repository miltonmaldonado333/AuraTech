package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Coach;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileCoachRepository implements ICoachRepository {

    private static final String DEFAULT_FILE_PATH = "data/coaches.txt";
    private final Path path;
    private final CoachParser parser;

    public FileCoachRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FileCoachRepository(String filePath) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "filePath cannot be null."));
        this.parser = new CoachParser();
    }

    @Override
    public boolean save(Coach coach) throws PersistenceException {
        if (coach == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo.");
        }
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

    /** Una línea dañada se omite (y se informa) en lugar de impedir cargar al resto. */
    @Override
    public List<Coach> findAll() throws PersistenceException {
        List<Coach> coaches = new ArrayList<>();
        List<String> lines = FileStorage.readLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                String[] fields = TextCodec.split(line, ';');
                if (!"COACH".equals(fields[0].trim())) {
                    continue;
                }
                coaches.add(parser.fromTextFields(fields));
            } catch (RuntimeException e) {
                System.err.println("Línea " + (i + 1) + " omitida en " + path + ": " + e.getMessage());
            }
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
        if (coach == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo.");
        }
        List<Coach> coaches = findAll();
        for (int i = 0; i < coaches.size(); i++) {
            if (coaches.get(i).getId().equalsIgnoreCase(coach.getId())) {
                coaches.set(i, coach);
                saveAll(coaches);
                return true;
            }
        }
        return false;
    }

    private void saveAll(List<Coach> coaches) throws PersistenceException {
        List<String> lines = new ArrayList<>(coaches.size());
        for (Coach coach : coaches) {
            lines.add(parser.toTextLine(coach));
        }
        FileStorage.writeLines(path, lines);
    }
}
