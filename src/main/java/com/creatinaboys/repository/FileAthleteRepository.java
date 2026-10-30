package com.creatinaboys.repository;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.Footballer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileAthleteRepository implements IAthleteRepository {

    private static final String DEFAULT_FILE_PATH = "data/athletes.txt";

    /** Separa la línea del deportista de sus datos de estado: ...;@activo|manual|venceEl */
    private static final String EXTRAS_MARK = ";@";

    private final Path path;
    private final AthleteParserRegistry parsers;

    public FileAthleteRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FileAthleteRepository(String filePath) {
        this(filePath, defaultParsers());
    }

    public FileAthleteRepository(String filePath, AthleteParserRegistry parsers) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "filePath cannot be null."));
        this.parsers = Objects.requireNonNull(parsers, "parsers cannot be null.");
    }

    private static AthleteParserRegistry defaultParsers() {
        return new AthleteParserRegistry()
                .register(Footballer.class, new FootballerParser())
                .register(Basketballer.class, new BasketballerParser());
    }

    /**
     * Guarda un atleta nuevo. Devuelve false (sin modificar nada) si ya existe
     * un registro con la misma clave (documento + disciplina).
     */
    @Override
    public boolean save(Athlete athlete) {
        if (athlete == null) {
            throw new IllegalArgumentException("athlete cannot be null.");
        }
        List<Athlete> athletes = findAll();
        AthleteKey key = athlete.getKey();
        if (athletes.stream().anyMatch(a -> a.getKey().equals(key))) {
            return false;
        }
        athletes.add(athlete);
        saveAll(athletes);
        return true;
    }

    @Override
    public Athlete findByKey(AthleteKey key) throws AthleteNotFoundException {
        return findAll().stream()
                .filter(a -> a.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new AthleteNotFoundException(key.toString()));
    }

    /** Todos los registros de una misma persona (uno por disciplina). */
    @Override
    public List<Athlete> findById(String id) {
        List<Athlete> result = new ArrayList<>();
        for (Athlete a : findAll()) {
            if (a.getId().equals(id)) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Lee todos los atletas. Una línea dañada no impide cargar las demás: se
     * omite y se informa por la salida de error (el archivo original queda
     * respaldado como .bak antes de cualquier reescritura).
     */
    @Override
    public List<Athlete> findAll() {
        List<Athlete> athletes = new ArrayList<>();
        List<String> lines = FileStorage.readLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                athletes.add(parseLine(line));
            } catch (RuntimeException e) {
                System.err.println("Línea " + (i + 1) + " omitida en " + path + ": " + e.getMessage());
            }
        }
        return athletes;
    }

    @Override
    public boolean deleteByKey(AthleteKey key) {
        List<Athlete> athletes = findAll();
        boolean removed = athletes.removeIf(a -> a.getKey().equals(key));
        if (removed) {
            saveAll(athletes);
        }
        return removed;
    }

    /** Reemplaza el atleta almacenado que tenga la misma clave (documento + disciplina). */
    @Override
    public boolean update(Athlete athlete) throws AthleteNotFoundException {
        if (athlete == null) {
            throw new IllegalArgumentException("athlete cannot be null.");
        }
        AthleteKey key = athlete.getKey();
        List<Athlete> athletes = findAll();
        for (int i = 0; i < athletes.size(); i++) {
            if (athletes.get(i).getKey().equals(key)) {
                athletes.set(i, athlete);
                saveAll(athletes);
                return true;
            }
        }
        throw new AthleteNotFoundException(key.toString());
    }

    private void saveAll(List<Athlete> athletes) {
        List<String> lines = new ArrayList<>(athletes.size());
        for (Athlete athlete : athletes) {
            lines.add(toLine(athlete));
        }
        FileStorage.writeLines(path, lines);
    }

    // ------------------------------------------------------------------ Serialización

    /** Línea del parser + estado de la afiliación. */
    private String toLine(Athlete athlete) {
        return parsers.toTextLine(athlete)
                + EXTRAS_MARK
                + athlete.isActive() + "|"
                + athlete.isManualOverride() + "|"
                + athlete.getPaidUntil();
    }

    /** Separa el bloque de estado (si existe) y deja que los parsers lean la línea original. */
    private Athlete parseLine(String line) {
        String base = line;
        String extras = null;

        int idx = line.lastIndexOf(EXTRAS_MARK);
        if (idx >= 0) {
            base = line.substring(0, idx);
            extras = line.substring(idx + EXTRAS_MARK.length());
        }

        String[] fields = TextCodec.split(base, ';');
        Athlete athlete = parsers.fromTextLine(fields[0].trim(), fields);

        if (extras != null) {
            applyExtras(athlete, extras);
        }
        return athlete;
    }

    /** Líneas viejas (sin bloque) conservan los valores por defecto del modelo. */
    private void applyExtras(Athlete athlete, String extras) {
        String[] parts = extras.trim().split("\\|");
        if (parts.length != 3) {
            return;
        }
        try {
            boolean active = Boolean.parseBoolean(parts[0]);
            boolean manual = Boolean.parseBoolean(parts[1]);
            athlete.setPaidUntil(LocalDate.parse(parts[2]));
            athlete.setActive(active);
            if (manual) {
                athlete.setActiveManually(active);
            }
        } catch (RuntimeException e) {
            System.err.println("Estado ignorado por formato inválido: " + extras);
        }
    }
}