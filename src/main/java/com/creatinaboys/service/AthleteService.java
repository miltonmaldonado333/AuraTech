package com.creatinaboys.service;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.Footballer;
import com.creatinaboys.repository.IAthleteRepository;

import java.util.List;

public class AthleteService {

    private final IAthleteRepository repository;
    private final IBillingService billingService;

    public AthleteService(IAthleteRepository repository, IBillingService billingService) {
        this.repository = repository;
        this.billingService = billingService;
    }

    public boolean registerFootballer(String id, String fullName, int age, String phone, int jerseyNumber, String playingPosition) {
        Footballer footballer = new Footballer(id, fullName, age, phone, jerseyNumber, playingPosition);
        footballer.setCategory(billingService.determineCategory(age));
        footballer.setMonthlyFee(billingService.calculateFee(age));
        return repository.save(footballer);
    }

    public boolean registerBasketballer(String id, String fullName, int age, String phone, int jerseyNumber, double height) {
        Basketballer basketballer = new Basketballer(id, fullName, age, phone, jerseyNumber, height);
        basketballer.setCategory(billingService.determineCategory(age));
        basketballer.setMonthlyFee(billingService.calculateFee(age));
        return repository.save(basketballer);
    }

    public Athlete findAthlete(String id) {
        return repository.findById(id);
    }

    public List<Athlete> getAllAthletes() {
        return repository.findAll();
    }

    public boolean deleteAthlete(String id) {
        return repository.deleteById(id);
    }

    public int getTotalAthletesCount() {
        return repository.findAll().size();
    }

    public long getFootballersCount() {
        return repository.findAll().stream()
                .filter(a -> a instanceof Footballer)
                .count();
    }

    public long getBasketballersCount() {
        return repository.findAll().stream()
                .filter(a -> a instanceof Basketballer)
                .count();
    }
}