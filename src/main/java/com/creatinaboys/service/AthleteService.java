package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.repository.IAthleteRepository;

import java.util.List;
import java.util.Objects;

public class AthleteService {

    private final IAthleteRepository repository;
    private final IBillingService billingService;

    public AthleteService(IAthleteRepository repository, IBillingService billingService) {
        this.repository = Objects.requireNonNull(repository, "El repositorio de atletas no puede ser nulo.");
        this.billingService = Objects.requireNonNull(billingService, "El servicio de facturación no puede ser nulo.");
    }

    /**
     * Un único método de registro para cualquier Atleta (Cumple OCP, DIP y
     * SRP). El objeto Atleta ya llega instanciado desde el controlador o la UI.
     * @param athlete
     * @return {@code true} si se registró; {@code false} si ya existe un atleta con ese documento.
     * @throws com.creatinaboys.exceptions.InvalidAgeException si la edad es menor a 5 años.
     * @throws IllegalArgumentException si el atleta es nulo.
     */
    public boolean registerAthlete(Athlete athlete) throws InvalidAgeException {
        if (athlete == null) {
            throw new IllegalArgumentException("El atleta a registrar no puede ser nulo.");
        }
        athlete.calculateFee(billingService); // asigna categoría y tarifa según la disciplina
        return repository.save(athlete);
    }

    public Athlete findAthlete(String id) throws AthleteNotFoundException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento del atleta es obligatorio.");
        }
        return repository.findById(id.trim());
    }

    public List<Athlete> getAllAthletes() {
        return repository.findAll();
    }

    public boolean deleteAthlete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento del atleta es obligatorio.");
        }
        return repository.deleteById(id.trim());
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