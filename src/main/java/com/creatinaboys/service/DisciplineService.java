package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación concreta del servicio de gestión de disciplinas deportivas para el Club Deportivo Creatina-Boys.
 * <p>
 * Esta clase administra el catálogo de deportes disponibles (Fútbol, Baloncesto, etc.) y garantiza
 * la extensión limpia del sistema bajo las siguientes buenas prácticas:
 * </p>
 * <ul>
 *   <li><b>SRP (Single Responsibility Principle):</b> Su única función es administrar la oferta disciplinaria.</li>
 *   <li><b>OCP (Open/Closed Principle):</b> Permite añadir disciplinas dinámicamente sin alterar el código de la GUI.</li>
 *   <li><b>Java Streams & Lambdas:</b> Búsquedas, filtrados e inspecciones utilizando la API funcional de Java.</li>
 *   <li><b>Programación Defensiva:</b> Control estricto de nulos, cadenas en blanco y colecciones inmodificables.</li>
 * </ul>
 * 
 * @author Milton Andrés Maldonado Salcedo
 * @version 1.0
 */
public class DisciplineService implements IDisciplineService {
    // Nota: el catálogo vive en memoria; las disciplinas agregadas en tiempo de ejecución no se guardan en archivo.

    /** Colección interna de disciplinas deportivas habilitadas en el club. */
    private final List<String> disciplines;

    /**
     * Constructor por defecto. Carga las disciplinas base del club (Fútbol y Baloncesto).
     */
    public DisciplineService() {
        this.disciplines = new ArrayList<>();
        this.disciplines.add("Football");
        this.disciplines.add("Basketball");
    }

    /**
     * Constructor con inyección de lista inicial para pruebas o precarga desde archivo.
     *
     * @param initialDisciplines Lista de disciplinas iniciales.
     * @throws NullPointerException Si la lista proporcionada es {@code null}.
     */
    public DisciplineService(List<String> initialDisciplines) {
        Objects.requireNonNull(initialDisciplines, "La lista inicial de disciplinas no puede ser nula.");
        this.disciplines = initialDisciplines.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(d -> !d.isEmpty())
                .distinct()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retorna una copia inmodificable de la lista para proteger el encapsulamiento interno
     * frente a alteraciones externas desautorizadas.
     * </p>
     */
    @Override
    public List<String> getAvailableDisciplines() throws PersistenceException {
        return Collections.unmodifiableList(
                disciplines.stream()
                        .filter(Objects::nonNull)
                        .filter(d -> !d.isBlank())
                        .toList()
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Utiliza Streams y Lambdas para verificar insensibilidad a mayúsculas/minúsculas y evitar duplicados.
     * </p>
     *
     * @throws IllegalArgumentException Si {@code disciplineName} es nulo o está en blanco.
     */
    @Override
    public boolean registerDiscipline(String disciplineName) throws PersistenceException {
        if (disciplineName == null || disciplineName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la disciplina no puede estar vacío.");
        }

        String formattedName = disciplineName.trim();

        boolean exists = disciplines.stream()
                .filter(Objects::nonNull)
                .anyMatch(d -> d.equalsIgnoreCase(formattedName));

        if (exists) {
            return false;
        }

        return disciplines.add(formattedName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisciplineAvailable(String disciplineName) throws PersistenceException {
        if (disciplineName == null || disciplineName.isBlank()) {
            return false;
        }

        return disciplines.stream()
                .filter(Objects::nonNull)
                .anyMatch(d -> d.equalsIgnoreCase(disciplineName.trim()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> findDisciplineByName(String disciplineName) throws PersistenceException {
        if (disciplineName == null || disciplineName.isBlank()) {
            return Optional.empty();
        }

        return disciplines.stream()
                .filter(Objects::nonNull)
                .filter(d -> d.equalsIgnoreCase(disciplineName.trim()))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalDisciplinesCount() throws PersistenceException {
        return (int) disciplines.stream()
                .filter(Objects::nonNull)
                .filter(d -> !d.isBlank())
                .count();
    }
}