package com.creatinaboys.model;

public class Footballer extends Athlete {
    private String playingPosition;

    public Footballer(String id, String fullName, int age, String phone, int jerseyNumber, String playingPosition) {
        super(id, fullName, age, phone, jerseyNumber);
        this.playingPosition = playingPosition;
    }

    @Override
    public void calculateFee() {
        // Implementation will be handled by BillingService or custom rule
    }

    public String getPlayingPosition() { return playingPosition; }
}
