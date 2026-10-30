package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.repository.IDisciplineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio del catálogo de disciplinas deportivas del Club Creatina-Boys.
 * <p>
 * Los nombres canónicos son los mismos que devuelve {@code Athlete.getDiscipline()}
 * ("Football", "Basketball"), de modo que el catálogo y los deportistas se puedan cruzar.
 * </p>
 * <ul>
 *   <li><b>Persistencia:</b> si se inyecta un {@link IDisciplineRepository}, el catálogo se carga
 *       al iniciar y cada disciplina nueva se guarda en archivo (antes se perdía al cerrar la app).</li>
 *   <li><b>Alias:</b> "Fútbol" y "Baloncesto" (como se ven en la UI) se reconocen como
 *       "Football" y "Basketball", para no crear duplicados por el idioma.</li>
 *   <li><b>Seguridad de hilos:</b> los métodos están sincronizados.</li>
 * </ul>
 *
 * @author Milton Andrés Maldonado Salcedo
 * @version 2.0
 */
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
    private final IDisciplineRepository repository; // null = solo en memoria

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

    /** Con persistencia: carga el catálogo guardado y completa las disciplinas base si faltan. */
    public DisciplineService(IDisciplineRepository repository) throws PersistenceException {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo.");
        int before = load(repository.findAll());
        if (before != disciplines.size() || before == 0) {
            repository.saveAll(disciplines); // primera ejecución o archivo incompleto
        }
    }

    /** Carga y sanea la lista; devuelve cuántas había antes de completar las base. */
    private int load(List<String> source) {
        for (String raw : source) {
            if (raw != null && !raw.isBlank()) {
                String name = canonical(raw);
                if (indexOf(name) < 0) {
                    disciplines.add(name);
                }
            }
        }
        int before = disciplines.size();
        for (String base : DEFAULTS) {
            if (indexOf(base) < 0) {
                disciplines.add(base);
            }
        }
        return before;
    }

    @Override
    public synchronized List<String> getAvailableDisciplines() throws PersistenceException {
        return List.copyOf(disciplines);
    }

    @Override
    public synchronized boolean registerDiscipline(String disciplineName) throws PersistenceException {
        if (disciplineName == null || disciplineName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la disciplina no puede estar vacío.");
        }
        String name = canonical(disciplineName);
        if (indexOf(name) >= 0) {
            return false;
        }
        disciplines.add(name);
        if (repository != null) {
            try {
                repository.saveAll(disciplines);
            } catch (PersistenceException e) {
                disciplines.remove(name); // no dejar memoria y archivo desincronizados
                throw e;
            }
        }
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
        int i = indexOf(canonical(disciplineName));
        return i < 0 ? Optional.empty() : Optional.of(disciplines.get(i));
    }

    @Override
    public synchronized int getTotalDisciplinesCount() throws PersistenceException {
        return disciplines.size();
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
        String clean = raw.trim().replaceAll("\\s+", " ");
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