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

    /**
     * Recalcula el estado de todos según la política (vencimiento).
     * - Si la política dice "inactivo", queda inactivo siempre (aunque se haya activado a mano).
     * - Si un deportista fue desactivado a mano, sigue inactivo hasta que se reactive o renueve.
     * Solo escribe el archivo cuando algo cambió.
     */
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

    /** Botón Activar / Desactivar. */
    public boolean toggleAthleteStatus(String id) {
        try {
            Athlete a = repository.findById(id);
            if (a.isActive()) {
                a.setActiveManually(false);          // desactivado a mano
            } else if (a.isMembershipExpired(0)) {
                a.renewMembership(1);                // vencido: renueva 1 mes
                a.clearManualOverride();
                a.setActive(true);
            } else {
                a.clearManualOverride();             // vigente: solo reactiva
                a.setActive(true);
            }
            return repository.update(a);
        } catch (AthleteNotFoundException e) {
            return false;
        }
    }

    /** Activa o desactiva a mano (se conserva por compatibilidad). */
    public boolean setAthleteActive(String id, boolean active) {
        try {
            Athlete a = repository.findById(id);
            a.setActiveManually(active);
            return repository.update(a);
        } catch (AthleteNotFoundException e) {
            return false;
        }
    }

    /** Extiende la mensualidad y reactiva. Lo llamará el módulo de Pagos. */
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
            // Fue eliminado mientras se evaluaba; no hay nada que guardar.
        }
    }
}