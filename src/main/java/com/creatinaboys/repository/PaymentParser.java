package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Payment;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Formato: PAYMENT;athleteId;amount;paymentDate;period;discipline
 * (period ej. 2026-10). Las líneas antiguas de 5 campos no traen disciplina
 * y se leen con disciplina vacía.
 */
public class PaymentParser {

    private static final String TYPE = "PAYMENT";
    private static final int LEGACY_FIELD_COUNT = 5;
    private static final int FIELD_COUNT = 6;

    public String toTextLine(Payment payment) {
        return String.join(";",
                TYPE,
                TextCodec.escape(payment.getAthleteId()),
                String.valueOf(payment.getAmount()),
                payment.getPaymentDate().toString(),
                payment.getPeriod().toString(),
                TextCodec.escape(payment.getDiscipline())
        );
    }

    public Payment fromTextFields(String[] fields) throws PersistenceException {
        try {
            boolean legacy = fields.length < FIELD_COUNT;
            TextCodec.requireFields(fields, legacy ? LEGACY_FIELD_COUNT : FIELD_COUNT, TYPE);

            String discipline = legacy ? "" : TextCodec.unescape(fields[5]);

            return new Payment(
                    TextCodec.unescape(fields[1]),
                    Double.parseDouble(fields[2].trim()),
                    LocalDate.parse(fields[3].trim()),
                    YearMonth.parse(fields[4].trim()),
                    discipline);
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Error al parsear el registro de pago: " + e.getMessage(), e);
        }
    }
}