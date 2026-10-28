package com.creatinaboys.repository;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of IAthleteRepository.
 * Its only responsibility is storing athletes (SRP): changing to files or to a
 * database only requires another implementation of the interface (OCP, DIP).
 */
public class AthleteRepository implements IAthleteRepository {

    private final List<Athlete> athleteList = new ArrayList<>();

    @Override
    public boolean save(Athlete athlete) {
        if (athlete == null) {
            throw new IllegalArgumentException("athlete cannot be null.");
        }
        if (indexOf(athlete.getId()) >= 0) {
            return false;
        }
        return athleteList.add(athlete);
    }

    @Override
    public Athlete findById(String id) throws AthleteNotFoundException {
        int index = indexOf(id);
        if (index < 0) {
            throw new AthleteNotFoundException(id);
        }
        return athleteList.get(index);
    }

    @Override
    public List<Athlete> findAll() {
        return new ArrayList<>(athleteList);
    }

    @Override
    public boolean deleteById(String id) {
        int index = indexOf(id);
        if (index < 0) {
            return false;
        }
        athleteList.remove(index);
        return true;
    }

    @Override
    public boolean update(Athlete athlete) throws AthleteNotFoundException {
        int index = indexOf(athlete.getId());
        if (index < 0) {
            throw new AthleteNotFoundException(athlete.getId());
        }
        athleteList.set(index, athlete);
        return true;
    }

    private int indexOf(String id) {
        for (int i = 0; i < athleteList.size(); i++) {
            if (athleteList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}