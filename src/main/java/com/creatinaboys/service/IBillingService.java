package com.creatinaboys.service;

import com.creatinaboys.model.CategoryEnum;

public interface IBillingService {
    double calculateFee(int age);
    CategoryEnum determineCategory(int age);
}