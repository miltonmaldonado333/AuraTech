package com.creatinaboys.model;

import java.math.BigDecimal;
import java.util.List;

public record AgeCategory(String name, BigDecimal monthlyFee, List<Integer> ages) {

    public AgeCategory {
        ages = List.copyOf(ages);
    }
}