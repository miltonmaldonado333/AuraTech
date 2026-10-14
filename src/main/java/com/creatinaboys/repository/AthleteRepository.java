package com.creatinaboys.repository;

import com.creatinaboys.model.Athlete;
import java.util.List;

public interface AthleteRepository {
    boolean save(Athlete athlete);
    Athlete findById(String id);
    List<Athlete> findAll();
    boolean deleteById(String id);
    boolean update(Athlete athlete);
}