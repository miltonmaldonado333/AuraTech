package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.FitnessStatusEnum;
import com.creatinaboys.model.HealthRecord;
import com.creatinaboys.model.InjuryRecord;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.IHealthRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HealthService implements IHealthService {

    private final IHealthRepository healthRepository;
    private final IAthleteRepository athleteRepository;

    public HealthService(IHealthRepository healthRepository, IAthleteRepository athleteRepository) {
        if (healthRepository == null || athleteRepository == null) {
            throw new IllegalArgumentException("Los repositorios inyectados no pueden ser nulos");
        }
        this.healthRepository = healthRepository;
        this.athleteRepository = athleteRepository;
    }

    /**
     * Guarda o actualiza la ficha médica de un atleta validando su existencia previa.
     */
    @Override
    public boolean saveOrUpdateHealthRecord(HealthRecord record) throws AthleteNotFoundException, PersistenceException {
        if (record == null) {
            throw new IllegalArgumentException("El registro de salud no puede ser nulo");
        }
        
        // Regla de Negocio: La ficha debe estar vinculada a un deportista existente
        athleteRepository.findById(record.getAthleteId());

        return healthRepository.save(record);
    }

    /**
     * Obtiene la ficha médica de un deportista.
     */
    @Override
    public Optional<HealthRecord> getHealthRecordByAthleteId(String athleteId) throws PersistenceException {
        if (athleteId == null || athleteId.isBlank()) {
            throw new IllegalArgumentException("El documento del atleta es obligatorio");
        }
        return healthRepository.findByAthleteId(athleteId);
    }

    /**
     * Registra una nueva lesión en la ficha médica de un deportista.
     */
    @Override
    public boolean addInjuryToAthlete(String athleteId, InjuryRecord injury) throws AthleteNotFoundException, PersistenceException {
        if (injury == null) {
            throw new IllegalArgumentException("La lesión no puede ser nula");
        }

        requireAthleteId(athleteId);
        // Verificar que el atleta existe
        athleteRepository.findById(athleteId);

        // Obtener la ficha existente o crear una por defecto si no tiene una registrada
        HealthRecord record = healthRepository.findByAthleteId(athleteId)
                .orElseGet(() -> new HealthRecord(athleteId, FitnessStatusEnum.FIT, LocalDate.now(), ""));

        record.addInjury(injury);
        return healthRepository.save(record);
    }

    /**
     * Actualiza la aptitud médica de un atleta.
     */
    @Override
    public boolean updateFitnessStatus(String athleteId, FitnessStatusEnum newStatus, String restrictions, LocalDate assessmentDate) 
            throws AthleteNotFoundException, PersistenceException {
        
        requireAthleteId(athleteId);
        athleteRepository.findById(athleteId);

        HealthRecord record = healthRepository.findByAthleteId(athleteId)
                .orElseGet(() -> new HealthRecord(athleteId, newStatus, assessmentDate, restrictions));

        record.updateFitnessStatus(newStatus, restrictions, assessmentDate);
        return healthRepository.save(record);
    }

    /**
     * Valida si un atleta está habilitado para entrenar.
     */
    @Override
    public boolean isAthleteFitToTrain(String athleteId) throws PersistenceException {
        requireAthleteId(athleteId);
        Optional<HealthRecord> record = healthRepository.findByAthleteId(athleteId);
        // Si no tiene ficha registrada se asume Apto por defecto o según reglas del club
        return record.map(r -> r.getFitnessStatus() != FitnessStatusEnum.UNFIT).orElse(true);
    }

    /**
     * Retorna todas las fichas de salud registradas.
     */
    @Override
    public List<HealthRecord> getAllHealthRecords() throws PersistenceException {
        return healthRepository.findAll();
    }

    private static void requireAthleteId(String athleteId) {
        if (athleteId == null || athleteId.isBlank()) {
            throw new IllegalArgumentException("El documento del atleta es obligatorio");
        }
    }
}