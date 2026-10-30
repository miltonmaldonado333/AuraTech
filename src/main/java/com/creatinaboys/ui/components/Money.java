package com.creatinaboys.ui.components;

import java.text.NumberFormat;
import java.util.Locale;

/** Formato de pesos colombianos para mostrar en pantalla (ej. $ 50.000). */
public final class Money {

    private static final Locale CO = Locale.forLanguageTag("es-CO");

    private Money() { }

    public static String cop(double value) {
        return NumberFormat.getCurrencyInstance(CO).format(value).replace(",00", "");
    }
}