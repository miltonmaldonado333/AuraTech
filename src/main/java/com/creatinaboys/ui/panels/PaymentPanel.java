package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Payment;
import com.creatinaboys.model.PaymentStatusEnum;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.ui.components.*;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de Pagos y Cartera (RF4): resumen del mes, registro de pagos totales o parciales,
 * estado de la mensualidad de cada deportista e historial de pagos.
 * <p>
 * Toda la lógica (saldos, validaciones, renovación de la afiliación) vive en {@link ISaleService};
 * esta clase solo muestra datos y recoge la entrada del usuario.
 * </p>
 */
public class PaymentPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String VIEW_STATUS = "STATUS";
    private static final String VIEW_HISTORY = "HISTORY";
    private static final String SEPARATOR = "  ·  ";
    private static final Color OK_GREEN = new Color(22, 163, 74);
    private static final Color AMBER = new Color(217, 119, 6);

    private static final Map<String, Color[]> STATUS_PALETTE = Map.of(
            PaymentStatusEnum.PAID.getLabel(), ModernTable.pill(new Color(187, 247, 208), new Color(22, 101, 52)),
            PaymentStatusEnum.PARTIAL.getLabel(), ModernTable.pill(new Color(254, 240, 138), new Color(133, 77, 14)),
            PaymentStatusEnum.PENDING.getLabel(), ModernTable.pill(new Color(254, 202, 202), new Color(153, 27, 27))
    );

    private final AthleteService athleteService;
    private final ISaleService saleService;

    // Resumen
    private final JLabel lblCollected = statValue();
    private final JLabel lblPending = statValue();
    private final JLabel lblUpToDate = statValue();
    private final JLabel lblHistoric = statValue();

    // Formulario
    private final ModernComboBox cmbAthlete = new ModernComboBox(new String[0]);
    private final PlaceholderTextField txtAmount = new PlaceholderTextField("Monto a pagar (COP)");
    private final JLabel lblFee = infoValue();
    private final JLabel lblPaid = infoValue();
    private final JLabel lblBalance = infoValue();
    private final JLabel lblStatus = infoValue();
    private final JLabel lblFeedback = new JLabel(" ");

    // Tablas
    private JTable statusTable;
    private JTable historyTable;
    private final CardLayout tableCards = new CardLayout();
    private final JPanel tableHolder = new JPanel(tableCards);
    private TabButton tabStatus;
    private TabButton tabHistory;

    private List<Athlete> athletes = List.of();
    private Map<String, Double> paidByAthlete = Map.of();
    private boolean updatingCombo;

    public PaymentPanel(AthleteService athleteService, ISaleService saleService) {
        this.athleteService = athleteService;
        this.saleService = saleService;

        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(new EmptyBorder(30, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(buildStats(), BorderLayout.NORTH);

        JPanel lower = new JPanel(new BorderLayout(16, 0));
        lower.setOpaque(false);
        lower.add(buildForm(), BorderLayout.WEST);
        lower.add(buildTablesCard(), BorderLayout.CENTER);
        center.add(lower, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        cmbAthlete.addActionListener(e -> {
            if (!updatingCombo) {
                updateAthleteInfo(true);
            }
        });
        refresh();
    }

    // ------------------------------------------------------------------ Construcción

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 8, 14, 0));

        JLabel title = new JLabel("Pagos y Cartera");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Registra las mensualidades y controla quién está al día este mes.");
        subtitle.setFont(ThemeConstants.FONT_REGULAR);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        return header;
    }

    private JComponent buildStats() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.add(statCard(Type.CARD, ThemeConstants.PRIMARY_BLUE, lblCollected, "Recaudado este mes"));
        row.add(statCard(Type.FLAG, new Color(239, 68, 68), lblPending, "Por cobrar este mes"));
        row.add(statCard(Type.CHECK, new Color(16, 185, 129), lblUpToDate, "Deportistas al día"));
        row.add(statCard(Type.CHART, ThemeConstants.PRIMARY_INDIGO, lblHistoric, "Recaudado histórico"));
        return row;
    }

    private static JComponent statCard(Type icon, Color accent, JLabel value, String caption) {
        CardPanel card = new CardPanel(new BorderLayout(14, 0), 10);
        card.add(new IconBadge(icon, accent, 44), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        JLabel cap = new JLabel(caption);
        cap.setFont(ThemeConstants.FONT_SMALL);
        cap.setForeground(ThemeConstants.TEXT_MUTED);
        value.setAlignmentX(LEFT_ALIGNMENT);
        cap.setAlignmentX(LEFT_ALIGNMENT);
        text.add(Box.createVerticalGlue());
        text.add(value);
        text.add(cap);
        text.add(Box.createVerticalGlue());
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildForm() {
        CardPanel card = new CardPanel(new BorderLayout(), 16);
        card.setPreferredSize(new Dimension(300, 0));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel("Registrar pago");
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(14));

        content.add(sectionLabel("DEPORTISTA"));
        content.add(Box.createVerticalStrut(6));
        content.add(fullWidth(new InputBox(Type.USER, cmbAthlete)));
        content.add(Box.createVerticalStrut(14));

        JPanel info = new JPanel(new GridLayout(4, 2, 8, 6));
        info.setOpaque(false);
        info.setAlignmentX(LEFT_ALIGNMENT);
        info.add(infoKey("Mensualidad"));
        info.add(lblFee);
        info.add(infoKey("Pagado este mes"));
        info.add(lblPaid);
        info.add(infoKey("Saldo pendiente"));
        info.add(lblBalance);
        info.add(infoKey("Estado"));
        info.add(lblStatus);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        content.add(info);
        content.add(Box.createVerticalStrut(16));

        content.add(sectionLabel("MONTO"));
        content.add(Box.createVerticalStrut(6));
        content.add(fullWidth(new InputBox(Type.CARD, txtAmount)));
        content.add(Box.createVerticalStrut(10));

        ModernButton btnFull = new ModernButton("Pagar saldo completo", Type.CHECK, ModernButton.Style.SECONDARY, 200);
        btnFull.addActionListener(e -> fillBalance());
        ModernButton btnPay = new ModernButton("Registrar pago", Type.CARD, ModernButton.Style.PRIMARY, 200);
        btnPay.addActionListener(e -> registerPayment());
        txtAmount.addActionListener(e -> registerPayment());
        content.add(fullWidth(btnFull));
        content.add(Box.createVerticalStrut(8));
        content.add(fullWidth(btnPay));

        lblFeedback.setFont(ThemeConstants.FONT_SMALL);
        lblFeedback.setAlignmentX(LEFT_ALIGNMENT);
        lblFeedback.setBorder(new EmptyBorder(10, 2, 0, 0));
        content.add(lblFeedback);

        card.add(content, BorderLayout.NORTH);
        return card;
    }

    private JComponent buildTablesCard() {
        CardPanel card = new CardPanel(new BorderLayout(0, 12), 14);

        tabStatus = new TabButton("Estado del mes", true);
        tabHistory = new TabButton("Historial de pagos", false);
        tabStatus.addActionListener(e -> showTab(VIEW_STATUS));
        tabHistory.addActionListener(e -> showTab(VIEW_HISTORY));

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tabs.setOpaque(false);
        tabs.add(tabStatus);
        tabs.add(tabHistory);

        statusTable = ModernTable.create(
                new String[]{"Documento", "Deportista", "Tarifa", "Pagado", "Saldo", "Estado"},
                new int[]{115, 140, 100, 100, 100, 105}, 2, 3, 4);
        ModernTable.setBadgeColumn(statusTable, 5, STATUS_PALETTE);
        statusTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectAthleteFromTable();
            }
        });

        historyTable = ModernTable.create(
                new String[]{"Fecha", "Documento", "Deportista", "Periodo", "Monto"},
                new int[]{95, 115, 180, 90, 100}, 4);

        tableHolder.setOpaque(false);
        tableHolder.add(ModernTable.wrap(statusTable), VIEW_STATUS);
        tableHolder.add(ModernTable.wrap(historyTable), VIEW_HISTORY);

        card.add(tabs, BorderLayout.NORTH);
        card.add(tableHolder, BorderLayout.CENTER);
        return card;
    }

    private void showTab(String view) {
        tableCards.show(tableHolder, view);
        tabStatus.setActive(VIEW_STATUS.equals(view));
        tabHistory.setActive(VIEW_HISTORY.equals(view));
    }

    // ------------------------------------------------------------------ Datos

    /** Vuelve a leer los datos. Se llama cada vez que se muestra la pantalla. */
    public void refresh() {
        try {
            athletes = athleteService.getAllAthletes().stream()
                    .sorted(Comparator.comparing(Athlete::getFullName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            paidByAthlete = saleService.getPaidThisMonthByAthlete();
        } catch (PersistenceException e) {
            feedback("No se pudieron leer los datos: " + e.getMessage(), false);
            return;
        }
        reloadCombo();
        reloadStats();
        reloadStatusTable();
        reloadHistoryTable();
        updateAthleteInfo(true);
    }

    private void reloadCombo() {
        String selectedId = selectedAthleteId();
        updatingCombo = true;
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Athlete a : athletes) {
            model.addElement(a.getFullName() + SEPARATOR + a.getId());
        }
        cmbAthlete.setModel(model);
        selectInCombo(selectedId);
        updatingCombo = false;
    }

    private void reloadStats() {
        double collected = 0, pending = 0;
        int upToDate = 0;
        for (Athlete a : athletes) {
            double paid = paidOf(a);
            collected += Math.min(paid, a.getMonthlyFee());
            pending += Math.max(0, a.getMonthlyFee() - paid);
            if (statusOf(a) == PaymentStatusEnum.PAID) {
                upToDate++;
            }
        }
        lblCollected.setText(Money.cop(collected));
        lblPending.setText(Money.cop(pending));
        lblUpToDate.setText(upToDate + " de " + athletes.size());
        lblHistoric.setText(Money.cop(saleService.calculateTotalCollectedRevenue()));
    }

    private void reloadStatusTable() {
        DefaultTableModel model = (DefaultTableModel) statusTable.getModel();
        model.setRowCount(0);
        athletes.stream()
                .sorted(Comparator.comparing((Athlete a) -> statusOf(a).ordinal()).reversed()
                        .thenComparing(Athlete::getFullName, String.CASE_INSENSITIVE_ORDER))
                .forEach(a -> model.addRow(new Object[]{
                        a.getId(),
                        a.getFullName(),
                        Money.cop(a.getMonthlyFee()),
                        Money.cop(paidOf(a)),
                        Money.cop(Math.max(0, a.getMonthlyFee() - paidOf(a))),
                        statusOf(a).getLabel()
                }));
    }

    private void reloadHistoryTable() {
        Map<String, String> names = new HashMap<>();
        athletes.forEach(a -> names.put(a.getId(), a.getFullName()));

        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        for (Payment p : saleService.getPaymentHistory()) {
            model.addRow(new Object[]{
                    p.getPaymentDate().format(DATE_FMT),
                    p.getAthleteId(),
                    names.getOrDefault(p.getAthleteId(), "(deportista eliminado)"),
                    p.getPeriod().toString(),
                    Money.cop(p.getAmount())
            });
        }
    }

    // ------------------------------------------------------------------ Formulario

    private void updateAthleteInfo(boolean prefillAmount) {
        Athlete a = selectedAthlete();
        if (a == null) {
            lblFee.setText("—");
            lblPaid.setText("—");
            lblBalance.setText("—");
            lblStatus.setText("—");
            lblStatus.setForeground(ThemeConstants.TEXT_MAIN);
            return;
        }
        double paid = paidOf(a);
        double balance = Math.max(0, a.getMonthlyFee() - paid);
        PaymentStatusEnum status = statusOf(a);

        lblFee.setText(Money.cop(a.getMonthlyFee()));
        lblPaid.setText(Money.cop(paid));
        lblBalance.setText(Money.cop(balance));
        lblStatus.setText(status.getLabel());
        lblStatus.setForeground(switch (status) {
            case PAID -> OK_GREEN;
            case PARTIAL -> AMBER;
            case PENDING -> ThemeConstants.DANGER_RED;
        });
        if (prefillAmount) {
            txtAmount.setText(balance > 0 ? String.valueOf((long) Math.ceil(balance)) : "");
        }
    }

    private void fillBalance() {
        updateAthleteInfo(true);
    }

    private void registerPayment() {
        Athlete a = selectedAthlete();
        if (a == null) {
            feedback("Selecciona un deportista.", false);
            return;
        }
        String digits = txtAmount.getText().replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            feedback("Escribe el monto a pagar.", false);
            return;
        }
        try {
            double amount = Double.parseDouble(digits);
            boolean wasPaidBefore = statusOf(a) == PaymentStatusEnum.PAID;
            if (saleService.processMonthlyPayment(a.getId(), amount)) {
                refresh();
                boolean nowPaid = statusOf(a) == PaymentStatusEnum.PAID;
                feedback("Pago de " + Money.cop(amount) + " registrado para " + a.getFullName()
                        + (nowPaid && !wasPaidBefore ? ". Mensualidad completa: afiliación renovada un mes." : "."), true);
            } else {
                feedback("No se pudo registrar el pago.", false);
            }
        } catch (AthleteNotFoundException ex) {
            feedback("El deportista ya no existe.", false);
            refresh();
        } catch (IllegalArgumentException | PersistenceException ex) {
            feedback(ex.getMessage(), false);
        }
    }

    private void feedback(String text, boolean ok) {
        lblFeedback.setForeground(ok ? OK_GREEN : ThemeConstants.DANGER_RED);
        lblFeedback.setText("<html><body style='width:240px'>" + text + "</body></html>");
    }

    // ------------------------------------------------------------------ Selección

    private String selectedAthleteId() {
        Object item = cmbAthlete.getSelectedItem();
        if (item == null) {
            return null;
        }
        String s = item.toString();
        int i = s.lastIndexOf(SEPARATOR);
        return i < 0 ? s : s.substring(i + SEPARATOR.length());
    }

    private Athlete selectedAthlete() {
        String id = selectedAthleteId();
        return id == null ? null : athletes.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    private void selectInCombo(String id) {
        if (cmbAthlete.getItemCount() == 0) {
            return;
        }
        if (id != null) {
            for (int i = 0; i < cmbAthlete.getItemCount(); i++) {
                if (cmbAthlete.getItemAt(i).endsWith(SEPARATOR + id)) {
                    cmbAthlete.setSelectedIndex(i);
                    return;
                }
            }
        }
        cmbAthlete.setSelectedIndex(0);
    }

    private void selectAthleteFromTable() {
        int row = statusTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectInCombo(String.valueOf(statusTable.getValueAt(row, 0)));
        updateAthleteInfo(true);
    }

    // ------------------------------------------------------------------ Utilidades

    private double paidOf(Athlete a) {
        return paidByAthlete.getOrDefault(a.getId(), 0.0);
    }

    private PaymentStatusEnum statusOf(Athlete a) {
        return PaymentStatusEnum.of(a.getMonthlyFee(), paidOf(a));
    }

    private static JLabel statValue() {
        JLabel l = new JLabel("—");
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setForeground(ThemeConstants.TEXT_MAIN);
        return l;
    }

    private static JLabel infoValue() {
        JLabel l = new JLabel("—");
        l.setFont(ThemeConstants.FONT_BOLD);
        l.setForeground(ThemeConstants.TEXT_MAIN);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    private static JLabel infoKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeConstants.FONT_REGULAR);
        l.setForeground(ThemeConstants.TEXT_MUTED);
        return l;
    }

    private static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeConstants.FONT_SECTION);
        l.setForeground(ThemeConstants.PLACEHOLDER);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JComponent fullWidth(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    /** Pestaña de texto con subrayado azul cuando está activa. */
    private static class TabButton extends JButton {
        private boolean active;

        TabButton(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(ThemeConstants.FONT_BOLD);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 12, 10, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setForeground(active ? ThemeConstants.PRIMARY_BLUE : ThemeConstants.TEXT_MUTED);
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? ThemeConstants.PRIMARY_BLUE : ThemeConstants.TEXT_MUTED);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (active) {
                g.setColor(ThemeConstants.PRIMARY_BLUE);
                g.fillRect(8, getHeight() - 3, getWidth() - 16, 3);
            }
        }
    }
}