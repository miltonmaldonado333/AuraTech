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

    public <T extends Athlete> long getAthletesCountByType(Class<T> type) {
        return repository.findAll().stream()
                .filter(type::isInstance)
                .count();
    }

    // ------------------------------------------------------------------ Estado y mensualidad

    /** Activa o desactiva a mano y bloquea la política automática para ese deportista. */
    public boolean setAthleteActive(String id, boolean active) {
        try {
            Athlete a = repository.findById(id);
            a.setActiveManually(active);
            return repository.update(a);
        } catch (AthleteNotFoundException e) {
            return false;
        }
    }

    /** Recalcula el estado según la política, salvo los fijados a mano. Solo guarda si hubo cambio. */
    public void applyActivationPolicy(IActivationPolicy policy) {
        for (Athlete a : getAllAthletes()) {
            if (a.isManualOverride()) {
                continue;
            }
            boolean should = policy.shouldBeActive(a);
            if (a.isActive() != should) {
                a.setActive(should);
                persist(a);
            }
        }
    }

    /** Extiende la mensualidad y reactiva al deportista. Lo llamará el módulo de Pagos. */
    public boolean renewMembership(String id, int months) {
        try {
            Athlete a = repository.findById(id);
            a.renewMembership(months);
            a.clearManualOverride();
            a.setActive(!a.isMembershipExpired(0));
            return repository.update(a);
        } catch (AthleteNotFoundException e) {
            return false;
        }
    }

    private void persist(Athlete a) {
        try {
            repository.update(a);
        } catch (AthleteNotFoundException e) {
            // El deportista fue eliminado mientras se evaluaba; no hay nada que guardar.
        }
    }
}