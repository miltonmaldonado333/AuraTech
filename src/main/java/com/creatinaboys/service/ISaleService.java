package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Payment;

import java.util.List;
import java.util.Map;

/**
 * Contrato de interfaz para la gestión financiera, cobros de mensualidad y métricas de ventas.
 * <p>
 * Aplica el Principio de Inversión de Dependencias (DIP) de SOLID, permitiendo que las
 * capas superiores (como la interfaz gráfica Swing) dependan de esta abstracción y no de una
 * implementación concreta.
 * </p>
 * 
 * @author Milton Andrés Maldonado Salcedo
 * @version 1.0
 */
public interface ISaleService {

    /**
     * Registra el pago (total o parcial) de la mensualidad del mes actual de un atleta.
     *
     * @param athleteId Documento de identidad único del atleta registrado.
     * @param amount Monto abonado o pagado en pesos colombianos (COP).
     * @return {@code true} si el pago se procesó y registró correctamente; {@code false} en caso contrario.
     * @throws AthleteNotFoundException Si no se encuentra un atleta con la identificación proporcionada en el repositorio.
     * @throws IllegalArgumentException Si el ID ingresado es nulo, está vacío, el monto es menor o igual a cero,
     *         la mensualidad del mes ya está pagada o el monto supera el saldo pendiente.
     *         Cuando el pago completa la mensualidad, esta se renueva un mes y el deportista queda activo.
     */
    boolean processMonthlyPayment(String athleteId, double amount) throws AthleteNotFoundException;

    /**
     * Calcula el ingreso mensual proyectado o estimado del club deportivo.
     * <p>
     * Suma la tarifa mensual correspondiente a todos los atletas activos según su categoría.
     * Este valor alimenta directamente los indicadores del Panel General (Dashboard).
     * </p>
     *
     * @return El monto total estimado de ingresos mensuales en pesos colombianos (COP).
     */
    double calculateEstimatedMonthlyRevenue();

    /**
     * Calcula el valor total acumulado de los pagos efectivamente registrados.
     *
     * @return El total de dinero efectivamente recaudado por concepto de mensualidades (COP).
     */
    double calculateTotalCollectedRevenue();

    /**
     * Obtiene el desglose de ingresos mensuales estimados agrupados por disciplina deportiva
     * (por ejemplo: Fútbol, Baloncesto).
     *
     * @return Un mapa cuya clave es el nombre de la disciplina y el valor es el total proyectado (COP).
     */
    Map<String, Double> getRevenueByDiscipline();

    /**
     * Obtiene el desglose de ingresos mensuales estimados agrupados por categoría de edad
     * (Niños, Jóvenes, Adultos).
     *
     * @return Un mapa cuyas claves son las constantes de {@link CategoryEnum} y cuyos valores representan el total proyectado (COP).
     */
    Map<CategoryEnum, Double> getRevenueByCategory();

    /**
     * Retorna el listado de atletas cuyo pago del mes actual es menor a su mensualidad.
     *
     * @return Una lista de objetos {@link Athlete} con estado de cobro pendiente.
     */
    List<Athlete> getAthletesWithPendingPayment();

    /**
     * Calcula lo que le falta pagar a un atleta de su mensualidad del mes actual.
     *
     * @param athleteId Documento de identidad del atleta.
     * @return Saldo pendiente en COP; 0 si ya está al día.
     * @throws AthleteNotFoundException Si no existe un atleta con ese documento.
     */
    double getPendingBalance(String athleteId) throws AthleteNotFoundException;

    /**
     * Total pagado en el mes actual por cada deportista.
     *
     * @return Mapa documento → monto pagado (los que no han pagado no aparecen).
     */
    Map<String, Double> getPaidThisMonthByAthlete();

    /**
     * Historial completo de pagos, del más reciente al más antiguo.
     *
     * @return Lista de pagos registrados; nunca {@code null}.
     */
    List<Payment> getPaymentHistory();
}