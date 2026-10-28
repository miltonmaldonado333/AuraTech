package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.InvalidAgeException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.IBillingService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.ui.components.CardPanel;
import com.creatinaboys.ui.components.IconBadge;
import com.creatinaboys.ui.components.ShareBar;
import com.creatinaboys.ui.components.SpanishLabels;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pantalla de Categorías por edad: rango de edades, tarifa mensual, deportistas
 * (totales y activos), ingreso mensual estimado y reparto por disciplina.
 * <p>
 * Los rangos y tarifas no están escritos aquí: se obtienen de {@link IBillingService},
 * que es el experto en esas reglas. Si cambian allá, esta pantalla se actualiza sola.
 * </p>
 */
public class CategoryPanel extends JPanel {

    private static final Locale CO = Locale.forLanguageTag("es-CO");
    private static final Color GREEN = new Color(16, 185, 129);

    private final AthleteService athleteService;
    private final IBillingService billingService;
    private final IDisciplineService disciplineService;

    private final JPanel cardsRow = new JPanel(new GridLayout(1, 3, 18, 0));
    private final JLabel lblSummary = new JLabel();

    public CategoryPanel(AthleteService athleteService, IBillingService billingService,
                         IDisciplineService disciplineService) {
        this.athleteService = athleteService;
        this.billingService = billingService;
        this.disciplineService = disciplineService;

        setLayout(new BorderLayout());
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(new EmptyBorder(30, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);

        cardsRow.setOpaque(false);
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(cardsRow, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 8, 14, 0));

        JLabel title = new JLabel("Categorías");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("La categoría y la tarifa se asignan automáticamente según la edad del deportista.");
        subtitle.setFont(ThemeConstants.FONT_REGULAR);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        lblSummary.setFont(ThemeConstants.FONT_SMALL);
        lblSummary.setForeground(ThemeConstants.TEXT_MUTED);
        lblSummary.setAlignmentX(LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(6));
        header.add(lblSummary);
        return header;
    }

    /** Vuelve a leer los datos. Se llama cada vez que se muestra la pantalla. */
    public void refresh() {
        cardsRow.removeAll();

        List<Athlete> athletes = athleteService.getAllAthletes();
        List<String> disciplines;
        try {
            disciplines = disciplineService.getAvailableDisciplines();
        } catch (PersistenceException e) {
            disciplines = List.of();
        }

        Map<CategoryEnum, int[]> ranges = computeAgeRanges();
        int total = athletes.size();
        long active = athletes.stream().filter(Athlete::isActive).count();
        lblSummary.setText(total + " deportistas en total · " + active + " activos");

        for (CategoryEnum category : CategoryEnum.values()) {
            cardsRow.add(buildCategoryCard(category, ranges.get(category), athletes, disciplines, total));
        }
        cardsRow.revalidate();
        cardsRow.repaint();
    }

    private JComponent buildCategoryCard(CategoryEnum category, int[] range, List<Athlete> all,
                                         List<String> disciplines, int total) {
        Color accent = accentOf(category);
        List<Athlete> members = all.stream().filter(a -> a.getCategory() == category).toList();
        long active = members.stream().filter(Athlete::isActive).count();
        double income = members.stream().filter(Athlete::isActive).mapToDouble(Athlete::getMonthlyFee).sum();

        CardPanel card = new CardPanel(new BorderLayout(), 18);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Cabecera: ícono + nombre + rango
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.add(new IconBadge(Type.TAG, accent, 46), BorderLayout.WEST);

        JPanel names = new JPanel();
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.setOpaque(false);
        JLabel name = label(SpanishLabels.of(category.getName()), new Font("Segoe UI", Font.BOLD, 20), ThemeConstants.TEXT_MAIN);
        JLabel ages = label(rangeText(range), ThemeConstants.FONT_REGULAR, ThemeConstants.TEXT_MUTED);
        names.add(Box.createVerticalGlue());
        names.add(name);
        names.add(ages);
        names.add(Box.createVerticalGlue());
        top.add(names, BorderLayout.CENTER);
        content.add(top);
        content.add(Box.createVerticalStrut(18));

        // Tarifa
        JLabel fee = label(money(feeOf(range)), new Font("Segoe UI", Font.BOLD, 30), accent);
        fee.setAlignmentX(LEFT_ALIGNMENT);
        JLabel feeCaption = label("tarifa mensual", ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED);
        content.add(fee);
        content.add(feeCaption);
        content.add(Box.createVerticalStrut(16));
        content.add(separator());
        content.add(Box.createVerticalStrut(14));

        // Métricas
        content.add(metricRow("Deportistas", String.valueOf(members.size())));
        content.add(Box.createVerticalStrut(8));
        content.add(metricRow("Activos", String.valueOf(active)));
        content.add(Box.createVerticalStrut(8));
        content.add(metricRow("Ingreso mensual estimado", money(income)));
        content.add(Box.createVerticalStrut(14));

        // Peso dentro del club
        ShareBar share = new ShareBar(accent);
        share.setAlignmentX(LEFT_ALIGNMENT);
        share.setValue(total == 0 ? 0 : (double) members.size() / total);
        content.add(share);
        int pct = total == 0 ? 0 : (int) Math.round(100.0 * members.size() / total);
        JLabel shareText = label(pct + "% del club", ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED);
        shareText.setBorder(new EmptyBorder(4, 0, 0, 0));
        content.add(shareText);
        content.add(Box.createVerticalStrut(16));
        content.add(separator());
        content.add(Box.createVerticalStrut(12));

        // Por disciplina
        content.add(label("POR DISCIPLINA", ThemeConstants.FONT_SECTION, ThemeConstants.PLACEHOLDER));
        content.add(Box.createVerticalStrut(8));
        for (String discipline : disciplines) {
            long n = members.stream().filter(a -> discipline.equalsIgnoreCase(a.getDiscipline())).count();
            content.add(metricRow(SpanishLabels.of(discipline), String.valueOf(n)));
            content.add(Box.createVerticalStrut(6));
        }

        card.add(content, BorderLayout.NORTH);
        return card;
    }

    // ------------------------------------------------------------------ cálculo de rangos

    /** Recorre las edades válidas y deduce el rango [min, max] de cada categoría. */
    private Map<CategoryEnum, int[]> computeAgeRanges() {
        Map<CategoryEnum, int[]> ranges = new EnumMap<>(CategoryEnum.class);
        for (int age = 1; age <= Athlete.MAX_AGE; age++) {
            try {
                CategoryEnum c = billingService.determineCategory(age);
                int[] r = ranges.get(c);
                if (r == null) {
                    ranges.put(c, new int[]{age, age});
                } else {
                    r[1] = age;
                }
            } catch (InvalidAgeException ignored) {
                // edad por debajo del mínimo del club: no pertenece a ninguna categoría
            }
        }
        return ranges;
    }

    private double feeOf(int[] range) {
        if (range == null) {
            return 0;
        }
        try {
            return billingService.calculateFee(range[0]);
        } catch (InvalidAgeException e) {
            return 0;
        }
    }

    private static String rangeText(int[] range) {
        if (range == null) {
            return "Sin rango definido";
        }
        return range[1] >= Athlete.MAX_AGE ? range[0] + " años o más" : range[0] + " a " + range[1] + " años";
    }

    // ------------------------------------------------------------------ utilidades visuales

    private static Color accentOf(CategoryEnum c) {
        return switch (c) {
            case CHILDREN -> GREEN;
            case YOUTH -> ThemeConstants.PRIMARY_BLUE;
            case ADULTS -> ThemeConstants.PRIMARY_INDIGO;
        };
    }

    private static String money(double value) {
        return NumberFormat.getCurrencyInstance(CO).format(value).replace(",00", "");
    }

    private static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JComponent metricRow(String key, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel k = new JLabel(key);
        k.setFont(ThemeConstants.FONT_REGULAR);
        k.setForeground(ThemeConstants.TEXT_MUTED);
        JLabel v = new JLabel(value);
        v.setFont(ThemeConstants.FONT_BOLD);
        v.setForeground(ThemeConstants.TEXT_MAIN);
        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    private static JComponent separator() {
        JSeparator s = new JSeparator();
        s.setForeground(ThemeConstants.BORDER_COLOR);
        s.setAlignmentX(LEFT_ALIGNMENT);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}