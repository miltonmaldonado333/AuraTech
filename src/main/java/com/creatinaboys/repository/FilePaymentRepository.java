package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Payment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilePaymentRepository implements IPaymentRepository {

    private static final String DEFAULT_FILE_PATH = "data/payments.txt";
    private final Path path;
    private final PaymentParser parser;

    public FilePaymentRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FilePaymentRepository(String filePath) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "filePath cannot be null."));
        this.parser = new PaymentParser();
    }

    @Override
    public boolean save(Payment payment) throws PersistenceException {
        if (payment == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo.");
        }
        List<Payment> payments = findAll();
        payments.add(payment);
        replaceAll(payments);
        return true;
    }

    /** Reescribe el archivo completo (FileStorage conserva la copia .bak). */
    @Override
    public void replaceAll(List<Payment> payments) throws PersistenceException {
        Objects.requireNonNull(payments, "payments cannot be null.");
        List<String> lines = new ArrayList<>(payments.size());
        for (Payment p : payments) {
            lines.add(parser.toTextLine(p));
        }
        FileStorage.writeLines(path, lines);
    }

    /** Una línea dañada se omite (y se informa) en lugar de impedir cargar al resto. */
    @Override
    public List<Payment> findAll() throws PersistenceException {
        List<Payment> payments = new ArrayList<>();
        List<String> lines = FileStorage.readLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                String[] fields = TextCodec.split(line, ';');
                if (!"PAYMENT".equals(fields[0].trim())) {
                    continue;
                }
                payments.add(parser.fromTextFields(fields));
            } catch (RuntimeException e) {
                System.err.println("Línea " + (i + 1) + " omitida en " + path + ": " + e.getMessage());
            }
        }
        return payments;
    }

    @Override
    public List<Payment> findByAthleteId(String athleteId) throws PersistenceException {
        return findAll().stream()
                .filter(p -> p.getAthleteId().equalsIgnoreCase(athleteId))
                .collect(Collectors.toList());
    }
}