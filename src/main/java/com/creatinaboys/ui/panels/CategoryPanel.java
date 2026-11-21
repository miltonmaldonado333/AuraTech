package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AgeCategory;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.CategoryService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.ui.components.CardPanel;
import com.creatinaboys.ui.components.IconBadge;
import com.creatinaboys.ui.components.ModernButton;
import com.creatinaboys.ui.components.Money;
import com.creatinaboys.ui.components.ShareBar;
import com.creatinaboys.ui.components.SpanishLabels;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.validation.DataValidator;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;

/** Manages age rules and displays enrollment statistics by category. */
public class CategoryPanel extends JPanel {

    private final AthleteService athleteService;
    private final CategoryService categoryService;
    private final IDisciplineService disciplineService;

    private final JPanel cardsRow = new JPanel(new GridLayout(0, 2, 18, 16));
    private final JLabel lblSummary = new JLabel();
    private final ModernButton addButton = new ModernButton(
            "Nueva categoría", Type.TAG, ModernButton.Style.PRIMARY, 175);
    private boolean busy;

    private record CategoryInput(String name, int minAge, int maxAge, BigDecimal monthlyFee) { }

    private record CategoryView(List<AgeCategory> categories, List<Athlete> athletes,
                                List<String> disciplines) { }

    public CategoryPanel(AthleteService athleteService, CategoryService categoryService,
                         IDisciplineService disciplineService) {
        this.athleteService = athleteService;
        this.categoryService = categoryService;
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

        addButton.addActionListener(event -> showCategoryForm(null));

        JPanel actions = new JPanel(new BorderLayout(14, 0));
        actions.setOpaque(false);
        actions.add(header, BorderLayout.CENTER);
        actions.add(addButton, BorderLayout.EAST);

        return actions;
    }

    public void refresh() {
        if (busy) {
            return;
        }

        setBusy(true, "Loading categories...");

        new SwingWorker<CategoryView, Void>() {
            @Override
            protected CategoryView doInBackground() {
                List<AgeCategory> categories = categoryService.getCategories();
                List<Athlete> athletes = athleteService.getAllAthletes();

                List<String> disciplines;
                try {
                    disciplines = disciplineService.getAvailableDisciplines();
                } catch (PersistenceException e) {
                    disciplines = List.of();
                }

                return new CategoryView(categories, athletes, disciplines);
            }

            @Override
            protected void done() {
                try {
                    renderCategories(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    lblSummary.setText("Category loading interrupted.");
                } catch (ExecutionException e) {
                    lblSummary.setText(e.getCause().getMessage());
                } finally {
                    busy = false;
                    addButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void renderCategories(CategoryView view) {
        cardsRow.removeAll();

        int total = view.athletes().size();
        long active = view.athletes().stream().filter(Athlete::isActive).count();

        lblSummary.setText(view.categories().size() + " categorías · "
                + total + " deportistas · " + active + " activos");

        for (AgeCategory category : view.categories()) {
            cardsRow.add(buildCategoryCard(category, view.athletes(), view.disciplines(), total));
        }

        cardsRow.revalidate();
        cardsRow.repaint();
    }

    private void setBusy(boolean busy, String message) {
        this.busy = busy;
        addButton.setEnabled(!busy);
        lblSummary.setText(message);
    }

    private void showCategoryForm(CategoryInput previous) {
        JTextField name = new JTextField(previous == null ? "" : previous.name(), 20);
        JSpinner minAge = ageSpinner(previous == null ? DataValidator.MIN_AGE : previous.minAge());
        JSpinner maxAge = ageSpinner(previous == null ? DataValidator.MIN_AGE : previous.maxAge());
        JTextField fee = new JTextField(previous == null ? "" : previous.monthlyFee().toString(), 12);

        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.add(new JLabel("Category name"));
        fields.add(name);
        fields.add(new JLabel("Minimum age"));
        fields.add(minAge);
        fields.add(new JLabel("Maximum age"));
        fields.add(maxAge);
        fields.add(new JLabel("Monthly fee (COP)"));
        fields.add(fee);

        JPanel form = new JPanel(new BorderLayout(0, 12));
        form.add(fields, BorderLayout.CENTER);
        form.add(new JLabel("<html>The selected ages will use this category for new enrollments.<br>"
                + "Current enrollments keep their category and fee.</html>"), BorderLayout.SOUTH);

        while (JOptionPane.showConfirmDialog(this, form, "New category",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                minAge.commitEdit();
                maxAge.commitEdit();

                BigDecimal monthlyFee = new BigDecimal(fee.getText().strip().replace(',', '.'));

                saveCategory(new CategoryInput(
                        name.getText(),
                        (int) minAge.getValue(),
                        (int) maxAge.getValue(),
                        monthlyFee
                ));

                return;
            } catch (NumberFormatException | ParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Enter whole ages and a fee such as 85000 or 85000,50.",
                        "Invalid category", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private JSpinner ageSpinner(int age) {
        return new JSpinner(new SpinnerNumberModel(
                age, DataValidator.MIN_AGE, DataValidator.MAX_AGE, 1));
    }

    private void saveCategory(CategoryInput input) {
        setBusy(true, "Saving category...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                categoryService.createCategory(
                        input.name(), input.minAge(), input.maxAge(), input.monthlyFee());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(CategoryPanel.this,
                            "Category created successfully.",
                            "New category", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    JOptionPane.showMessageDialog(CategoryPanel.this, cause.getMessage(),
                            "Unable to save category", JOptionPane.WARNING_MESSAGE);

                    if (cause instanceof ValidationException && isShowing()) {
                        setBusy(false, "Check the category details.");
                        showCategoryForm(input);

                        if (!busy) {
                            refresh();
                        }

                        return;
                    }
                }

                busy = false;
                refresh();
            }
        }.execute();
    }

    private JComponent buildCategoryCard(AgeCategory category, List<Athlete> all,
                                         List<String> disciplines, int total) {
        Color accent = ThemeConstants.PRIMARY_BLUE;

        List<Athlete> members = all.stream()
                .filter(athlete -> category.name().equals(athlete.getCategoryName()))
                .toList();

        long active = members.stream().filter(Athlete::isActive).count();

        double income = members.stream()
                .filter(Athlete::isActive)
                .mapToDouble(Athlete::getMonthlyFee)
                .sum();

        CardPanel card = new CardPanel(new BorderLayout(), 18);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.add(new IconBadge(Type.TAG, accent, 46), BorderLayout.WEST);

        JPanel names = new JPanel();
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.setOpaque(false);

        JLabel name = label(categoryLabel(category.name()),
                new Font("Segoe UI", Font.BOLD, 20), ThemeConstants.TEXT_MAIN);

        JLabel ages = label(rangeText(category.ages()),
                ThemeConstants.FONT_REGULAR, ThemeConstants.TEXT_MUTED);

        names.add(Box.createVerticalGlue());
        names.add(name);
        ages.setToolTipText(ages.getText());
        names.add(ages);
        names.add(Box.createVerticalGlue());

        top.add(names, BorderLayout.CENTER);
        content.add(top);
        content.add(Box.createVerticalStrut(18));

        JLabel fee = label(Money.cop(category.monthlyFee().doubleValue()),
                new Font("Segoe UI", Font.BOLD, 30), accent);
        fee.setAlignmentX(LEFT_ALIGNMENT);

        JLabel feeCaption = label("tarifa mensual",
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED);

        content.add(fee);
        content.add(feeCaption);
        content.add(Box.createVerticalStrut(16));
        content.add(separator());
        content.add(Box.createVerticalStrut(14));

        content.add(metricRow("Deportistas", String.valueOf(members.size())));
        content.add(Box.createVerticalStrut(8));
        content.add(metricRow("Activos", String.valueOf(active)));
        content.add(Box.createVerticalStrut(8));
        content.add(metricRow("Ingreso mensual estimado", Money.cop(income)));
        content.add(Box.createVerticalStrut(14));

        ShareBar share = new ShareBar(accent);
        share.setAlignmentX(LEFT_ALIGNMENT);
        share.setValue(total == 0 ? 0 : (double) members.size() / total);
        content.add(share);

        int pct = total == 0 ? 0 : (int) Math.round(100.0 * members.size() / total);
        JLabel shareText = label(pct + "% del club",
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED);
        shareText.setBorder(new EmptyBorder(4, 0, 0, 0));

        content.add(shareText);
        content.add(Box.createVerticalStrut(16));
        content.add(separator());
        content.add(Box.createVerticalStrut(12));

        content.add(label("POR DISCIPLINA", ThemeConstants.FONT_SECTION, ThemeConstants.PLACEHOLDER));
        content.add(Box.createVerticalStrut(8));

        for (String discipline : disciplines) {
            long count = members.stream()
                    .filter(athlete -> discipline.equalsIgnoreCase(athlete.getDiscipline()))
                    .count();

            content.add(metricRow(SpanishLabels.of(discipline), String.valueOf(count)));
            content.add(Box.createVerticalStrut(6));
        }

        card.add(content, BorderLayout.NORTH);

        return card;
    }

    private static String categoryLabel(String name) {
        return switch (name) {
            case "CHILDREN", "YOUTH", "ADULTS" -> SpanishLabels.of(name);
            default -> name;
        };
    }

    private static String rangeText(List<Integer> ages) {
        if (ages.isEmpty()) {
            return "Sin edades para nuevas afiliaciones";
        }

        StringJoiner ranges = new StringJoiner(", ");
        int start = ages.get(0);
        int end = start;

        for (int age : ages.subList(1, ages.size())) {
            if (age != end + 1) {
                ranges.add(start == end ? Integer.toString(start) : start + "–" + end);
                start = age;
            }
            end = age;
        }

        ranges.add(start == end ? Integer.toString(start) : start + "–" + end);
        return ranges + " años";
    }

    private static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private static JComponent metricRow(String key, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);

        JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(ThemeConstants.FONT_REGULAR);
        keyLabel.setForeground(ThemeConstants.TEXT_MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(ThemeConstants.FONT_BOLD);
        valueLabel.setForeground(ThemeConstants.TEXT_MAIN);

        row.add(keyLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        return row;
    }

    private static JComponent separator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(ThemeConstants.BORDER_COLOR);
        separator.setAlignmentX(LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }
}