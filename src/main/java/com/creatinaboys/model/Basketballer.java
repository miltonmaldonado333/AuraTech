package com.creatinaboys.model;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.service.IBillingService;

/** Athlete enrolled in the basketball discipline. Height is expressed in meters. */
public class Basketballer extends Athlete {
    private double height;

    public Basketballer(String id, String fullName, int age, String phone, int jerseyNumber, double height) {
        super(id, fullName, age, phone, jerseyNumber);
        this.height = requireValidHeight(height);
    }

    private static double requireValidHeight(double height) {
        if (Double.isNaN(height) || height <= 0 || height > 2.5) {
            throw new IllegalArgumentException("height must be between 0 and 2.5 meters.");
        }
        return height;
    }

    @Override
    public void calculateFee(IBillingService billingService) throws InvalidAgeException {
        setCategory(billingService.determineCategory(getAge()));
        setMonthlyFee(billingService.calculateFee(getAge()));
    }

    @Override
    public String getDiscipline() {
        return "Basketball";
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = requireValidHeight(height);
    }
}