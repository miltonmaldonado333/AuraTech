package com.creatinaboys.repository.parser;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.CategoryEnum;

public class BasketballerParser implements AthleteParser {

    private static final String TYPE = "BASKETBALL";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toTextLine(Athlete athlete) {
        Basketballer basketballer = (Basketballer) athlete;
        String category = basketballer.getCategory() == null ? "" : basketballer.getCategory().name();
        
        return String.join(";",
                TYPE,
                basketballer.getId(),
                basketballer.getFullName(),
                String.valueOf(basketballer.getAge()),
                basketballer.getPhone(),
                String.valueOf(basketballer.getJerseyNumber()),
                String.valueOf(basketballer.getHeight()),
                category,
                String.valueOf(basketballer.getMonthlyFee())
        );
    }

    @Override
    public Athlete fromTextFields(String[] fields) {
        try {
            int age = Integer.parseInt(fields[3]);
            int jerseyNumber = Integer.parseInt(fields[5]);
            double height = Double.parseDouble(fields[6]);
            Basketballer basketballer = new Basketballer(fields[1], fields[2], age, fields[4], jerseyNumber, height);

            if (!fields[7].isBlank()) {
                basketballer.setCategory(CategoryEnum.valueOf(fields[7]));
            }
            basketballer.setMonthlyFee(Double.parseDouble(fields[8]));
            return basketballer;
        } catch (Exception e) {
            throw new PersistenceException("Error parsing Basketballer data: " + e.getMessage(), e);
        }
    }
}