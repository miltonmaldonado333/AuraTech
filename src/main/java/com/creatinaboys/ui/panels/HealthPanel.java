package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.FitnessStatusEnum;
import com.creatinaboys.model.HealthRecord;
import com.creatinaboys.model.InjuryRecord;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.IHealthService;
import com.creatinaboys.ui.components.*;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Pantalla de fichas de salud (RF6): estado físico, restricciones e historial de lesiones.
 * La ficha pertenece a la persona (documento), no a cada disciplina.
 * <p>
 * Toda la lógica vive en {@link IHealthService}; esta clase solo muestra datos y recoge la entrada.
 * </p>
 */
public class HealthPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color OK_GREEN = new Color(22, 163, 74);
    private static final Color AMBER = new Color(217, 119, 6);

    private static final String ST_TREATMENT = "En Tratamiento";
    private static final String ST_OBSERVATION = "En Observación";
    private static final String ST_RECOVERED = "Recuperado";
    private static final String[] INJURY_STATUS = {ST_TREATMENT, ST_OBSERVATION, ST_RECOVERED};

    private static final Map<String, Color[]> INJURY_PALETTE = Map.of(
            ST_TREATMENT, ModernTable.pill(new Color(254, 202, 202), new Color(153, 27, 27)),
            ST_OBSERVATION, ModernTable.pill(new Color(254, 240, 138), new Color(133, 77, 14)),
            ST_RECOVERED, ModernTable.pill(new Color(187, 247, 208), new Color(22, 101, 52))
    );

    /** Persona única (documento + nombre) para el combo. */
    private record Person(String id, String name) { }

    private final AthleteService athleteService;
    private final IHealthService healthService;

    // Resumen
    private final JLabel lblFit = statValue();
    private final JLabel lblRestricted = statValue();
    private final JLabel lblUnfit = statValue();
    private final JLabel lblInjuries = statValue();

    // Formulario
    private final ModernComboBox cmbPerson = new ModernComboBox(new String[0]);
    private final ModernComboBox cmbFitness = new ModernComboBox(descriptions());
    private final PlaceholderTextField txtRestrictions = new PlaceholderTextField("Ej: sin contacto, carga reducida");
    private final JLabel lblDocument = infoValue();
    private final JLabel lblCurrent = infoValue();
    private final JLabel lblCanTrain = infoValue();
    private final JLabel lblAssessed = infoValue();
    private final JLabel lblFeedback = new JLabel(" ");

    // Tabla
    private final JLabel lblInjuryCount = new JLabel(" ");
    private JTable injuriesTable;
    /** Lesiones en el mismo orden que las filas de la tabla. */
    private List<InjuryRecord> injuryRows = List.of();

    /** Personas en el mismo orden que los elementos del combo. */
    private List<Person> people = List.of();
    private boolean loading = false;

    public HealthPanel(AthleteService athleteService, IHealthService healthService) {
        this.athleteService = athleteService;
        this.healthService = healthService;

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
        lower.add(buildInjuriesCard(), BorderLayout.CENTER);
        center.add(lower, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        cmbPerson.addActionListener(e -> {
            if (!loading) {
                loadRecord();
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

        JLabel title = new JLabel("Fichas de Salud");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Controla la aptitud física, las restricciones y las lesiones de cada deportista.");
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
        row.add(statCard(Type.CHECK, new Color(16, 185, 129), lblFit, "Aptos"));
        row.add(statCard(Type.FLAG, new Color(245, 158, 11), lblRestricted, "Aptos con restricciones"));
        row.add(statCard(Type.CLOSE, new Color(239, 68, 68), lblUnfit, "No aptos"));
        row.add(statCard(Type.CHART, ThemeConstants.PRIMARY_INDIGO, lblInjuries, "Lesiones activas"));
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
        CardPanel card = new CardPanel(new BorderLayout(), 12);
        card.setPreferredSize(new Dimension(350, 0));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel("Ficha del deportista");
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(8));

        content.add(sectionLabel("DEPORTISTA"));
        content.add(Box.createVerticalStrut(6));
        content.add(fullWidth(new InputBox(Type.USER, cmbPerson)));
        content.add(Box.createVerticalStrut(8));

        JPanel info = new JPanel(new GridLayout(4, 2, 8, 4));
        info.setOpaque(false);
        info.setAlignmentX(LEFT_ALIGNMENT);
        info.add(infoKey("Documento"));
        info.add(lblDocument);
        info.add(infoKey("Estado actual"));
        info.add(lblCurrent);
        info.add(infoKey("¿Puede entrenar?"));
        info.add(lblCanTrain);
        info.add(infoKey("Última valoración"));
        info.add(lblAssessed);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        content.add(info);
        content.add(Box.createVerticalStrut(10));

        content.add(sectionLabel("ESTADO FÍSICO"));
        content.add(Box.createVerticalStrut(6));
        content.add(fullWidth(new InputBox(Type.CHECK, cmbFitness)));
        content.add(Box.createVerticalStrut(8));

        content.add(sectionLabel("RESTRICCIONES"));
        content.add(Box.createVerticalStrut(6));
        content.add(fullWidth(new InputBox(Type.FLAG, txtRestrictions)));
        content.add(Box.createVerticalStrut(8));

        ModernButton btnSave = new ModernButton("Guardar ficha", Type.CHECK, ModernButton.Style.PRIMARY, 200);
        btnSave.addActionListener(e -> saveRecord());
        txtRestrictions.addActionListener(e -> saveRecord());
        content.add(fullWidth(btnSave));

        lblFeedback.setFont(ThemeConstants.FONT_SMALL);
        lblFeedback.setAlignmentX(LEFT_ALIGNMENT);
        lblFeedback.setBorder(new EmptyBorder(10, 2, 0, 0));
        content.add(lblFeedback);

        card.add(content, BorderLayout.NORTH);
        return card;
    }

    private JComponent buildInjuriesCard() {
        CardPanel card = new CardPanel(new BorderLayout(0, 12), 14);

        JLabel title = new JLabel("Historial de lesiones");
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        title.setForeground(ThemeConstants.TEXT_MAIN);
        lblInjuryCount.setFont(ThemeConstants.FONT_SMALL);
        lblInjuryCount.setForeground(ThemeConstants.TEXT_MUTED);

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(2, 4, 0, 4));
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        title.setAlignmentX(LEFT_ALIGNMENT);
        lblInjuryCount.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(lblInjuryCount);

        ModernButton btnInjury = new ModernButton("Agregar lesión", Type.USER_PLUS, ModernButton.Style.SECONDARY, 170);
        btnInjury.addActionListener(e -> addInjury());
        head.add(titleBox, BorderLayout.WEST);
        head.add(btnInjury, BorderLayout.EAST);

        injuriesTable = ModernTable.create(
                new String[]{"Lesión", "Fecha", "Estado", "Observaciones"},
                new int[]{160, 95, 155, 190});
        ModernTable.setBadgeColumn(injuriesTable, 2, INJURY_PALETTE);
        injuriesTable.setToolTipText("Haz click en una lesión para editarla o eliminarla");
        injuriesTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        injuriesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = injuriesTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < injuryRows.size()) {
                    injuriesTable.setRowSelectionInterval(row, row);
                    editInjury(injuryRows.get(row));
                }
            }
        });

        card.add(head, BorderLayout.NORTH);
        card.add(ModernTable.wrap(injuriesTable), BorderLayout.CENTER);
        return card;
    }

    // ------------------------------------------------------------------ Datos

    /** Recarga la lista de personas (una por documento), el resumen y la ficha seleccionada. */
    public void refresh() {
        loading = true;
        Person previous = selected();

        Map<String, Person> unique = new LinkedHashMap<>();
        for (Athlete a : athleteService.getAllAthletes()) {
            unique.putIfAbsent(a.getId(), new Person(a.getId(), a.getFullName()));
        }
        people = new ArrayList<>(unique.values());

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        int keepIndex = 0;
        for (int i = 0; i < people.size(); i++) {
            Person p = people.get(i);
            model.addElement(p.name());
            if (previous != null && previous.id().equals(p.id())) {
                keepIndex = i;
            }
        }
        cmbPerson.setModel(model);
        if (!people.isEmpty()) {
            cmbPerson.setSelectedIndex(keepIndex);
        }
        loading = false;

        reloadStats();
        loadRecord();
    }

    private void reloadStats() {
        int fit = 0, restricted = 0, unfit = 0, active = 0;
        try {
            for (HealthRecord r : healthService.getAllHealthRecords()) {
                switch (r.getFitnessStatus()) {
                    case FIT -> fit++;
                    case FIT_WITH_RESTRICTIONS -> restricted++;
                    case UNFIT -> unfit++;
                }
                for (InjuryRecord i : r.getInjuries()) {
                    if (!ST_RECOVERED.equals(i.getStatus())) {
                        active++;
                    }
                }
            }
        } catch (PersistenceException ex) {
            feedback("No se pudo leer el resumen: " + ex.getMessage(), false);
        }
        lblFit.setText(String.valueOf(fit));
        lblRestricted.setText(String.valueOf(restricted));
        lblUnfit.setText(String.valueOf(unfit));
        lblInjuries.setText(String.valueOf(active));
    }

    private void loadRecord() {
        DefaultTableModel model = (DefaultTableModel) injuriesTable.getModel();
        model.setRowCount(0);
        injuryRows = List.of();
        lblInjuryCount.setText(" ");

        Person p = selected();
        if (p == null) {
            showSummary(null, true);
            feedback("No hay deportistas registrados.", false);
            return;
        }
        try {
            Optional<HealthRecord> opt = healthService.getHealthRecordByAthleteId(p.id());
            if (opt.isEmpty()) {
                cmbFitness.setSelectedIndex(FitnessStatusEnum.FIT.ordinal());
                txtRestrictions.setText("");
                showSummary(null, true);
                lblInjuryCount.setText("Sin lesiones");
                return;
            }
            HealthRecord r = opt.get();
            cmbFitness.setSelectedIndex(r.getFitnessStatus().ordinal());
            txtRestrictions.setText(r.getRestrictions());
            injuryRows = new ArrayList<>(r.getInjuries());
            for (InjuryRecord i : r.getInjuries()) {
                model.addRow(new Object[]{i.getDescription(), i.getDate().format(DATE_FMT),
                        i.getStatus(), i.getObservations().isBlank() ? "—" : i.getObservations()});
            }
            int n = r.getInjuries().size();
            lblInjuryCount.setText(n == 0 ? "Sin lesiones" : n + (n == 1 ? " lesión" : " lesiones"));
            showSummary(r, healthService.isAthleteFitToTrain(p.id()));
        } catch (PersistenceException ex) {
            showError("No se pudo leer la ficha:\n" + ex.getMessage());
        }
    }

    /** Muestra el estado actual de la ficha; {@code r == null} significa "sin ficha registrada". */
    private void showSummary(HealthRecord r, boolean canTrain) {
        Person sel = selected();
        lblDocument.setText(sel == null ? "—" : sel.id());
        if (r == null) {
            lblCurrent.setText("Sin ficha");
            lblCurrent.setForeground(ThemeConstants.TEXT_MUTED);
            lblCanTrain.setText(selected() == null ? "—" : "Sí");
            lblCanTrain.setForeground(selected() == null ? ThemeConstants.TEXT_MAIN : OK_GREEN);
            lblAssessed.setText("—");
            return;
        }
        lblCurrent.setText(r.getFitnessStatus().getDescription());
        lblCurrent.setForeground(switch (r.getFitnessStatus()) {
            case FIT -> OK_GREEN;
            case FIT_WITH_RESTRICTIONS -> AMBER;
            case UNFIT -> ThemeConstants.DANGER_RED;
        });
        lblCanTrain.setText(canTrain ? "Sí" : "No");
        lblCanTrain.setForeground(canTrain ? OK_GREEN : ThemeConstants.DANGER_RED);
        lblAssessed.setText(r.getAssessmentDate().format(DATE_FMT));
    }

    // ------------------------------------------------------------------ Acciones

    private void saveRecord() {
        Person p = selected();
        if (p == null) {
            feedback("Selecciona un deportista.", false);
            return;
        }
        try {
            healthService.updateFitnessStatus(p.id(), selectedFitness(),
                    txtRestrictions.getText().trim(), LocalDate.now());
            reloadStats();
            loadRecord();
            feedback("Ficha de " + p.name() + " guardada.", true);
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        } catch (AthleteNotFoundException ex) {
            feedback("El deportista ya no existe.", false);
            refresh();
        } catch (PersistenceException ex) {
            showError("No se pudo guardar la ficha:\n" + ex.getMessage());
        }
    }

    private void addInjury() {
        Person p = selected();
        if (p == null) {
            feedback("Selecciona un deportista.", false);
            return;
        }

        PlaceholderTextField txtDesc = new PlaceholderTextField("Ej: esguince de tobillo");
        ModernComboBox cbStatus = new ModernComboBox(INJURY_STATUS);
        PlaceholderTextField txtObs = new PlaceholderTextField("Opcional");
        JPanel form = injuryForm(txtDesc, cbStatus, txtObs);

        int option = JOptionPane.showConfirmDialog(this, form, "Nueva lesión · " + p.name(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            InjuryRecord injury = new InjuryRecord(UUID.randomUUID().toString(),
                    txtDesc.getText(), LocalDate.now(),
                    (String) cbStatus.getSelectedItem(), txtObs.getText().trim());
            healthService.addInjuryToAthlete(p.id(), injury);
            reloadStats();
            loadRecord();
            feedback("Lesión registrada para " + p.name() + ".", true);
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        } catch (AthleteNotFoundException ex) {
            feedback("El deportista ya no existe.", false);
            refresh();
        } catch (PersistenceException ex) {
            showError("No se pudo guardar la lesión:\n" + ex.getMessage());
        }
    }

    /** Click en una lesión de la tabla: permite cambiar sus datos o eliminarla. */
    private void editInjury(InjuryRecord injury) {
        Person p = selected();
        if (p == null) {
            return;
        }
        PlaceholderTextField txtDesc = new PlaceholderTextField("Ej: esguince de tobillo");
        ModernComboBox cbStatus = new ModernComboBox(INJURY_STATUS);
        PlaceholderTextField txtObs = new PlaceholderTextField("Opcional");
        txtDesc.setText(injury.getDescription());
        cbStatus.setSelectedItem(injury.getStatus());
        txtObs.setText(injury.getObservations());

        String[] options = {"Guardar cambios", "Eliminar", "Cancelar"};
        int choice = JOptionPane.showOptionDialog(this, injuryForm(txtDesc, cbStatus, txtObs),
                "Lesión · " + p.name(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        try {
            if (choice == 0) {
                if (healthService.updateInjury(p.id(), injury.getId(), txtDesc.getText(),
                        (String) cbStatus.getSelectedItem(), txtObs.getText().trim())) {
                    reloadStats();
                    loadRecord();
                    feedback("Lesión actualizada.", true);
                } else {
                    feedback("La lesión ya no existe.", false);
                    loadRecord();
                }
            } else if (choice == 1) {
                int ok = JOptionPane.showConfirmDialog(this,
                        "¿Eliminar la lesión \"" + injury.getDescription() + "\"?",
                        "Eliminar lesión", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ok != JOptionPane.YES_OPTION) {
                    injuriesTable.clearSelection();
                    return;
                }
                if (healthService.removeInjury(p.id(), injury.getId())) {
                    reloadStats();
                    loadRecord();
                    feedback("Lesión eliminada.", true);
                } else {
                    feedback("La lesión ya no existe.", false);
                    loadRecord();
                }
            } else {
                injuriesTable.clearSelection();
            }
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        } catch (AthleteNotFoundException ex) {
            feedback("El deportista ya no existe.", false);
            refresh();
        } catch (PersistenceException ex) {
            showError("No se pudo guardar el cambio:\n" + ex.getMessage());
        }
    }

    private static JPanel injuryForm(PlaceholderTextField txtDesc, ModernComboBox cbStatus, PlaceholderTextField txtObs) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(4, 4, 4, 4));
        form.add(sectionLabel("LESIÓN"));
        form.add(Box.createVerticalStrut(6));
        form.add(fullWidth(new InputBox(Type.FLAG, txtDesc)));
        form.add(Box.createVerticalStrut(12));
        form.add(sectionLabel("ESTADO"));
        form.add(Box.createVerticalStrut(6));
        form.add(fullWidth(new InputBox(Type.CHECK, cbStatus)));
        form.add(Box.createVerticalStrut(12));
        form.add(sectionLabel("OBSERVACIONES"));
        form.add(Box.createVerticalStrut(6));
        form.add(fullWidth(new InputBox(Type.EDIT, txtObs)));
        return form;
    }

    // ------------------------------------------------------------------ Utilidades

    private Person selected() {
        int i = cmbPerson.getSelectedIndex();
        return (i >= 0 && i < people.size()) ? people.get(i) : null;
    }

    private FitnessStatusEnum selectedFitness() {
        int i = cmbFitness.getSelectedIndex();
        FitnessStatusEnum[] all = FitnessStatusEnum.values();
        return (i >= 0 && i < all.length) ? all[i] : FitnessStatusEnum.FIT;
    }

    private static String[] descriptions() {
        FitnessStatusEnum[] all = FitnessStatusEnum.values();
        String[] out = new String[all.length];
        for (int i = 0; i < all.length; i++) {
            out[i] = all[i].getDescription();
        }
        return out;
    }

    private void feedback(String text, boolean ok) {
        lblFeedback.setForeground(ok ? OK_GREEN : ThemeConstants.DANGER_RED);
        lblFeedback.setText("<html><body style='width:240px'>" + text + "</body></html>");
    }

    private void warn(String msg) {
        feedback("Revisa los datos.", false);
        JOptionPane.showMessageDialog(this, msg, "Revisa los datos", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
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
}