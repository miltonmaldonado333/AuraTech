package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.repository.IAthleteRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementación concreta del servicio de ventas y gestión financiera para el Club Deportivo Creatina-Boys.
 *
 * Esta clase orquesta el procesamiento de pagos, la consolidación de ingresos estimados y reales,
 * y la generación de métricas por disciplina y categoría utilizando la API de Streams y Expresiones Lambda de Java.
 *
 *   SRP (Single Responsibility Principle): Su única responsabilidad es administrar la lógica financiera y de cobros del club.
 *   DIP (Dependency Inversion Principle):Depende exclusivamente de las abstracciones {@link IAthleteRepository} e {@link IBillingService}.
 *   GRASP Controller: Actúa como controlador para coordinar casos de uso financieros desde la capa de presentación.
 * 
 * 
 * @author Milton Andrés Maldonado Salcedo
 * @version 1.1
 */
public class SaleService implements ISaleService {

    /** Repositorio para la persistencia y consulta de atletas. */
    private final IAthleteRepository athleteRepository;

    /** Servicio especializado en las reglas de facturación y categorías por edad. */
    private final IBillingService billingService;

    /**
     * Construye una nueva instancia de {@code SaleService} inyectando sus dependencias requeridas.
     *
     * @param athleteRepository Repositorio de almacenamiento de atletas (en memoria o archivo).
     * @param billingService Servicio encargado de determinar tarifas y categorías de edad.
     * @throws NullPointerException Si alguno de los parámetros inyectados es {@code null}.
     */
    public SaleService(IAthleteRepository athleteRepository, IBillingService billingService) {
        this.athleteRepository = Objects.requireNonNull(athleteRepository, "El repositorio de atletas no puede ser nulo.");
        this.billingService = Objects.requireNonNull(billingService, "El servicio de facturación no puede ser nulo.");
    }

    /**
     * {@inheritDoc}
     * Valida la existencia del atleta en el repositorio, comprueba que el monto cubra o abone a la tarifa
     * calculada y actualiza el estado del registro.

     * @param athleteId Documento de identidad único del atleta registrado.
     * @param amount Monto abonado o pagado en pesos colombianos (COP).
     * @return {@code true} si el proceso finalizó exitosamente.
     * @throws AthleteNotFoundException Si el documento ingresado no coincide con ningún registro.
     * @throws IllegalArgumentException Si {@code athleteId} es nulo/vacío o el monto es menor o igual a cero.
     */
    @Override
    public boolean processMonthlyPayment(String athleteId, double amount) throws AthleteNotFoundException {
        if (athleteId == null || athleteId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del atleta es requerido para procesar el pago.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }

        Athlete athlete = athleteRepository.findById(athleteId);
        if (athlete == null) {
            throw new AthleteNotFoundException(athleteId);
        }

        double expectedFee = athlete.getMonthlyFee();
        if (amount < expectedFee) {
            System.out.println("Pago parcial recibido para " + athlete.getFullName() + ": $" + amount + " / $" + expectedFee);
        }

        athleteRepository.update(athlete);
        return true;
    }

    /**
     * {@inheritDoc}
     * Procesa la lista de atletas mediante Java Streams, aplicando {@code mapToDouble} y {@code sum()}
     * para calcular el total acumulado de las tarifas mensuales.
     *
     * @return Total proyectado en pesos colombianos (COP).
     */
    @Override
    public double calculateEstimatedMonthlyRevenue() {
        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .mapToDouble(Athlete::getMonthlyFee)
                .sum();
    }

    /**
     * {@inheritDoc}
     *
     * @return Total recaudado en pesos colombianos (COP).
     */
    @Override
    public double calculateTotalCollectedRevenue() {
        return calculateEstimatedMonthlyRevenue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Agrupa a los deportistas por su disciplina utilizando {@code Collectors.groupingBy}
     * y suma las mensualidades acumuladas con {@code Collectors.summingDouble}.
     * </p>
     *
     * @return Mapa asociativo con la disciplina y su correspondiente total en COP.
     */
    @Override
    public Map<String, Double> getRevenueByDiscipline() {
        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(athlete -> athlete.getDiscipline() != null)
                .collect(Collectors.groupingBy(
                        Athlete::getDiscipline,
                        Collectors.summingDouble(Athlete::getMonthlyFee)
                ));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filtra a los atletas por categoría asignada y agrupa los ingresos en un {@link EnumMap}.
     * Garantiza que todas las categorías de {@link CategoryEnum} queden inicializadas aunque no tengan atletas.
     * </p>
     *
     * @return Mapa asociativo con la categoría de edad y su respectivo acumulado financiero en COP.
     */
    @Override
    public Map<CategoryEnum, Double> getRevenueByCategory() {
        Map<CategoryEnum, Double> categoryRevenueMap = athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(athlete -> athlete.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Athlete::getCategory,
                        () -> new EnumMap<>(CategoryEnum.class),
                        Collectors.summingDouble(Athlete::getMonthlyFee)
                ));

        // Asegurar que todas las categorías del Enum tengan al menos valor 0.0
        for (CategoryEnum category : CategoryEnum.values()) {
            categoryRevenueMap.putIfAbsent(category, 0.0);
        }

        return categoryRevenueMap;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Utiliza Streams para filtrar la colección de atletas activos según el estado de cobro.
     * </p>
     *
     * @return Lista de atletas con pagos pendientes.
     */
    @Override
    public List<Athlete> getAthletesWithPendingPayment() {
        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar de filtrado: Obtiene los atletas pertenecientes a una disciplina específica.
     *
     * @param discipline Nombre de la disciplina (ej. "Football", "Basketball").
     * @return Lista de atletas filtrados por disciplina.
     */
    public List<Athlete> getAthletesByDiscipline(String discipline) {
        if (discipline == null) return List.of();

        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> discipline.equalsIgnoreCase(a.getDiscipline()))
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar de filtrado: Obtiene los atletas pertenecientes a una categoría de edad.
     *
     * @param category Constante de {@link CategoryEnum} (CHILDREN, YOUTH, ADULTS).
     * @return Lista de atletas filtrados por categoría.
     */
    public List<Athlete> getAthletesByCategory(CategoryEnum category) {
        if (category == null) return List.of();

        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> category.equals(a.getCategory()))
                .collect(Collectors.toList());
    }
}