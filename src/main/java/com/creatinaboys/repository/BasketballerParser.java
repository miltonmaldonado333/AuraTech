package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.CategoryEnum;

public class BasketballerParser implements AthleteParser {

    private static final String TYPE = "BASKETBALL";
    private static final int FIELD_COUNT = 9;

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
                TextCodec.escape(basketballer.getId()),
                TextCodec.escape(basketballer.getFullName()),
                String.valueOf(basketballer.getAge()),
                TextCodec.escape(basketballer.getPhone()),
                String.valueOf(basketballer.getJerseyNumber()),
                String.valueOf(basketballer.getHeight()),
                category,
                String.valueOf(basketballer.getMonthlyFee())
        );
    }

    @Override
    public Athlete fromTextFields(String[] fields) {
        try {
            TextCodec.requireFields(fields, FIELD_COUNT, TYPE);
            int age = Integer.parseInt(fields[3].trim());
            int jerseyNumber = Integer.parseInt(fields[5].trim());
            double height = Double.parseDouble(fields[6].trim());
            Basketballer basketballer = new Basketballer(
                    TextCodec.unescape(fields[1]),
                    TextCodec.unescape(fields[2]),
                    age,
                    TextCodec.unescape(fields[4]),
                    jerseyNumber,
                    height);

            if (!fields[7].isBlank()) {
                basketballer.setCategory(CategoryEnum.valueOf(fields[7].trim()));
            }
            basketballer.setMonthlyFee(Double.parseDouble(fields[8].trim()));
            return basketballer;
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Error parsing Basketballer data: " + e.getMessage(), e);
        }
    }
}
