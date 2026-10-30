package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Footballer;

public class FootballerParser implements AthleteParser {

    private static final String TYPE = "FOOTBALL";
    private static final int FIELD_COUNT = 9;

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
                TextCodec.escape(footballer.getId()),
                TextCodec.escape(footballer.getFullName()),
                String.valueOf(footballer.getAge()),
                TextCodec.escape(footballer.getPhone()),
                String.valueOf(footballer.getJerseyNumber()),
                TextCodec.escape(footballer.getPlayingPosition()),
                category,
                String.valueOf(footballer.getMonthlyFee())
        );
    }

    @Override
    public Athlete fromTextFields(String[] fields) {
        try {
            TextCodec.requireFields(fields, FIELD_COUNT, TYPE);
            int age = Integer.parseInt(fields[3].trim());
            int jerseyNumber = Integer.parseInt(fields[5].trim());
            Footballer footballer = new Footballer(
                    TextCodec.unescape(fields[1]),
                    TextCodec.unescape(fields[2]),
                    age,
                    TextCodec.unescape(fields[4]),
                    jerseyNumber,
                    TextCodec.unescape(fields[6]));

            if (!fields[7].isBlank()) {
                footballer.setCategory(CategoryEnum.valueOf(fields[7].trim()));
            }
            footballer.setMonthlyFee(Double.parseDouble(fields[8].trim()));
            return footballer;
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Error parsing Footballer data: " + e.getMessage(), e);
        }
    }
}
