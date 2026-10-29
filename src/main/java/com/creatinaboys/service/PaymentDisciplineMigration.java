package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Payment;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.IPaymentRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Asigna la disciplina a los pagos antiguos (sin disciplina) cuando el documento
 * tiene un único registro de deportista. Es idempotente: se puede ejecutar en
 * cada arranque y solo escribe el archivo si algo cambió.
 */
public class PaymentDisciplineMigration {

    private final IPaymentRepository paymentRepository;
    private final IAthleteRepository athleteRepository;

    public PaymentDisciplineMigration(IPaymentRepository paymentRepository,
                                      IAthleteRepository athleteRepository) {
        this.paymentRepository = paymentRepository;
        this.athleteRepository = athleteRepository;
    }

    /** @return cantidad de pagos migrados. */
    public int run() throws PersistenceException {
        List<Payment> payments = paymentRepository.findAll();
        List<Payment> result = new ArrayList<>(payments.size());
        int migrated = 0;

        for (Payment p : payments) {
            if (!p.getDiscipline().isEmpty()) {
                result.add(p);
                continue;
            }
            List<Athlete> records = athleteRepository.findById(p.getAthleteId());
            if (records.size() == 1) {
                result.add(new Payment(p.getAthleteId(), p.getAmount(), p.getPaymentDate(),
                        p.getPeriod(), records.get(0).getDiscipline()));
                migrated++;
            } else {
                result.add(p); // sin registro o ambiguo: no se adivina
            }
        }

        if (migrated > 0) {
            paymentRepository.replaceAll(result);
        }
        return migrated;
    }
}