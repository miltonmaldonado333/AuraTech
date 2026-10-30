package com.creatinaboys.repository;

import com.creatinaboys.model.HealthRecord;
import com.creatinaboys.exceptions.PersistenceException;
import java.util.List;
import java.util.Optional;

public interface IHealthRepository {
    boolean save(HealthRecord record) throws PersistenceException;
    Optional<HealthRecord> findByAthleteId(String athleteId) throws PersistenceException;
    List<HealthRecord> findAll() throws PersistenceException;
    boolean deleteByAthleteId(String athleteId) throws PersistenceException;
    boolean update(HealthRecord record) throws PersistenceException;
}