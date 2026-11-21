package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.CategoryEnum;

import java.util.List;

public interface IAthlteService {

    boolean registerAthlete(Athlete athlete) throws InvalidAgeException, ValidationException;

    /**
     * Registra eligiendo la categoría cuando la edad cae en varias.
     * @param categoryName nombre elegido; puede ser null si la edad solo tiene una categoría
     */
    default boolean registerAthlete(Athlete athlete, String categoryName)
            throws InvalidAgeException, ValidationException {
        return registerAthlete(athlete);
    }

    /** Categorías disponibles para esa edad (más de una = hay que preguntar). */
    default List<com.creatinaboys.model.AgeCategory> categoryOptionsForAge(int age)
            throws InvalidAgeException {
        throw new UnsupportedOperationException("categoryOptionsForAge no implementado");
    }

    /** Igual que updateAthleteData, pero con la categoría elegida si la nueva edad cae en varias. */
    default boolean updateAthleteData(AthleteKey key, String fullName, int age, String phone,
                                      int jerseyNumber, String specific, String categoryName)
            throws AthleteNotFoundException, InvalidAgeException, ValidationException {
        return updateAthleteData(key, fullName, age, phone, jerseyNumber, specific);
    }

    Athlete findAthlete(AthleteKey key) throws AthleteNotFoundException;
    List<Athlete> findAthletesById(String id);
    List<Athlete> getAllAthletes();
    boolean existsByKey(AthleteKey key);
    int getTotalAthletesCount();
    <T extends Athlete> long getAthletesCountByType(Class<T> type);

    /**
     * RF1: edita todos los datos de un deportista (nombre, edad, teléfono, dorsal y
     * posición/estatura). El documento y la disciplina identifican el registro y no cambian.
     * Nombre, edad y teléfono se aplican a todas las disciplinas de la misma persona.
     * @param specific posición de juego (fútbol) o estatura en metros (baloncesto)
     */
    boolean updateAthleteData(AthleteKey key, String fullName, int age, String phone,
                              int jerseyNumber, String specific)
            throws AthleteNotFoundException, InvalidAgeException, ValidationException;

    /** Categoría que le correspondería a esa edad (para avisar antes de guardar). */
    default CategoryEnum categoryForAge(int age) throws InvalidAgeException {
        throw new UnsupportedOperationException("categoryForAge no implementado");
    }

    /** Mensualidad que le correspondería a esa edad (para avisar antes de guardar). */
    default double feeForAge(int age) throws InvalidAgeException {
        throw new UnsupportedOperationException("feeForAge no implementado");
    }

    boolean deleteAthlete(AthleteKey key);
    boolean toggleAthleteStatus(AthleteKey key);
    boolean setAthleteActive(AthleteKey key, boolean active);
    boolean renewMembership(AthleteKey key, int months);
    void applyActivationPolicy(IActivationPolicy policy);
}