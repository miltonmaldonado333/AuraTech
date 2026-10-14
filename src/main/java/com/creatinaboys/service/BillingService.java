package com.creatinaboys.service;

import com.creatinaboys.model.CategoryEnum;

public class BillingService {
    public double calculateFee(int age) {
        if (age <= 12) return 50000.0;
        if (age <= 17) return 70000.0;
        return 100000.0;
    }

    public CategoryEnum determineCategory(int age) {
        if (age <= 12) return CategoryEnum.CHILDREN;
        if (age <= 17) return CategoryEnum.YOUTH;
        return CategoryEnum.ADULTS;
    }
}
