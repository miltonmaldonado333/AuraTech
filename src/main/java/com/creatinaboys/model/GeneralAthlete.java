package com.creatinaboys.model;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.service.IBillingService;

/** An athlete in a discipline that uses only the shared enrollment fields. */
public class GeneralAthlete extends Athlete {

    private final String discipline;

    public GeneralAthlete(String id, String fullName, int age, String phone,
                          int jerseyNumber, String discipline) {
        super(id, fullName, age, phone, jerseyNumber);
        this.discipline = requireText(discipline, "discipline");
    }

    @Override
    public String getDiscipline() {
        return discipline;
    }

    @Override
    public void calculateFee(IBillingService billingService) throws InvalidAgeException {
        AgeCategory category = billingService.determineEnrollmentCategory(getAge());
        setCategoryName(category.name());
        setMonthlyFee(category.monthlyFee().doubleValue());
    }
}
