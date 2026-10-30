package com.creatinaboys.service;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.model.CategoryEnum;

public interface IBillingService {

    double calculateFee(int age) throws InvalidAgeException;

    CategoryEnum determineCategory(int age) throws InvalidAgeException;
}
