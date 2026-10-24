package com.creatinaboys.model;

import com.creatinaboys.service.IBillingService;

/**
 * Base entity for every athlete registered in the club. Concrete subclasses
 * represent a specific sports discipline (OCP, LSP).
 */
public abstract class Athlete {

    private String id;
    private String fullName;
    private int age;
    private String phone;
    private int jerseyNumber;
    private double monthlyFee;
    private CategoryEnum category;

    protected Athlete(String id, String fullName, int age, String phone, int jerseyNumber) {
        this.id = requireText(id, "id");
        this.fullName = requireText(fullName, "fullName");
        this.age = age;
        this.phone = requireText(phone, "phone");
        if (jerseyNumber < 0) {
            throw new IllegalArgumentException("jerseyNumber cannot be negative.");
        }
        this.jerseyNumber = jerseyNumber;
    }

    public abstract void calculateFee(IBillingService billingService);

    /**
     * Name of the sports discipline (e.g. "Football", "Basketball").
     */
    public abstract String getDiscipline();

    protected static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = requireText(id, "id");
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = requireText(fullName, "fullName");
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = requireText(phone, "phone");
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        if (jerseyNumber < 0) {
            throw new IllegalArgumentException("jerseyNumber cannot be negative.");
        }
        this.jerseyNumber = jerseyNumber;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return getDiscipline() + "{id='" + id + "', fullName='" + fullName + "', age=" + age
                + ", category=" + category + ", monthlyFee=" + monthlyFee + "}";
    }
}
