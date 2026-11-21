package com.creatinaboys.service;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.AgeCategory;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.validation.DataValidator;

public class BillingService implements IBillingService {

    private final CategoryService categoryService;

    public BillingService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public AgeCategory determineEnrollmentCategory(int age) throws InvalidAgeException {
        if (age < DataValidator.MIN_AGE || age > DataValidator.MAX_AGE) {
            throw new InvalidAgeException(age);
        }

        return categoryService.getCategories().stream()
                .filter(category -> category.ages().contains(age))
                .findFirst()
                .orElseThrow(() -> new PersistenceException(
                        "No category is configured for age " + age + "."));
    }

    @Override
    public java.util.List<AgeCategory> findEnrollmentCategories(int age) throws InvalidAgeException {
        if (age < DataValidator.MIN_AGE || age > DataValidator.MAX_AGE) {
            throw new InvalidAgeException(age);
        }

        return categoryService.getCategories().stream()
                .filter(category -> category.ages().contains(age))
                .toList();
    }

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
            case YOUTH -> 70000.0;
            case ADULTS -> 100000.0;
        };
    }
}