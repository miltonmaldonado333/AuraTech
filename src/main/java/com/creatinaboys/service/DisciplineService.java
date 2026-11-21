package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.repository.IDisciplineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Manages discovery, naming and registration for the discipline catalog. */
public class DisciplineService implements IDisciplineService {

    /** Disciplinas base del club: siempre existen, aunque el archivo esté vacío o no exista. */
    private static final List<String> DEFAULTS = List.of("Football", "Basketball");

    /** Alias (en minúsculas y sin tildes) → nombre canónico. */
    private static final Map<String, String> ALIASES = Map.of(
            "futbol", "Football",
            "soccer", "Football",
            "baloncesto", "Basketball",
            "basquetbol", "Basketball",
            "basket", "Basketball"
    );

    private final List<String> disciplines = new ArrayList<>();
    private final IDisciplineRepository repository;

    /** Solo en memoria, con las disciplinas base. */
    public DisciplineService() {
        this.repository = null;
        this.disciplines.addAll(DEFAULTS);
    }

    /** Solo en memoria, con una lista inicial (pruebas). Se sanea y se garantizan las disciplinas base. */
    public DisciplineService(List<String> initialDisciplines) {
        Objects.requireNonNull(initialDisciplines, "La lista inicial de disciplinas no puede ser nula.");
        this.repository = null;
        load(initialDisciplines);
    }

    /** Uses the supplied persistence adapter as the source of truth. */
    public DisciplineService(IDisciplineRepository repository) throws PersistenceException {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo.");

    }

    private void load(List<String> source) {
        for (String raw : source) {
            if (raw != null && !raw.isBlank()) {
                String name = canonical(raw);
                if (indexOf(name) < 0) {
                    disciplines.add(name);
                }
            }
        }
        for (String base : DEFAULTS) {
            if (indexOf(base) < 0) {
                disciplines.add(base);
            }
        }
    }

    @Override
    public synchronized List<String> getAvailableDisciplines() throws PersistenceException {
        if (repository == null) {
            return List.copyOf(disciplines);
        }

        List<String> available = new ArrayList<>(DEFAULTS);
        for (String discipline : repository.findAll()) {
            if (available.stream().noneMatch(discipline::equalsIgnoreCase)) {
                available.add(discipline);
            }
        }
        return List.copyOf(available);
    }

    @Override
    public synchronized boolean registerDiscipline(String disciplineName) throws PersistenceException {
        if (disciplineName == null || disciplineName.isBlank()) {
            throw new IllegalArgumentException("The discipline name is required.");
        }
        String name = canonical(disciplineName);
        if (!name.matches("[\\p{L}\\p{M}\\p{N} ._'-]{1,60}")
                || !name.matches(".*[\\p{L}\\p{N}].*")) {
            throw new IllegalArgumentException("Enter a discipline name with 1 to 60 characters.");
        }
        if (DEFAULTS.stream().anyMatch(name::equalsIgnoreCase)) {
            return false;
        }
        if (repository != null) {
            return repository.add(name);
        }
        if (indexOf(name) >= 0) {
            return false;
        }
        disciplines.add(name);
        return true;
    }

    @Override
    public synchronized boolean isDisciplineAvailable(String disciplineName) throws PersistenceException {
        return findDisciplineByName(disciplineName).isPresent();
    }

    @Override
    public synchronized Optional<String> findDisciplineByName(String disciplineName) throws PersistenceException {
        if (disciplineName == null || disciplineName.isBlank()) {
            return Optional.empty();
        }
        String name = canonical(disciplineName);
        return getAvailableDisciplines().stream().filter(name::equalsIgnoreCase).findFirst();
    }

    @Override
    public synchronized int getTotalDisciplinesCount() throws PersistenceException {
        return getAvailableDisciplines().size();
    }

    // ------------------------------------------------------------------ utilidades

    private int indexOf(String name) {
        for (int i = 0; i < disciplines.size(); i++) {
            if (disciplines.get(i).equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /** Quita espacios sobrantes, resuelve alias y pone la primera letra en mayúscula. */
    private static String canonical(String raw) {
        String clean = java.text.Normalizer.normalize(raw.strip(), java.text.Normalizer.Form.NFC)
                .replaceAll("\\s+", " ");
        String key = java.text.Normalizer.normalize(clean, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        String alias = ALIASES.get(key);
        if (alias != null) {
            return alias;
        }
        return clean.substring(0, 1).toUpperCase(Locale.ROOT) + clean.substring(1);
    }
}