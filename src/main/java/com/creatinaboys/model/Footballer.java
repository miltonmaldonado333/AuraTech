package com.creatinaboys.model;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.service.IBillingService;

/** Athlete enrolled in the football discipline. */
public class Footballer extends Athlete {
    private String playingPosition;

    public Footballer(String id, String fullName, int age, String phone, int jerseyNumber, String playingPosition) {
        super(id, fullName, age, phone, jerseyNumber);
        this.playingPosition = requireText(playingPosition, "playingPosition");
    }

    @Override
    public void calculateFee(IBillingService billingService) throws InvalidAgeException {
        setCategory(billingService.determineCategory(getAge()));
        setMonthlyFee(billingService.calculateFee(getAge()));
    }

    @Override
    public String getDiscipline() {
        return "Football";
    }

    public String getPlayingPosition() {
        return playingPosition;
    }

    public void setPlayingPosition(String playingPosition) {
        this.playingPosition = requireText(playingPosition, "playingPosition");
    }
}