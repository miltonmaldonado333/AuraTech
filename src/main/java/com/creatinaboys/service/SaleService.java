package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Payment;
import com.creatinaboys.repository.FilePaymentRepository;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.IPaymentRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementación concreta del servicio de ventas y gestión financiera para el Club Deportivo Creatina-Boys.
 * <p>
 * Orquesta el registro de pagos, la consolidación de ingresos estimados y reales,
 * y la generación de métricas por disciplina y categoría con Streams y lambdas.
 * </p>
 * <ul>
 *   <li><b>SRP:</b> su única responsabilidad es la lógica financiera y de cobros del club.</li>
 *   <li><b>DIP:</b> depende de las abstracciones {@link IAthleteRepository}, {@link IPaymentRepository} e {@link IBillingService}.</li>
 *   <li><b>GRASP Controller:</b> coordina los casos de uso financieros desde la capa de presentación.</li>
 * </ul>
 * <p>
 * Un pago cubre el mes actual. Se pueden registrar pagos parciales: el deportista queda
 * al día cuando lo pagado en el mes alcanza su mensualidad.
 * </p>
 *
 * @author Milton Andrés Maldonado Salcedo
 * @version 2.0
 */
public class SaleService implements ISaleService {

    /** Tolerancia para comparar montos en pesos con decimales. */
    private static final double EPSILON = 0.005;

    private final IAthleteRepository athleteRepository;
    private final IBillingService billingService;
    private final IPaymentRepository paymentRepository;
    private final Clock clock;

    /**
     * Constructor original. Los pagos se guardan en {@code data/payments.txt}.
     *
     * @throws NullPointerException Si alguno de los parámetros es {@code null}.
     */
    public SaleService(IAthleteRepository athleteRepository, IBillingService billingService) {
        this(athleteRepository, billingService, new FilePaymentRepository());
    }

    public SaleService(IAthleteRepository athleteRepository, IBillingService billingService,
                       IPaymentRepository paymentRepository) {
        this(athleteRepository, billingService, paymentRepository, Clock.systemDefaultZone());
    }

    /** Permite fijar el reloj (útil en pruebas). */
    public SaleService(IAthleteRepository athleteRepository, IBillingService billingService,
                       IPaymentRepository paymentRepository, Clock clock) {
        this.athleteRepository = Objects.requireNonNull(athleteRepository, "El repositorio de atletas no puede ser nulo.");
        this.billingService = Objects.requireNonNull(billingService, "El servicio de facturación no puede ser nulo.");
        this.paymentRepository = Objects.requireNonNull(paymentRepository, "El repositorio de pagos no puede ser nulo.");
        this.clock = Objects.requireNonNull(clock, "El reloj no puede ser nulo.");
    }

    /**
     * {@inheritDoc}
     * Valida la existencia del atleta y registra el pago (total o parcial) en el mes actual.
     *
     * @throws IllegalArgumentException Si {@code athleteId} es nulo/vacío o el monto es menor o igual a cero.
     * @throws AthleteNotFoundException Si el documento no coincide con ningún atleta.
     */
    @Override
    public boolean processMonthlyPayment(String athleteId, double amount) throws AthleteNotFoundException {
        if (athleteId == null || athleteId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del atleta es requerido para procesar el pago.");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }

        Athlete athlete = athleteRepository.findById(athleteId.trim());
        Payment payment = new Payment(athlete.getId(), amount, LocalDate.now(clock), currentPeriod());
        return paymentRepository.save(payment);
    }

    /**
     * Saldo pendiente del mes actual para un deportista (0 si ya está al día).
     *
     * @throws AthleteNotFoundException Si el atleta no existe.
     */
    @Override
    public double getPendingBalance(String athleteId) throws AthleteNotFoundException {
        if (athleteId == null || athleteId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del atleta es requerido.");
        }
        Athlete athlete = athleteRepository.findById(athleteId.trim());
        double paid = paidThisMonthByAthlete().getOrDefault(athlete.getId(), 0.0);
        return Math.max(0.0, athlete.getMonthlyFee() - paid);
    }

    /** {@inheritDoc} Suma la mensualidad de todos los atletas registrados. */
    @Override
    public double calculateEstimatedMonthlyRevenue() {
        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .mapToDouble(Athlete::getMonthlyFee)
                .sum();
    }

    /** {@inheritDoc} Suma todos los pagos efectivamente registrados. */
    @Override
    public double calculateTotalCollectedRevenue() {
        return paymentRepository.findAll().stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    /** {@inheritDoc} */
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
     * Garantiza que todas las categorías de {@link CategoryEnum} queden inicializadas aunque no tengan atletas.
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

        for (CategoryEnum category : CategoryEnum.values()) {
            categoryRevenueMap.putIfAbsent(category, 0.0);
        }
        return categoryRevenueMap;
    }

    /**
     * {@inheritDoc}
     * Un atleta está pendiente si lo pagado en el mes actual es menor que su mensualidad.
     */
    @Override
    public List<Athlete> getAthletesWithPendingPayment() {
        Map<String, Double> paid = paidThisMonthByAthlete();
        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> paid.getOrDefault(a.getId(), 0.0) + EPSILON < a.getMonthlyFee())
                .collect(Collectors.toList());
    }

    /** Atletas pertenecientes a una disciplina (ej. "Football", "Basketball"). */
    public List<Athlete> getAthletesByDiscipline(String discipline) {
        if (discipline == null) return List.of();

        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> discipline.equalsIgnoreCase(a.getDiscipline()))
                .collect(Collectors.toList());
    }

    /** Atletas pertenecientes a una categoría de edad. */
    public List<Athlete> getAthletesByCategory(CategoryEnum category) {
        if (category == null) return List.of();

        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> category.equals(a.getCategory()))
                .collect(Collectors.toList());
    }

    private YearMonth currentPeriod() {
        return YearMonth.now(clock);
    }

    /** Total pagado en el mes actual, agrupado por documento del atleta. */
    private Map<String, Double> paidThisMonthByAthlete() {
        YearMonth period = currentPeriod();
        Map<String, Double> paid = new HashMap<>();
        for (Payment p : paymentRepository.findAll()) {
            if (period.equals(p.getPeriod())) {
                paid.merge(p.getAthleteId(), p.getAmount(), Double::sum);
            }
        }
        return paid;
    }
}