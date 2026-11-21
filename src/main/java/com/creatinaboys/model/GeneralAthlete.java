package com.creatinaboys.model;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.service.IBillingService;

/** Deportista de una disciplina sin atributos propios (por ejemplo, Beisbol). */
public class GeneralAthlete extends Athlete {

    private final String discipline;

    public GeneralAthlete(String id, String fullName, int age, String phone,
                          int jerseyNumber, String discipline) {
        super(id, fullName, age, phone, jerseyNumber);
        this.discipline = requireText(discipline, "discipline");
    }

    @Override
    public void calculateFee(IBillingService billingService) throws InvalidAgeException {
        setCategory(billingService.determineCategory(getAge()));
        setMonthlyFee(billingService.calculateFee(getAge()));
    }

    @Override
    public String getDiscipline() {
        return discipline;
    }
}