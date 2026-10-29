package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Payment;
import com.creatinaboys.model.PaymentStatusEnum;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.ui.components.CardPanel;
import com.creatinaboys.ui.components.IconBadge;
import com.creatinaboys.ui.components.Money;
import com.creatinaboys.ui.components.ModernTable;
import com.creatinaboys.ui.components.ShareBar;
import com.creatinaboys.ui.components.SpanishLabels;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.ui.theme.ThemeConstants;
import com.creatinaboys.model.AthleteKey;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Pantalla de Reportes financieros: indicadores del mes, ingresos proyectados
 * por disciplina y por categoría, cartera pendiente y recaudo mes a mes.
 * <p>
 * Solo lee datos: toda la lógica de cálculo vive en {@link ISaleService} y
 * {@link AthleteService}; esta clase únicamente los presenta.
 * </p>
 */
public class ReportsPanel extends JPanel {

    private static final String VIEW_PENDING = "PENDING";
    private static final String VIEW_MONTHLY = "MONTHLY";
    private static final Color OK_GREEN = new Color(16, 185, 129);

    private static final Map<String, Color[]> STATUS_PALETTE = Map.of(
            PaymentStatusEnum.PARTIAL.getLabel(), ModernTable.pill(new Color(254, 240, 138), new Color(133, 77, 14)),
            PaymentStatusEnum.PENDING.getLabel(), ModernTable.pill(new Color(254, 202, 202), new Color(153, 27, 27))
    );

    private final AthleteService athleteService;
    private final ISaleService saleService;

    // Indicadores
    private final JLabel lblProjected = statValue();
    private final JLabel lblCollected = statValue();
    private final JLabel lblPending = statValue();
    private final JLabel lblCompliance = statValue();
    private final JLabel lblError = new JLabel(" ");

    // Ingresos proyectados (se reconstruyen en cada refresh)
    private final JPanel revenueContent = new JPanel();

    // Tablas
    private JTable pendingTable;
    private JTable monthlyTable;
    private final CardLayout tableCards = new CardLayout();
    private final JPanel tableHolder = new JPanel(tableCards);
    private TabButton tabPending;
    private TabButton tabMonthly;

    public ReportsPanel(AthleteService athleteService, ISaleService saleService) {
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
        lower.add(buildRevenueCard(), BorderLayout.WEST);
        lower.add(buildTablesCard(), BorderLayout.CENTER);
        center.add(lower, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    // ------------------------------------------------------------------ Construcción
    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 8, 14, 0));

        JLabel title = new JLabel("Reportes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Resumen financiero del club: ingresos, cartera pendiente y recaudo por mes.");
        subtitle.setFont(ThemeConstants.FONT_REGULAR);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        lblError.setFont(ThemeConstants.FONT_SMALL);
        lblError.setForeground(ThemeConstants.DANGER_RED);
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        header.add(lblError);
        return header;
    }

    private JComponent buildStats() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.add(statCard(Type.CHART, ThemeConstants.PRIMARY_INDIGO, lblProjected, "Ingreso proyectado"));
        row.add(statCard(Type.CARD, ThemeConstants.PRIMARY_BLUE, lblCollected, "Recaudado este mes"));
        row.add(statCard(Type.FLAG, new Color(239, 68, 68), lblPending, "Por cobrar este mes"));
        row.add(statCard(Type.CHECK, OK_GREEN, lblCompliance, "Cobro del mes"));
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

    private JComponent buildRevenueCard() {
        CardPanel card = new CardPanel(new BorderLayout(), 16);
        card.setPreferredSize(new Dimension(330, 0));

        revenueContent.setLayout(new BoxLayout(revenueContent, BoxLayout.Y_AXIS));
        revenueContent.setOpaque(false);

        card.add(revenueContent, BorderLayout.NORTH);
        return card;
    }

    private JComponent buildTablesCard() {
        CardPanel card = new CardPanel(new BorderLayout(0, 12), 14);

        tabPending = new TabButton("Cartera pendiente", true);
        tabMonthly = new TabButton("Recaudo por mes", false);
        tabPending.addActionListener(e -> showTab(VIEW_PENDING));
        tabMonthly.addActionListener(e -> showTab(VIEW_MONTHLY));

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tabs.setOpaque(false);
        tabs.add(tabPending);
        tabs.add(tabMonthly);

        pendingTable = ModernTable.create(
                new String[]{"Documento", "Deportista", "Disciplina", "Saldo", "Estado"},
                new int[]{110, 150, 95, 100, 105}, 3);
        ModernTable.setBadgeColumn(pendingTable, 4, STATUS_PALETTE);

        monthlyTable = ModernTable.create(
                new String[]{"Periodo", "Pagos", "Recaudado"},
                new int[]{120, 80, 130}, 1, 2);

        tableHolder.setOpaque(false);
        tableHolder.add(ModernTable.wrap(pendingTable), VIEW_PENDING);
        tableHolder.add(ModernTable.wrap(monthlyTable), VIEW_MONTHLY);

        card.add(tabs, BorderLayout.NORTH);
        card.add(tableHolder, BorderLayout.CENTER);
        return card;
    }

    private void showTab(String view) {
        tableCards.show(tableHolder, view);
        tabPending.setActive(VIEW_PENDING.equals(view));
        tabMonthly.setActive(VIEW_MONTHLY.equals(view));
    }

    // ------------------------------------------------------------------ Datos
    /**
     * Vuelve a leer los datos. Se llama cada vez que se muestra la pantalla.
     */
    public void refresh() {
        try {
            List<Athlete> athletes = athleteService.getAllAthletes();
            Map<AthleteKey, Double> paid = saleService.getPaidThisMonthByAthlete();

            lblError.setText(" ");
            reloadStats(athletes, paid);
            reloadRevenue();
            reloadPendingTable(athletes, paid);
            reloadMonthlyTable();
        } catch (PersistenceException e) {
            lblError.setText("No se pudieron leer los datos: " + e.getMessage());
        }
    }

    private void reloadStats(List<Athlete> athletes, Map<AthleteKey, Double> paid) {
        double projected = saleService.calculateEstimatedMonthlyRevenue();
        double collected = 0, pending = 0;
        for (Athlete a : athletes) {
            double p = paid.getOrDefault(a.getKey(), 0.0);
            collected += Math.min(p, a.getMonthlyFee());
            pending += Math.max(0, a.getMonthlyFee() - p);
        }
        lblProjected.setText(Money.cop(projected));
        lblCollected.setText(Money.cop(collected));
        lblPending.setText(Money.cop(pending));
        lblCompliance.setText(projected > 0 ? Math.round(collected * 100 / projected) + " %" : "—");
    }

    private void reloadRevenue() {
        double total = saleService.calculateEstimatedMonthlyRevenue();

        revenueContent.removeAll();
        revenueContent.add(cardTitle("Ingresos proyectados"));
        revenueContent.add(Box.createVerticalStrut(14));

        revenueContent.add(sectionLabel("POR DISCIPLINA"));
        revenueContent.add(Box.createVerticalStrut(8));
        saleService.getRevenueByDiscipline().entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> addRevenueRow(SpanishLabels.of(e.getKey()), e.getValue(), total,
                ThemeConstants.PRIMARY_BLUE));

        revenueContent.add(Box.createVerticalStrut(10));
        revenueContent.add(sectionLabel("POR CATEGORÍA"));
        revenueContent.add(Box.createVerticalStrut(8));
        Map<CategoryEnum, Double> byCategory = saleService.getRevenueByCategory();
        for (CategoryEnum category : CategoryEnum.values()) {
            addRevenueRow(SpanishLabels.of(category.getName()), byCategory.getOrDefault(category, 0.0),
                    total, ThemeConstants.PRIMARY_INDIGO);
        }

        revenueContent.revalidate();
        revenueContent.repaint();
    }

    private void addRevenueRow(String label, double amount, double total, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel name = new JLabel(label);
        name.setFont(ThemeConstants.FONT_REGULAR);
        name.setForeground(ThemeConstants.TEXT_MAIN);

        JLabel value = new JLabel(Money.cop(amount));
        value.setFont(ThemeConstants.FONT_BOLD);
        value.setForeground(ThemeConstants.TEXT_MAIN);

        ShareBar bar = new ShareBar(color);
        bar.setValue(total > 0 ? amount / total : 0);

        row.add(name, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        row.add(bar, BorderLayout.SOUTH);

        revenueContent.add(row);
        revenueContent.add(Box.createVerticalStrut(8));
    }

    private void reloadPendingTable(List<Athlete> athletes, Map<AthleteKey, Double> paid) {
        DefaultTableModel model = (DefaultTableModel) pendingTable.getModel();
        model.setRowCount(0);
        saleService.getAthletesWithPendingPayment().stream()
                .sorted(Comparator.comparingDouble((Athlete a)
                        -> a.getMonthlyFee() - paid.getOrDefault(a.getKey(), 0.0)).reversed())
                .forEach(a -> {
                    double p = paid.getOrDefault(a.getKey(), 0.0);
                    model.addRow(new Object[]{
                        a.getId(),
                        a.getFullName(),
                        SpanishLabels.of(a.getDiscipline()),
                        Money.cop(Math.max(0, a.getMonthlyFee() - p)),
                        PaymentStatusEnum.of(a.getMonthlyFee(), p).getLabel()
                    });
                });
    }

    private void reloadMonthlyTable() {
        Map<YearMonth, double[]> byPeriod = new TreeMap<>(Comparator.reverseOrder());
        for (Payment p : saleService.getPaymentHistory()) {
            double[] acc = byPeriod.computeIfAbsent(p.getPeriod(), k -> new double[2]);
            acc[0]++;
            acc[1] += p.getAmount();
        }

        DefaultTableModel model = (DefaultTableModel) monthlyTable.getModel();
        model.setRowCount(0);
        byPeriod.forEach((period, acc) -> model.addRow(new Object[]{
            period.toString(), (int) acc[0], Money.cop(acc[1])
        }));
    }

    // ------------------------------------------------------------------ Utilidades
    private static JLabel statValue() {
        JLabel l = new JLabel("—");
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setForeground(ThemeConstants.TEXT_MAIN);
        return l;
    }

    private static JLabel cardTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeConstants.FONT_SUBTITLE);
        l.setForeground(ThemeConstants.TEXT_MAIN);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeConstants.FONT_SECTION);
        l.setForeground(ThemeConstants.PLACEHOLDER);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    /**
     * Pestaña de texto con subrayado azul cuando está activa.
     */
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
