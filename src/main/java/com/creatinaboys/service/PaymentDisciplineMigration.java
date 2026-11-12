
package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Payment;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.IPaymentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Migrates legacy payments without a discipline.
 *
 * A payment is migrated only when its athlete ID
 * belongs to exactly one athlete record.
 *
 * The migration is idempotent and only persists
 * changes when at least one payment is updated.
 */
public class PaymentDisciplineMigration {

    private final IPaymentRepository paymentRepository;
    private final IAthleteRepository athleteRepository;

    public PaymentDisciplineMigration(
            IPaymentRepository paymentRepository,
            IAthleteRepository athleteRepository) {

        this.paymentRepository = Objects.requireNonNull(
                paymentRepository,
                "Payment repository cannot be null"
        );

        this.athleteRepository = Objects.requireNonNull(
                athleteRepository,
                "Athlete repository cannot be null"
        );
    }

    /**
     * Migrates payments without a discipline.
     *
     * @return number of successfully migrated payments
     */
    public int run() throws PersistenceException {

        List<Payment> payments = paymentRepository.findAll();

        boolean hasLegacyPayments = payments.stream()
                .anyMatch(payment ->
                        payment.getDiscipline().isEmpty()
                );

        if (!hasLegacyPayments) {
            return 0;
        }

        List<Athlete> athletes = athleteRepository.findAll();

        Map<String, List<Athlete>> athletesById =
                groupAthletesById(athletes);

        List<Payment> updatedPayments =
                new ArrayList<>(payments.size());

        int migrated = 0;

        for (Payment payment : payments) {

            if (!payment.getDiscipline().isEmpty()) {
                updatedPayments.add(payment);
                continue;
            }

            List<Athlete> records = athletesById.getOrDefault(
                    payment.getAthleteId(),
                    List.of()
            );

            if (records.size() != 1) {
                updatedPayments.add(payment);
                continue;
            }

            Athlete athlete = records.get(0);

            Payment migratedPayment = new Payment(
                    payment.getAthleteId(),
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getPeriod(),
                    athlete.getDiscipline()
            );

            updatedPayments.add(migratedPayment);
            migrated++;
        }

        if (migrated > 0) {
            paymentRepository.replaceAll(updatedPayments);
        }

        return migrated;
    }

    /**
     * Groups athlete records by identification number.
     */
    private Map<String, List<Athlete>> groupAthletesById(
            List<Athlete> athletes) {

        Map<String, List<Athlete>> result = new HashMap<>();

        for (Athlete athlete : athletes) {

            result.computeIfAbsent(
                    athlete.getId(),
                    key -> new ArrayList<>()
            ).add(athlete);
        }

        return result;
    }
}
