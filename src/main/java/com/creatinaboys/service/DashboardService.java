
package com.creatinaboys.service;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.Payment;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.IPaymentRepository;

import java.time.Clock;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DashboardService implements IDashBoardService {

    private static final double EPSILON = 0.005;

    private final IAthleteRepository athleteRepository;
    private final IPaymentRepository paymentRepository;
    private final ICoachService coachService;
    private final Clock clock;

    public DashboardService(
            IAthleteRepository athleteRepository,
            IPaymentRepository paymentRepository,
            ICoachService coachService) {

        this(
                athleteRepository,
                paymentRepository,
                coachService,
                Clock.systemDefaultZone()
        );
    }

    public DashboardService(
            IAthleteRepository athleteRepository,
            IPaymentRepository paymentRepository,
            ICoachService coachService,
            Clock clock) {

        this.athleteRepository = Objects.requireNonNull(
                athleteRepository);

        this.paymentRepository = Objects.requireNonNull(
                paymentRepository);

        this.coachService = Objects.requireNonNull(
                coachService);

        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public DashboardSnapshot loadDashboard() {

        List<Athlete> athletes =
                athleteRepository.findAll();

        List<Payment> payments =
                paymentRepository.findAll();

        int coaches =
                coachService.getTotalCoachesCount();

        YearMonth currentMonth = YearMonth.now(clock);

        Map<AthleteKey, Double> paidMap =
                calculateMonthlyPayments(
                        athletes, payments, currentMonth);

        long active = athletes.stream()
                .filter(Athlete::isActive)
                .count();

        double expected = athletes.stream()
                .mapToDouble(Athlete::getMonthlyFee)
                .sum();

        double collected = paidMap.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        List<Athlete> pending = athletes.stream()
                .filter(athlete ->
                        paidMap.getOrDefault(
                                athlete.getKey(), 0.0
                        ) + EPSILON < athlete.getMonthlyFee()
                )
                .collect(Collectors.toList());

        List<Payment> history =
                new ArrayList<>(payments);

        history.sort(
                Comparator.comparing(
                        Payment::getPaymentDate
                ).reversed()
        );

        Map<String, String> names = athletes.stream()
                .collect(Collectors.toMap(
                        Athlete::getId,
                        Athlete::getFullName,
                        (first, second) -> first
                ));

        return new DashboardSnapshot(
                List.copyOf(athletes),
                active,
                coaches,
                Map.copyOf(paidMap),
                collected,
                expected,
                List.copyOf(pending),
                List.copyOf(history),
                Map.copyOf(names)
        );
    }

    private Map<AthleteKey, Double> calculateMonthlyPayments(
            List<Athlete> athletes,
            List<Payment> payments,
            YearMonth currentMonth) {

        Map<String, Double> legacyPayments =
                new HashMap<>();

        Map<AthleteKey, Double> disciplinePayments =
                new HashMap<>();

        for (Payment payment : payments) {

            if (!currentMonth.equals(payment.getPeriod())) {
                continue;
            }

            if (payment.getDiscipline().isEmpty()) {

                legacyPayments.merge(
                        payment.getAthleteId(),
                        payment.getAmount(),
                        Double::sum
                );

            } else {

                AthleteKey key = new AthleteKey(
                        payment.getAthleteId(),
                        payment.getDiscipline()
                );

                disciplinePayments.merge(
                        key,
                        payment.getAmount(),
                        Double::sum
                );
            }
        }

        Map<AthleteKey, Double> result = new HashMap<>();

        for (Athlete athlete : athletes) {

            AthleteKey key = athlete.getKey();

            double paid = disciplinePayments.getOrDefault(
                    key, 0.0);

            paid += legacyPayments.getOrDefault(
                    athlete.getId(), 0.0);

            result.put(key, paid);
        }

        return result;
    }
}
