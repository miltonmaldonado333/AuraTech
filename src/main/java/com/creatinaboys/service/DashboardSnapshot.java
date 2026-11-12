
package com.creatinaboys.service;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.Payment;

import java.util.List;
import java.util.Map;

public record DashboardSnapshot(
        List<Athlete> athletes,
        long active,
        int coaches,
        Map<AthleteKey, Double> paidMap,
        double collected,
        double expected,
        List<Athlete> pending,
        List<Payment> history,
        Map<String, String> names
) {
}
