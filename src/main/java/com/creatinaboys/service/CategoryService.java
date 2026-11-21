package com.creatinaboys.service;

import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.AgeCategory;
import com.creatinaboys.repository.ICategoryRepository;
import com.creatinaboys.validation.DataValidator;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;

public class CategoryService {

    private static final BigDecimal MAX_FEE = new BigDecimal("999999999999.99");

    private final ICategoryRepository repository;

    public CategoryService(ICategoryRepository repository) {
        this.repository = repository;
    }

    public List<AgeCategory> getCategories() {
        return repository.findAll();
    }

    public void createCategory(String name, int minimumAge, int maximumAge, BigDecimal monthlyFee)
            throws ValidationException {

        String normalizedName = name == null ? ""
                : Normalizer.normalize(name.strip(), Normalizer.Form.NFC).replaceAll(" +", " ");

        if (!normalizedName.matches("[\\p{L}\\p{M}\\p{N} ._'-]{1,40}")
                || !normalizedName.matches(".*[\\p{L}\\p{N}].*")) {
            throw new ValidationException(
                    "Enter a name with 1 to 40 characters, including a letter or number.");
        }

        if (minimumAge < DataValidator.MIN_AGE || maximumAge > DataValidator.MAX_AGE
                || minimumAge > maximumAge) {
            throw new ValidationException("Enter an age range from " + DataValidator.MIN_AGE
                    + " to " + DataValidator.MAX_AGE + ", with the minimum age first.");
        }

        if (monthlyFee == null || monthlyFee.signum() < 0 || monthlyFee.compareTo(MAX_FEE) > 0
                || monthlyFee.stripTrailingZeros().scale() > 2) {
            throw new ValidationException(
                    "Enter a valid monthly fee with at most two decimal places.");
        }

        if (!repository.create(normalizedName, monthlyFee.setScale(2), minimumAge, maximumAge)) {
            throw new ValidationException("A category with this name already exists.");
        }
    }
}