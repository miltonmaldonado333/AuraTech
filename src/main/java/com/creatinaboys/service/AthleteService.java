package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.validation.DataValidator;

import java.util.List;

public class AthleteService implements IAthlteService {

    private final IAthleteRepository repository;
    private final IBillingService billingService;
    private final AthleteUniquenessPolicy uniqueness = new AthleteUniquenessPolicy();

    public AthleteService(IAthleteRepository repository, IBillingService billingService) {
        this.repository = repository;
        this.billingService = billingService;
    }

    @Override
    public boolean registerAthlete(Athlete athlete) throws InvalidAgeException, ValidationException {
        // Formato de los datos (RNF8): ninguna pantalla puede saltarse estas reglas.
        // La edad menor de 5 la rechaza BillingService con InvalidAgeException.
        DataValidator.validateId(athlete.getId());
        DataValidator.validateFullName(athlete.getFullName());
        DataValidator.validateRequiredPhone(athlete.getPhone());
        DataValidator.parseJerseyNumber(String.valueOf(athlete.getJerseyNumber()));
        if (athlete.getAge() > DataValidator.MAX_AGE) {
            throw new ValidationException("La edad no puede ser mayor a " + DataValidator.MAX_AGE + " años.");
        }
        if (athlete instanceof Basketballer b) {
            DataValidator.parseHeight(String.valueOf(b.getHeight()));
        }

        uniqueness.check(athlete, repository.findAll(), null);

        athlete.setCategory(billingService.determineCategory(athlete.getAge()));
        athlete.setMonthlyFee(billingService.calculateFee(athlete.getAge()));
        return repository.save(athlete);
    }

    @Override
    public Athlete findAthlete(AthleteKey key) throws AthleteNotFoundException {
        return repository.findByKey(key);
    }

    @Override
    public List<Athlete> findAthletesById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Athlete> getAllAthletes() {
        return repository.findAll();
    }

    @Override
    public boolean existsByKey(AthleteKey key) {
        return repository.findAll().stream().anyMatch(a -> a.getKey().equals(key));
    }

    @Override
    public boolean deleteAthlete(AthleteKey key) {
        return repository.deleteByKey(key);
    }

    @Override
    public int getTotalAthletesCount() {
        return repository.findAll().size();
    }

    @Override
    public <T extends Athlete> long getAthletesCountByType(Class<T> type) {
        return repository.findAll().stream().filter(type::isInstance).count();
    }

    // ------------------------------------------------------------------ Estado y mensualidad

    @Override
    public void applyActivationPolicy(IActivationPolicy policy) {
        for (Athlete a : getAllAthletes()) {
            boolean should = policy.shouldBeActive(a);
            boolean blockedManually = a.isManualOverride() && !a.isActive();
            boolean target = should && !blockedManually;

            if (a.isActive() != target) {
                a.setActive(target);
                persist(a);
            }
        }
    }

    @Override
    public boolean toggleAthleteStatus(AthleteKey key) {
        try {
            Athlete a = repository.findByKey(key);
            if (a.isActive()) {
                a.setActiveManually(false);
            } else if (a.isMembershipExpired(0)) {
                a.renewMembership(1);
                a.clearManualOverride();
                a.setActive(true);
            } else {
                a.clearManualOverride();
                a.setActive(true);
            }
            return repository.update(a);
        } catch (AthleteNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean setAthleteActive(AthleteKey key, boolean active) {
        try {
            Athlete a = repository.findByKey(key);
            a.setActiveManually(active);
            return repository.update(a);
        } catch (AthleteNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean renewMembership(AthleteKey key, int months) {
        try {
            Athlete a = repository.findByKey(key);
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
            // Fue eliminado mientras se evaluaba; no hay nada que guardar.
        }
    }
}