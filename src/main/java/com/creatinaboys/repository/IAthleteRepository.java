package com.creatinaboys.repository;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import java.util.List;

public interface IAthleteRepository {

    /**
     * Stores the athlete. Returns false if an athlete with the same id already
     * exists.
     */
    boolean save(Athlete athlete);

    Athlete findById(String id) throws AthleteNotFoundException;

    List<Athlete> findAll();

    /**
     * Removes the athlete. Returns false if no athlete has that id.
     */
    boolean deleteById(String id);

    /**
     * Replaces the stored athlete that has the same id.
     */
    boolean update(Athlete athlete) throws AthleteNotFoundException;
}
