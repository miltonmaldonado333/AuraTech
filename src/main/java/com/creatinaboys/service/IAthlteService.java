package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;

import java.util.List;

public interface IAthlteService {

    boolean registerAthlete(Athlete athlete) throws InvalidAgeException, ValidationException;

    Athlete findAthlete(AthleteKey key) throws AthleteNotFoundException;
    List<Athlete> findAthletesById(String id);
    List<Athlete> getAllAthletes();
    boolean existsByKey(AthleteKey key);
    int getTotalAthletesCount();
    <T extends Athlete> long getAthletesCountByType(Class<T> type);

    boolean deleteAthlete(AthleteKey key);
    boolean toggleAthleteStatus(AthleteKey key);
    boolean setAthleteActive(AthleteKey key, boolean active);
    boolean renewMembership(AthleteKey key, int months);
    void applyActivationPolicy(IActivationPolicy policy);
}