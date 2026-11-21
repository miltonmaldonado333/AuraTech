package com.creatinaboys.service;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.model.AgeCategory;
import com.creatinaboys.model.CategoryEnum;

public interface IBillingService {

    double calculateFee(int age) throws InvalidAgeException;

    CategoryEnum determineCategory(int age) throws InvalidAgeException;

    AgeCategory determineEnrollmentCategory(int age) throws InvalidAgeException;

    /**
     * Todas las categorías del catálogo cuyo rango incluye la edad. Si hay más de una,
     * la persona debe elegir en cuál quiere estar.
     */
    java.util.List<AgeCategory> findEnrollmentCategories(int age) throws InvalidAgeException;
}