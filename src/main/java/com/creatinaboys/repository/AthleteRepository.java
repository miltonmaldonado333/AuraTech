package com.creatinaboys.repository;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of IAthleteRepository.
 * Its only responsibility is storing athletes (SRP): changing to files or to a
 * database only requires another implementation of the interface (OCP, DIP).
 * A record is identified by its AthleteKey (document + discipline).
 */
public class AthleteRepository implements IAthleteRepository {

    private final List<Athlete> athleteList = new ArrayList<>();

    @Override
    public boolean save(Athlete athlete) {
        if (athlete == null) {
            throw new IllegalArgumentException("athlete cannot be null.");
        }
        if (indexOf(athlete.getKey()) >= 0) {
            return false;
        }
        return athleteList.add(athlete);
    }

    @Override
    public Athlete findByKey(AthleteKey key) throws AthleteNotFoundException {
        int index = indexOf(key);
        if (index < 0) {
            throw new AthleteNotFoundException(key.toString());
        }
        return athleteList.get(index);
    }

    /** Todos los registros de una misma persona (uno por disciplina). */
    @Override
    public List<Athlete> findById(String id) {
        List<Athlete> result = new ArrayList<>();
        for (Athlete a : athleteList) {
            if (a.getId().equals(id)) {
                result.add(a);
            }
        }
        return result;
    }

    @Override
    public List<Athlete> findAll() {
        return new ArrayList<>(athleteList);
    }

    @Override
    public boolean deleteByKey(AthleteKey key) {
        int index = indexOf(key);
        if (index < 0) {
            return false;
        }
        athleteList.remove(index);
        return true;
    }

    @Override
    public boolean update(Athlete athlete) throws AthleteNotFoundException {
        if (athlete == null) {
            throw new IllegalArgumentException("athlete cannot be null.");
        }
        int index = indexOf(athlete.getKey());
        if (index < 0) {
            throw new AthleteNotFoundException(athlete.getKey().toString());
        }
        athleteList.set(index, athlete);
        return true;
    }

    private int indexOf(AthleteKey key) {
        for (int i = 0; i < athleteList.size(); i++) {
            if (athleteList.get(i).getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }
}