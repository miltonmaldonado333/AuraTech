package com.creatinaboys.model;

public class Basketballer extends Athlete {
    private double height;

    public Basketballer(String id, String fullName, int age, String phone, int jerseyNumber, double height) {
        super(id, fullName, age, phone, jerseyNumber);
        this.height = height;
    }

    @Override
    public void calculateFee() {
        // Implementation will be handled by BillingService or custom rule
    }

    public double getHeight() { return height; }
}
