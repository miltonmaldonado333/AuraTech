package com.creatinaboys.repository;

import com.creatinaboys.model.Athlete;

public interface AthleteParser {
    String getType();
    String toTextLine(Athlete athlete);
    Athlete fromTextFields(String[] fields);
}