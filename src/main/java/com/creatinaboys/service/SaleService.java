package com.creatinaboys.service;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Payment;
import com.creatinaboys.repository.FilePaymentRepository;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.IPaymentRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio de ventas y gestión financiera para el Club Deportivo Creatina-Boys.
 * Un registro de deportista se identifica por documento + disciplina
 * (AthleteKey): los saldos y pagos se calculan por registro.
 *
 * @author Milton Andrés Maldonado Salcedo
 * @version 3.0
 */
public class SaleService implements ISaleService {

    /**
     * Tolerancia para comparar montos en pesos con decimales.
     */
    private static final double EPSILON = 0.005;

    private final IAthleteRepository athleteRepository;
    private final IPaymentRepository paymentRepository;
    private final Clock clock;

    public SaleService(IAthleteRepository athleteRepository, IBillingService billingService) {
        this(athleteRepository, billingService, new FilePaymentRepository());
    }

    public SaleService(IAthleteRepository athleteRepository, IBillingService billingService,
            IPaymentRepository paymentRepository) {
        this(athleteRepository, billingService, paymentRepository, Clock.systemDefaultZone());
    }

    public SaleService(IAthleteRepository athleteRepository, IBillingService billingService,
            IPaymentRepository paymentRepository, Clock clock) {
        this.athleteRepository = Objects.requireNonNull(athleteRepository, "El repositorio de atletas no puede ser nulo.");
        Objects.requireNonNull(billingService, "El servicio de facturación no puede ser nulo.");
        this.paymentRepository = Objects.requireNonNull(paymentRepository, "El repositorio de pagos no puede ser nulo.");
        this.clock = Objects.requireNonNull(clock, "El reloj no puede ser nulo.");
    }

    /**
     * Valida la existencia del registro y guarda el pago (total o parcial) del
     * mes actual, asociado a la disciplina del deportista.
     *
     * @throws IllegalArgumentException Si la clave es nula o el monto es menor
     * o igual a cero.
     * @throws AthleteNotFoundException Si no existe el registro (documento +
     * disciplina).
     */
    @Override
    public boolean processMonthlyPayment(AthleteKey key, double amount) throws AthleteNotFoundException {
        if (key == null) {
            throw new IllegalArgumentException("El deportista es requerido para procesar el pago.");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }

        Athlete athlete = athleteRepository.findByKey(key);
        double balance = Math.max(0.0,
                athlete.getMonthlyFee() - paidThisMonth().getOrDefault(athlete.getKey(), 0.0));

        if (balance <= EPSILON) {
            throw new IllegalArgumentException("La mensualidad de este mes ya está pagada.");
        }
        if (amount > balance + EPSILON) {
            throw new IllegalArgumentException(String.format(java.util.Locale.forLanguageTag("es-CO"),
                    "El monto ($%,.0f) supera el saldo pendiente ($%,.0f).", amount, balance));
        }

        Payment payment = new Payment(athlete.getId(), amount, LocalDate.now(clock),
                currentPeriod(), athlete.getDiscipline());
        boolean saved = paymentRepository.save(payment);

        // Si el pago completa la mensualidad, se extiende la afiliación un mes y queda activo.
        if (saved && balance - amount <= EPSILON) {
            athlete.renewMembership(1);
            athlete.clearManualOverride();
            athlete.setActive(!athlete.isMembershipExpired(0));
            athleteRepository.update(athlete);
        }
        return saved;
    }

    /**
     * Saldo pendiente del mes actual para un registro (0 si ya está al día).
     */
    @Override
    public double getPendingBalance(AthleteKey key) throws AthleteNotFoundException {
        if (key == null) {
            throw new IllegalArgumentException("El deportista es requerido.");
        }
        Athlete athlete = athleteRepository.findByKey(key);
        double paid = paidThisMonth().getOrDefault(athlete.getKey(), 0.0);
        return Math.max(0.0, athlete.getMonthlyFee() - paid);
    }

    /**
     * Suma la mensualidad de todos los registros.
     */
    @Override
    public double calculateEstimatedMonthlyRevenue() {
        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .mapToDouble(Athlete::getMonthlyFee)
                .sum();
    }

    /**
     * Suma todos los pagos efectivamente registrados.
     */
    @Override
    public double calculateTotalCollectedRevenue() {
        return paymentRepository.findAll().stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

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
     * Garantiza que todas las categorías queden inicializadas aunque no tengan
     * atletas.
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
     * Un registro está pendiente si lo pagado en el mes actual es menor que su
     * mensualidad.
     */
    @Override
    public List<Athlete> getAthletesWithPendingPayment() {
        Map<AthleteKey, Double> paid = paidThisMonth();
        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> paid.getOrDefault(a.getKey(), 0.0) + EPSILON < a.getMonthlyFee())
                .collect(Collectors.toList());
    }

    @Override
    public Map<AthleteKey, Double> getPaidThisMonthByAthlete() {
        return paidThisMonth();
    }

    @Override
    public List<Payment> getPaymentHistory() {
        List<Payment> all = new ArrayList<>(paymentRepository.findAll());
        Collections.reverse(all); // el último guardado primero
        all.sort(Comparator.comparing(Payment::getPaymentDate).reversed()); // estable: respeta el desempate
        return all;
    }

    /**
     * Atletas pertenecientes a una disciplina (ej. "Football", "Basketball").
     */
    public List<Athlete> getAthletesByDiscipline(String discipline) {
        if (discipline == null) {
            return List.of();
        }

        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> discipline.equalsIgnoreCase(a.getDiscipline()))
                .collect(Collectors.toList());
    }

    /**
     * Atletas pertenecientes a una categoría de edad.
     */
    public List<Athlete> getAthletesByCategory(CategoryEnum category) {
        if (category == null) {
            return List.of();
        }

        return athleteRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(a -> category.equals(a.getCategory()))
                .collect(Collectors.toList());
    }

    private YearMonth currentPeriod() {
        return YearMonth.now(clock);
    }

    /**
     * Total pagado en el mes actual por registro. La pertenencia la decide
     * Payment.belongsTo (Information Expert): un pago con disciplina cuenta
     * solo para ese registro; uno antiguo sin disciplina cuenta para el
     * documento.
     */
    private Map<AthleteKey, Double> paidThisMonth() {
        YearMonth period = currentPeriod();
        List<Payment> thisMonth = paymentRepository.findAll().stream()
                .filter(p -> period.equals(p.getPeriod()))
                .collect(Collectors.toList());

        Map<AthleteKey, Double> paid = new HashMap<>();
        for (Athlete a : athleteRepository.findAll()) {
            double sum = thisMonth.stream()
                    .filter(p -> p.belongsTo(a.getKey()))
                    .mapToDouble(Payment::getAmount)
                    .sum();
            paid.put(a.getKey(), sum);
        }
        return paid;
    }
}
