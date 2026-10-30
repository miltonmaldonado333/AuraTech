package com.creatinaboys.repository;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;

import java.util.List;

public interface IAthleteRepository {
    boolean save(Athlete athlete);
    Athlete findByKey(AthleteKey key) throws AthleteNotFoundException;
    List<Athlete> findById(String id);          // todos los registros de una persona
    List<Athlete> findAll();
    boolean deleteByKey(AthleteKey key);
    boolean update(Athlete athlete) throws AthleteNotFoundException; // identifica por athlete.getKey()
}