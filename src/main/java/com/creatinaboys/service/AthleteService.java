package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.repository.IAthleteRepository;

import java.util.List;

public class AthleteService {

    private final IAthleteRepository repository;
    private final IBillingService billingService;

    public AthleteService(IAthleteRepository repository, IBillingService billingService) {
        this.repository = repository;
        this.billingService = billingService;
    }

    /**
     * Un único método de registro para cualquier Atleta (Cumple OCP, DIP y
     * SRP). El objeto Atleta ya llega instanciado desde el controlador o la UI.
     * @param athlete
     * @return 
     * @throws com.creatinaboys.exceptions.InvalidAgeException
     */
    public boolean registerAthlete(Athlete athlete) throws InvalidAgeException {
        athlete.setCategory(billingService.determineCategory(athlete.getAge()));
        athlete.setMonthlyFee(billingService.calculateFee(athlete.getAge()));
        return repository.save(athlete);
    }

    public Athlete findAthlete(String id) throws AthleteNotFoundException {
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

    /**
     * Reemplaza a getFootballersCount() y getBasketballersCount() Permite
     * contar cualquier clase de Atleta sin modificar este código cuando
     * agregues más deportes.
     * @param <T>
     * @param type
     * @return 
     */
    public <T extends Athlete> long getAthletesCountByType(Class<T> type) {
        return repository.findAll().stream()
                .filter(type::isInstance)
                .count();
    }
}
