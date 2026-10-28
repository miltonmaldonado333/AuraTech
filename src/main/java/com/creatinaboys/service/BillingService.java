package com.creatinaboys.service;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.model.CategoryEnum;

public class BillingService implements IBillingService {

    @Override
    public CategoryEnum determineCategory(int age) throws InvalidAgeException {
        if (age >= 5 && age <= 12) {
            return CategoryEnum.CHILDREN;
        } else if (age >= 13 && age <= 17) {
            return CategoryEnum.YOUTH;
        } else if (age >= 18) {
            return CategoryEnum.ADULTS;
        } else {
            throw new InvalidAgeException(age);
        }
    }

    @Override
    public double calculateFee(int age) throws InvalidAgeException {
        CategoryEnum category = determineCategory(age);

        return switch (category) {
            case CHILDREN -> 50000.0;
            case YOUTH    -> 70000.0;
            case ADULTS   -> 100000.0;
        };
    }
}