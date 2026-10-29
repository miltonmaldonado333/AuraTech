package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Payment;

import java.util.List;
import java.util.Map;

public interface ISaleService {

    boolean processMonthlyPayment(AthleteKey key, double amount) throws AthleteNotFoundException;

    double getPendingBalance(AthleteKey key) throws AthleteNotFoundException;

    double calculateEstimatedMonthlyRevenue();

    double calculateTotalCollectedRevenue();

    Map<String, Double> getRevenueByDiscipline();

    Map<CategoryEnum, Double> getRevenueByCategory();

    List<Athlete> getAthletesWithPendingPayment();

    /** Total pagado en el mes actual por cada registro (documento + disciplina). */
    Map<AthleteKey, Double> getPaidThisMonthByAthlete();

    List<Payment> getPaymentHistory();
}