package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Payment;

import java.time.LocalDate;
import java.time.YearMonth;

/** Formato: PAYMENT;athleteId;amount;paymentDate;period (ej. 2026-10). */
public class PaymentParser {

    private static final String TYPE = "PAYMENT";
    private static final int FIELD_COUNT = 5;

    public String toTextLine(Payment payment) {
        return String.join(";",
                TYPE,
                TextCodec.escape(payment.getAthleteId()),
                String.valueOf(payment.getAmount()),
                payment.getPaymentDate().toString(),
                payment.getPeriod().toString()
        );
    }

    /** @param fields campos de la línea separados con {@link TextCodec#split}. */
    public Payment fromTextFields(String[] fields) throws PersistenceException {
        try {
            TextCodec.requireFields(fields, FIELD_COUNT, TYPE);
            return new Payment(
                    TextCodec.unescape(fields[1]),
                    Double.parseDouble(fields[2].trim()),
                    LocalDate.parse(fields[3].trim()),
                    YearMonth.parse(fields[4].trim()));
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Error al parsear el registro de pago: " + e.getMessage(), e);
        }
    }
}