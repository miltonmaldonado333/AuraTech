package com.creatinaboys.repository;

import com.creatinaboys.model.AgeCategory;
import java.math.BigDecimal;
import java.util.List;

public interface ICategoryRepository {

    List<AgeCategory> findAll();

    boolean create(String name, BigDecimal monthlyFee, int minimumAge, int maximumAge);
}