package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/** Guarda el catálogo de disciplinas en un archivo de texto (una por línea, UTF-8). */
public class FileDisciplineRepository implements IDisciplineRepository {

    private static final String DEFAULT_FILE_PATH = "data/disciplines.txt";
    private final Path path;

    public FileDisciplineRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FileDisciplineRepository(String filePath) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "filePath cannot be null."));
    }

    @Override
    public List<String> findAll() throws PersistenceException {
        return FileStorage.readLines(path).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    @Override
    public void saveAll(List<String> disciplines) throws PersistenceException {
        FileStorage.writeLines(path, Objects.requireNonNull(disciplines, "disciplines cannot be null."));
    }
}