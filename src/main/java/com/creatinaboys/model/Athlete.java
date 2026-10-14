package com.creatinaboys.model;

public abstract class Athlete {
    private String id;
    private String fullName;
    private int age;
    private String phone;
    private int jerseyNumber;
    private double monthlyFee;
    private CategoryEnum category;

    public Athlete(String id, String fullName, int age, String phone, int jerseyNumber) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
        this.phone = phone;
        this.jerseyNumber = jerseyNumber;
    }

    public abstract void calculateFee();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
        this.phone = phone;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
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

    
  
}
