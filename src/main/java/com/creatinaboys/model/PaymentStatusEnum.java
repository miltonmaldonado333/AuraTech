package com.creatinaboys.model;

/**
 * Estado de la mensualidad del mes actual de un deportista (RF4).
 * Se calcula a partir de lo pagado; no se guarda en el archivo.
 */
public enum PaymentStatusEnum {
    PAID("Pagado"),
    PARTIAL("Parcial"),
    PENDING("Pendiente");

    /** Tolerancia para comparar montos en pesos con decimales. */
    private static final double EPSILON = 0.005;

    private final String label;

    PaymentStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * @param fee  mensualidad del deportista
     * @param paid total pagado en el mes
     */
    public static PaymentStatusEnum of(double fee, double paid) {
        if (paid + EPSILON >= fee) {
            return PAID;
        }
        return paid > EPSILON ? PARTIAL : PENDING;
    }

    @Override
    public String toString() {
        return label;
    }
}