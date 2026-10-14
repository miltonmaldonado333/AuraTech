package com.creatinaboys.service;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.repository.AthleteRepository;
import java.util.List;

public class AthleteService {
    private AthleteRepository repository;
    private BillingService billingService;

    public AthleteService(AthleteRepository repository, BillingService billingService) {
        this.repository = repository;
        this.billingService = billingService;
    }

    public boolean registerAthlete(Athlete athlete) {
        return repository.register(athlete);
    }

    public Athlete findAthlete(String id) {
        return repository.find(id);
    }

    public List<Athlete> getAllAthletes() {
        return repository.findAll();
    }

    public boolean deleteAthlete(String id) {
        return repository.delete(id);
    }
}
