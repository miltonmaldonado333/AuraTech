package com.creatinaboys.service;

import com.creatinaboys.model.CategoryEnum;

public class BillingService implements IBillingService {

    @Override
    public double calculateFee(int age) {
        if (age >= 5 && age <= 12) {
            return 50000.0;
        } else if (age >= 13 && age <= 17) {
            return 70000.0;
        } else if (age >= 18) {
            return 100000.0;
        } else {
            throw new IllegalArgumentException("La edad mínima de ingreso al club es de 5 años.");
        }
    }

    @Override
    public CategoryEnum determineCategory(int age) {
        if (age >= 5 && age <= 12) {
            return CategoryEnum.CHILDREN;
        } else if (age >= 13 && age <= 17) {
            return CategoryEnum.YOUTH;
        } else if (age >= 18) {
            return CategoryEnum.ADULTS;
        } else {
            throw new IllegalArgumentException("La edad mínima de ingreso al club es de 5 años.");
        }
    }
}