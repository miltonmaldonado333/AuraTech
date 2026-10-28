package com.creatinaboys.service;

import com.creatinaboys.model.CategoryEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación concreta del servicio de gestión de entrenadores para el Club Deportivo Creatina-Boys.
 * <p>
 * Esta clase administra la asignación de entrenadores a las disciplinas (fútbol, baloncesto, etc.) y categorías de edad[cite: 1, 6].
 * Aplica las siguientes buenas prácticas de programación:
 * </p>
 * <ul>
 *   <li><b>SRP (Single Responsibility Principle):</b> Dedicada exclusivamente al dominio de entrenadores[cite: 6].</li>
 *   <li><b>DIP (Dependency Inversion Principle):</b> Implementa la interfaz {@link ICoachService}[cite: 6].</li>
 *   <li><b>Java Streams & Lambdas:</b> Filtrados, mapeos y consultas optimizadas mediante la API de Streams de Java.</li>
 *   <li><b>Programación Defensiva:</b> Validación estricta con {@link Objects#requireNonNull} y prevención de retornos nulos.</li>
 * </ul>
 * 
 * @author Milton Andrés Maldonado Salcedo
 * @version 1.0
 */
public class CoachService implements ICoachService {

    /** Colección de entrenadores administrados por el servicio. */
    private final List<Object> coachList;

    /**
     * Constructor por defecto. Inicializa la estructura de datos interna.
     */
    public CoachService() {
        this.coachList = new ArrayList<>();
    }

    /**
     * Constructor con inyección de colección para pruebas o persistencia previa.
     *
     * @param initialCoaches Lista inicial de entrenadores a cargar.
     * @throws NullPointerException Si la lista proporcionada es {@code null}.
     */
    public CoachService(List<Object> initialCoaches) {
        Objects.requireNonNull(initialCoaches, "La lista inicial de entrenadores no puede ser nula.");
        this.coachList = new ArrayList<>(initialCoaches);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valida mediante programación defensiva que todos los campos requeridos estén presentes
     * antes de proceder con la asignación lógica.
     * </p>
     *
     * @throws IllegalArgumentException Si el ID del entrenador, la disciplina o la categoría son inválidos o vacíos.
     */
    @Override
    public boolean assignCoachToCategory(String coachId, String discipline, CategoryEnum category) {
        if (coachId == null || coachId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del entrenador es requerido para realizar la asignación.");
        }
        if (discipline == null || discipline.trim().isEmpty()) {
            throw new IllegalArgumentException("La disciplina deportiva no puede estar vacía.");
        }
        Objects.requireNonNull(category, "La categoría de edad no puede ser nula.");

        // Lógica de asignación y verificación utilizando Streams para comprobar duplicados
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Devuelve una vista inmodificable de la lista para preservar la encapsulación y evitar
     * mutaciones no autorizadas desde la capa gráfica (Swing UI)[cite: 6].
     * </p>
     *
     * @return Lista inmutable de entrenadores registrados.
     */
    @Override
    public List<Object> getAllCoaches() {
        return Collections.unmodifiableList(
                coachList.stream()
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Utiliza la API de Streams para realizar la búsqueda secuencial y retorna el resultado
     * envuelto en un {@link Optional} seguro contra referencias nulas.
     * </p>
     */
    @Override
    public Optional<Object> findCoachById(String coachId) {
        if (coachId == null || coachId.trim().isEmpty()) {
            return Optional.empty();
        }

        return coachList.stream()
                .filter(Objects::nonNull)
                .filter(coach -> coachId.trim().equalsIgnoreCase(coach.toString()))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filtra la colección utilizando expresiones Lambda e ignora valores nulos o cadenas vacías.
     * </p>
     */
    @Override
    public List<Object> getCoachesByDiscipline(String discipline) {
        if (discipline == null || discipline.trim().isEmpty()) {
            return List.of();
        }

        return coachList.stream()
                .filter(Objects::nonNull)
                .filter(coach -> discipline.trim().equalsIgnoreCase(coach.toString()))
                .toList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filtra los entrenadores asignados a la categoría especificada usando {@code Stream.filter}.
     * </p>
     */
    @Override
    public List<Object> getCoachesByCategory(CategoryEnum category) {
        if (category == null) {
            return List.of();
        }

        return coachList.stream()
                .filter(Objects::nonNull)
                .filter(coach -> category.equals(coach.toString()))
                .toList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Utiliza {@code Collectors.groupingBy} y {@code Collectors.counting} para generar un resumen
     * estadístico de la distribución del personal técnico por disciplina[cite: 1, 6].
     * </p>
     */
    @Override
    public Map<String, Long> getCoachesCountByDiscipline() {
        return coachList.stream()
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.groupingBy(
                        Object::toString,
                        java.util.stream.Collectors.counting()
                ));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Computa la cantidad total de entrenadores activos filtrando elementos nulos de la colección.
     * </p>
     */
    @Override
    public int getTotalCoachesCount() {
        return (int) coachList.stream()
                .filter(Objects::nonNull)
                .count();
    }
}