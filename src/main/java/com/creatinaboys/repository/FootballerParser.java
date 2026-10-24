package com.creatinaboys.repository.parser;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Footballer;

public class FootballerParser implements AthleteParser {

    private static final String TYPE = "FOOTBALL";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toTextLine(Athlete athlete) {
        Footballer footballer = (Footballer) athlete;
        String category = footballer.getCategory() == null ? "" : footballer.getCategory().name();
        
        return String.join(";",
                TYPE,
                footballer.getId(),
                footballer.getFullName(),
                String.valueOf(footballer.getAge()),
                footballer.getPhone(),
                String.valueOf(footballer.getJerseyNumber()),
                footballer.getPlayingPosition(),
                category,
                String.valueOf(footballer.getMonthlyFee())
        );
    }

    @Override
    public Athlete fromTextFields(String[] fields) {
        try {
            int age = Integer.parseInt(fields[3]);
            int jerseyNumber = Integer.parseInt(fields[5]);
            Footballer footballer = new Footballer(fields[1], fields[2], age, fields[4], jerseyNumber, fields[6]);
            
            if (!fields[7].isBlank()) {
                footballer.setCategory(CategoryEnum.valueOf(fields[7]));
            }
            footballer.setMonthlyFee(Double.parseDouble(fields[8]));
            return footballer;
        } catch (Exception e) {
            throw new PersistenceException("Error parsing Footballer data: " + e.getMessage(), e);
        }
    }
}