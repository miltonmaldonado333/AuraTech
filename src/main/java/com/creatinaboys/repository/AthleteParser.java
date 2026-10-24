package com.creatinaboys.repository.parser;

import com.creatinaboys.model.Athlete;

public interface AthleteParser {

    String getType();

    String toTextLine(Athlete athlete);

    Athlete fromTextFields(String[] fields);
}
