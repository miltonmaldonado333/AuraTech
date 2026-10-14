package com.creatinaboys.repository;

import com.creatinaboys.model.Athlete;
import java.util.ArrayList;
import java.util.List;

public class AthleteRepository {
    private List<Athlete> athleteList = new ArrayList<>();

    public boolean register(Athlete athlete) {
        return athleteList.add(athlete);
    }

    public Athlete find(String id) {
        return athleteList.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Athlete> findAll() {
        return athleteList;
    }

    public boolean delete(String id) {
        return athleteList.removeIf(a -> a.getId().equals(id));
    }
}
