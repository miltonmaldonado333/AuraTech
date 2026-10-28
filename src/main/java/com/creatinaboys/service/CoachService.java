package com.creatinaboys.service;

import com.creatinaboys.exceptions.CoachNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Coach;
import com.creatinaboys.repository.ICoachRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación concreta del servicio de gestión de entrenadores para el Club Deportivo Creatina-Boys.
 * <p>
 * Administra la asignación de entrenadores a las disciplinas y categorías de edad, interactuando
 * directamente con la capa de repositorio para la persistencia de datos.
 * </p>
 *
 * @author Milton Andrés Maldonado Salcedo & Jesus Martinez Baute
 * @version 1.1
 */
public class CoachService implements ICoachService {

    private final ICoachRepository coachRepository;

    /**
     * Constructor con inyección de dependencias para la capa de repositorio.
     *
     * @param coachRepository Instancia del repositorio de entrenadores (DIP).
     */
    public CoachService(ICoachRepository coachRepository) {
        this.coachRepository = Objects.requireNonNull(coachRepository, "El repositorio de entrenadores no puede ser nulo.");
    }

    @Override
    public boolean registerCoach(Coach coach) throws PersistenceException {
        if (coach == null) {
            throw new IllegalArgumentException("El entrenador a registrar no puede ser nulo.");
        }
        // El repositorio devuelve false si ya existe un entrenador con ese documento.
        return coachRepository.save(coach);
    }

    @Override
    public boolean assignCoachToCategory(String coachId, String discipline, CategoryEnum category) 
            throws CoachNotFoundException, PersistenceException {
        
        if (coachId == null || coachId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del entrenador es requerido.");
        }
        if (discipline == null || discipline.trim().isEmpty()) {
            throw new IllegalArgumentException("La disciplina deportiva no puede estar vacía.");
        }
        Objects.requireNonNull(category, "La categoría de edad no puede ser nula.");

        Coach coach = coachRepository.findById(coachId.trim())
                .orElseThrow(() -> new CoachNotFoundException(coachId.trim()));

        coach.setDiscipline(discipline.trim());
        coach.setCategory(category);

        return coachRepository.update(coach);
    }

    @Override
    public List<Coach> getAllCoaches() throws PersistenceException {
        return Collections.unmodifiableList(coachRepository.findAll());
    }

    @Override
    public Optional<Coach> findCoachById(String coachId) throws PersistenceException {
        if (coachId == null || coachId.trim().isEmpty()) {
            return Optional.empty();
        }
        return coachRepository.findById(coachId.trim());
    }

    @Override
    public List<Coach> getCoachesByDiscipline(String discipline) throws PersistenceException {
        if (discipline == null || discipline.trim().isEmpty()) {
            return List.of();
        }
        return coachRepository.findAll().stream()
                .filter(coach -> discipline.trim().equalsIgnoreCase(coach.getDiscipline()))
                .toList();
    }

    @Override
    public List<Coach> getCoachesByCategory(CategoryEnum category) throws PersistenceException {
        if (category == null) {
            return List.of();
        }
        return coachRepository.findAll().stream()
                .filter(coach -> category == coach.getCategory())
                .toList();
    }

    @Override
    public boolean updateCoach(Coach coach) throws CoachNotFoundException, PersistenceException {
        if (coach == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo.");
        }
        boolean updated = coachRepository.update(coach);
        if (!updated) {
            throw new CoachNotFoundException(coach.getId());
        }
        return true;
    }

    @Override
    public boolean deleteCoach(String coachId) throws CoachNotFoundException, PersistenceException {
        if (coachId == null || coachId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del entrenador es obligatorio.");
        }
        boolean deleted = coachRepository.deleteById(coachId.trim());
        if (!deleted) {
            throw new CoachNotFoundException(coachId.trim());
        }
        return true;
    }

    @Override
    public Map<String, Long> getCoachesCountByDiscipline() throws PersistenceException {
        return coachRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Coach::getDiscipline,
                        Collectors.counting()
                ));
    }

    @Override
    public int getTotalCoachesCount() throws PersistenceException {
        return coachRepository.findAll().size();
    }
}