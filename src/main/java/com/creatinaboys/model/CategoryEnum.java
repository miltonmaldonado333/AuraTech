package com.creatinaboys.model;

/**
 * Age categories of the club. It only defines the constants (SRP);
 * the age ranges and fees are handled by BillingService (Information Expert).
 */
public enum CategoryEnum {
    CHILDREN("Children"),
    YOUTH("Youth"),
    ADULTS("Adults");

    private final String name;

    CategoryEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}