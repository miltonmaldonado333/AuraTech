package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Footballer;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.validation.DataValidator;

import java.util.ArrayList;
import com.creatinaboys.model.AgeCategory;


import java.util.ArrayList;
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
        return registerAthlete(athlete, null);
    }

    @Override
    public boolean registerAthlete(Athlete athlete, String categoryName)
            throws InvalidAgeException, ValidationException {
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

        AgeCategory category = resolveCategory(athlete.getAge(), categoryName);
        athlete.setCategoryName(category.name());
        athlete.setMonthlyFee(category.monthlyFee().doubleValue());

        return repository.save(athlete);
    }

    @Override
    public boolean updateAthleteData(AthleteKey key, String fullName, int age, String phone,
            int jerseyNumber, String specific)
            throws AthleteNotFoundException, InvalidAgeException, ValidationException {
        return updateAthleteData(key, fullName, age, phone, jerseyNumber, specific, null);
    }

    @Override
    public boolean updateAthleteData(AthleteKey key, String fullName, int age, String phone,
            int jerseyNumber, String specific, String categoryName)
            throws AthleteNotFoundException, InvalidAgeException, ValidationException {
        // Mismas reglas de formato que el registro (RNF8).
        DataValidator.validateFullName(fullName);
        DataValidator.validateRequiredPhone(phone);
        DataValidator.parseJerseyNumber(String.valueOf(jerseyNumber));
        if (age < DataValidator.MIN_AGE) {
            throw new InvalidAgeException(age);
        }
        if (age > DataValidator.MAX_AGE) {
            throw new ValidationException("La edad no puede ser mayor a " + DataValidator.MAX_AGE + " años.");
        }

        Athlete target = repository.findByKey(key);
        String newName = fullName.trim();
        String newPhone = phone.trim();

        double newHeight = 0;
        String newPosition = null;
        Athlete candidate;
        if (target instanceof Basketballer) {
            newHeight = DataValidator.parseHeight(specific);
            candidate = new Basketballer(target.getId(), newName, age, newPhone, jerseyNumber, newHeight);
        } else {
            DataValidator.validateRequiredString(specific, "Posición de juego");
            newPosition = specific.trim();
            candidate = new Footballer(target.getId(), newName, age, newPhone, jerseyNumber, newPosition);
        }

        // Unicidad (dorsal por disciplina). Los otros registros de la misma persona se
        // excluyen porque se renombran junto con este.
        List<Athlete> others = repository.findAll().stream()
                .filter(a -> !a.getId().equals(target.getId()))
                .toList();
        uniqueness.check(candidate, others, null);

        // Nombre, edad y teléfono son de la persona: se aplican a todas sus disciplinas.
        // Se trabaja sobre los objetos de esta lista (con una BD cada consulta devuelve
        // objetos distintos, así que lo que se modifique debe ser lo que se guarda).
        List<Athlete> records = new ArrayList<>(repository.findById(target.getId()));
        Athlete mine = null;
        for (Athlete a : records) {
            if (a.getKey().equals(key)) {
                mine = a;
            }
        }
        if (mine == null) {
            mine = target;
            records.add(target);
        }

        // La categoría solo se recalcula si cambió la edad; si no, se conserva la actual
        // (y su tarifa), así editar un teléfono no reasigna a nadie de categoría.
        boolean ageChanged = age != target.getAge();
        AgeCategory newCategory = ageChanged ? resolveCategory(age, categoryName) : null;

        for (Athlete a : records) {
            a.setFullName(newName);
            a.setAge(age);
            a.setPhone(newPhone);
            if (newCategory != null) {
                a.setCategoryName(newCategory.name());
                a.setMonthlyFee(newCategory.monthlyFee().doubleValue());
            }
        }

        // Datos propios de este registro.
        mine.setJerseyNumber(jerseyNumber);
        if (mine instanceof Basketballer b) {
            b.setHeight(newHeight);
        } else if (mine instanceof Footballer f) {
            f.setPlayingPosition(newPosition);
        }

        // Cada registro de la persona se guarda con el update de siempre.
        boolean ok = true;
        for (Athlete a : records) {
            ok &= repository.update(a);
        }
        return ok;
    }

    @Override
    public List<AgeCategory> categoryOptionsForAge(int age) throws InvalidAgeException {
        return billingService.findEnrollmentCategories(age);
    }

    /**
     * Elige la categoría de la edad. Si hay una sola se usa; si hay varias, la persona
     * debe haber elegido una (categoryName). Nunca se adivina.
     */
    private AgeCategory resolveCategory(int age, String categoryName)
            throws InvalidAgeException, ValidationException {
        List<AgeCategory> options = billingService.findEnrollmentCategories(age);

        if (options.isEmpty()) {
            throw new com.creatinaboys.exceptions.PersistenceException(
                    "No category is configured for age " + age + ".");
        }

        if (categoryName != null && !categoryName.isBlank()) {
            return options.stream()
                    .filter(option -> option.name().equals(categoryName))
                    .findFirst()
                    .orElseThrow(() -> new ValidationException(
                            "La categoría \"" + categoryName + "\" no incluye la edad " + age + "."));
        }

        if (options.size() > 1) {
            throw new ValidationException("La edad " + age
                    + " pertenece a varias categorías. Elige en cuál desea estar.");
        }

        return options.get(0);
    }

    @Override
    public CategoryEnum categoryForAge(int age) throws InvalidAgeException {
        return billingService.determineCategory(age);
    }

    @Override
    public double feeForAge(int age) throws InvalidAgeException {
        return billingService.calculateFee(age);
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