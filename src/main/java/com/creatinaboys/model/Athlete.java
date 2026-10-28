package com.creatinaboys.model;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.service.IBillingService;

import java.util.Objects;

/**
 * Base entity for every athlete registered in the club. Concrete subclasses
 * represent a specific sports discipline (OCP, LSP).
 */
public abstract class Athlete {

    /** Edad máxima aceptada por el modelo (la edad mínima del club la valida BillingService). */
    public static final int MAX_AGE = 120;

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
        this.age = requireValidAge(age);
        this.phone = requireText(phone, "phone");
        if (jerseyNumber < 0) {
            throw new IllegalArgumentException("jerseyNumber cannot be negative.");
        }
        this.jerseyNumber = jerseyNumber;
    }

    /**
     * Assigns the category and the monthly fee of the athlete. The pricing
     * rules are not here: they are delegated to the billing abstraction (DIP),
     * whose expert is BillingService (GRASP Information Expert). Each
     * discipline overrides it (polymorphism).
     * @param billingService
     * @throws com.creatinaboys.exceptions.InvalidAgeException
     */
    public abstract void calculateFee(IBillingService billingService) throws InvalidAgeException;

    /**
     * Name of the sports discipline (e.g. "Football", "Basketball").
     * @return 
     */
    public abstract String getDiscipline();

    protected static int requireValidAge(int age) {
        if (age <= 0 || age > MAX_AGE) {
            throw new IllegalArgumentException("age must be between 1 and " + MAX_AGE + ".");
        }
        return age;
    }

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
        this.age = requireValidAge(age);
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
        if (Double.isNaN(monthlyFee) || Double.isInfinite(monthlyFee) || monthlyFee < 0) {
            throw new IllegalArgumentException("monthlyFee must be a non-negative number.");
        }
        this.monthlyFee = monthlyFee;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    /** Dos atletas son el mismo si comparten documento de identidad. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(id, ((Athlete) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getDiscipline() + "{id='" + id + "', fullName='" + fullName + "', age=" + age
                + ", category=" + category + ", monthlyFee=" + monthlyFee + "}";
    }
}
