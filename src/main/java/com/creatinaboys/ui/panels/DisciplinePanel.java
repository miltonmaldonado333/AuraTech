package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.ui.components.CardPanel;
import com.creatinaboys.ui.components.IconBadge;
import com.creatinaboys.ui.components.InputBox;
import com.creatinaboys.ui.components.ModernButton;
import com.creatinaboys.ui.components.PlaceholderTextField;
import com.creatinaboys.ui.components.ShareBar;
import com.creatinaboys.ui.components.SpanishLabels;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Pantalla de Disciplinas: catálogo del club con deportistas por disciplina y su
 * reparto por categoría, más un formulario para agregar disciplinas nuevas.
 */
public class DisciplinePanel extends JPanel {

    private static final Color[] ACCENTS = {
            ThemeConstants.PRIMARY_BLUE,
            new Color(249, 115, 22),
            new Color(16, 185, 129),
            ThemeConstants.PRIMARY_INDIGO,
            new Color(236, 72, 153)
    };
    private static final Color OK_GREEN = new Color(22, 163, 74);

    private final AthleteService athleteService;
    private final IDisciplineService disciplineService;

    private final JPanel grid = new JPanel(new GridLayout(0, 2, 18, 18));
    private final JLabel lblSummary = new JLabel();
    private final JLabel lblFeedback = new JLabel(" ");
    private PlaceholderTextField txtName;

    public DisciplinePanel(AthleteService athleteService, IDisciplineService disciplineService) {
        this.athleteService = athleteService;
        this.disciplineService = disciplineService;

        setLayout(new BorderLayout());
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(new EmptyBorder(30, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);

        grid.setOpaque(false);
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(grid, BorderLayout.NORTH);

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

        JLabel title = new JLabel("Disciplinas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Oferta deportiva del club y distribución de los deportistas por disciplina.");
        subtitle.setFont(ThemeConstants.FONT_REGULAR);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        lblSummary.setFont(ThemeConstants.FONT_SMALL);
        lblSummary.setForeground(ThemeConstants.TEXT_MUTED);
        lblSummary.setAlignmentX(LEFT_ALIGNMENT);

        txtName = new PlaceholderTextField("Nombre de la nueva disciplina (ej. Voleibol)...");
        InputBox nameBox = new InputBox(Type.BALL, txtName);
        nameBox.setPreferredSize(new Dimension(400, 52));

        ModernButton btnAdd = new ModernButton("Agregar", Type.CHECK, ModernButton.Style.PRIMARY, 140);
        btnAdd.addActionListener(e -> addDiscipline());
        txtName.addActionListener(e -> addDiscipline());

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addRow.setOpaque(false);
        addRow.setAlignmentX(LEFT_ALIGNMENT);
        addRow.setBorder(new EmptyBorder(0, -10, 0, 0));
        addRow.add(nameBox);
        addRow.add(btnAdd);

        lblFeedback.setFont(ThemeConstants.FONT_SMALL);
        lblFeedback.setAlignmentX(LEFT_ALIGNMENT);
        lblFeedback.setBorder(new EmptyBorder(6, 0, 0, 0));

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(6));
        header.add(lblSummary);
        header.add(Box.createVerticalStrut(14));
        header.add(addRow);
        header.add(lblFeedback);
        return header;
    }

    private void addDiscipline() {
        String name = txtName.getText().trim();
        try {
            if (name.isEmpty()) {
                feedback("Escribe el nombre de la disciplina.", false);
            } else if (disciplineService.registerDiscipline(name)) {
                txtName.setText("");
                feedback("Disciplina \"" + SpanishLabels.of(name) + "\" agregada al club.", true);
                refresh();
            } else {
                feedback("Esa disciplina ya existe.", false);
            }
        } catch (PersistenceException | IllegalArgumentException ex) {
            feedback("No se pudo guardar: " + ex.getMessage(), false);
        }
    }

    private void feedback(String text, boolean ok) {
        lblFeedback.setForeground(ok ? OK_GREEN : ThemeConstants.DANGER_RED);
        lblFeedback.setText(text);
    }

    /** Vuelve a leer los datos. Se llama cada vez que se muestra la pantalla. */
    public void refresh() {
        grid.removeAll();

        List<String> disciplines;
        try {
            disciplines = disciplineService.getAvailableDisciplines();
        } catch (PersistenceException e) {
            disciplines = List.of();
            feedback("No se pudo leer el catálogo: " + e.getMessage(), false);
        }
        List<Athlete> athletes = athleteService.getAllAthletes();
        lblSummary.setText(disciplines.size() + " disciplinas · " + athletes.size() + " deportistas");

        for (int i = 0; i < disciplines.size(); i++) {
            grid.add(buildCard(disciplines.get(i), athletes, ACCENTS[i % ACCENTS.length]));
        }
        if (disciplines.size() % 2 == 1) {
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            grid.add(filler);
        }
        grid.revalidate();
        grid.repaint();
    }

    private JComponent buildCard(String discipline, List<Athlete> all, Color accent) {
        List<Athlete> members = all.stream()
                .filter(a -> discipline.equalsIgnoreCase(a.getDiscipline()))
                .toList();
        long active = members.stream().filter(Athlete::isActive).count();

        CardPanel card = new CardPanel(new BorderLayout(), 18);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.add(new IconBadge(Type.TROPHY, accent, 46), BorderLayout.WEST);

        JPanel names = new JPanel();
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.setOpaque(false);
        names.add(Box.createVerticalGlue());
        names.add(label(SpanishLabels.of(discipline), new Font("Segoe UI", Font.BOLD, 20), ThemeConstants.TEXT_MAIN));
        names.add(label(members.size() + (members.size() == 1 ? " deportista" : " deportistas")
                + " · " + active + (active == 1 ? " activo" : " activos"), ThemeConstants.FONT_REGULAR, ThemeConstants.TEXT_MUTED));
        names.add(Box.createVerticalGlue());
        top.add(names, BorderLayout.CENTER);

        content.add(top);
        content.add(Box.createVerticalStrut(16));
        content.add(label("POR CATEGORÍA", ThemeConstants.FONT_SECTION, ThemeConstants.PLACEHOLDER));
        content.add(Box.createVerticalStrut(10));

        for (CategoryEnum category : CategoryEnum.values()) {
            long n = members.stream().filter(a -> a.getCategory() == category).count();
            content.add(categoryRow(SpanishLabels.of(category.getName()), n, members.size(), accent));
            content.add(Box.createVerticalStrut(10));
        }

        card.add(content, BorderLayout.NORTH);
        return card;
    }

    private static JComponent categoryRow(String name, long count, int total, Color accent) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);

        JPanel text = new JPanel(new BorderLayout());
        text.setOpaque(false);
        JLabel k = new JLabel(name);
        k.setFont(ThemeConstants.FONT_REGULAR);
        k.setForeground(ThemeConstants.TEXT_MUTED);
        JLabel v = new JLabel(String.valueOf(count));
        v.setFont(ThemeConstants.FONT_BOLD);
        v.setForeground(ThemeConstants.TEXT_MAIN);
        text.add(k, BorderLayout.WEST);
        text.add(v, BorderLayout.EAST);

        ShareBar bar = new ShareBar(accent);
        bar.setValue(total == 0 ? 0 : (double) count / total);

        row.add(text, BorderLayout.NORTH);
        row.add(bar, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    private static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}