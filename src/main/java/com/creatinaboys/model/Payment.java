package com.creatinaboys.model;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Pago (total o parcial) de la mensualidad de un deportista.
 * Es inmutable: un pago registrado no se modifica.
 */
public class Payment {

    private final String athleteId;
    private final double amount;
    private final LocalDate paymentDate;
    private final YearMonth period;

    public Payment(String athleteId, double amount, LocalDate paymentDate, YearMonth period) {
        if (athleteId == null || athleteId.isBlank()) {
            throw new IllegalArgumentException("El ID del atleta es obligatorio para registrar un pago");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }
        if (paymentDate == null) {
            throw new IllegalArgumentException("La fecha del pago es obligatoria");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha del pago no puede ser futura");
        }
        if (period == null) {
            throw new IllegalArgumentException("El periodo (mes) del pago es obligatorio");
        }
        this.athleteId = athleteId.trim();
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.period = period;
    }

    public String getAthleteId() { return athleteId; }

    public double getAmount() { return amount; }

    public LocalDate getPaymentDate() { return paymentDate; }

    /** Mes de la mensualidad que cubre el pago. */
    public YearMonth getPeriod() { return period; }

    @Override
    public String toString() {
        return "Payment{athleteId='" + athleteId + "', amount=" + amount
                + ", date=" + paymentDate + ", period=" + period + "}";
    }
}