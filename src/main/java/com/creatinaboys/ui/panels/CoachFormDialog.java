package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Coach;
import com.creatinaboys.ui.components.*;
import com.creatinaboys.ui.theme.ThemeConstants;
import com.creatinaboys.validation.DataValidator;
import com.creatinaboys.ui.theme.ModernComboBox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CoachFormDialog extends JDialog {

    @FunctionalInterface
    public interface Saver {
        void save(Coach coach) throws Exception;
    }

    private final Coach existing;
    private final Saver saver;
    private final List<String> disciplines;
    private boolean saved;

    private PlaceholderTextField txtId;
    private PlaceholderTextField txtName;
    private PlaceholderTextField txtPhone;
    private ModernComboBox cmbDiscipline;
    private ModernComboBox cmbCategory;
    private JLabel lblError;

    public CoachFormDialog(Window owner, Coach existing, List<String> disciplines, Saver saver) {
        super(owner, existing == null ? "Nuevo Entrenador" : "Editar Entrenador",
                ModalityType.APPLICATION_MODAL);

        this.existing = existing;
        this.saver = saver;
        this.disciplines = List.copyOf(disciplines);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeConstants.MAIN_BG);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(buildCard(), BorderLayout.CENTER);
        setContentPane(root);

        if (existing != null) {
            fillFromCoach(existing);
        }

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);

        getRootPane().registerKeyboardAction(
                event -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public boolean isSaved() {
        return saved;
    }

    private JComponent buildCard() {
        boolean editing = existing != null;
        CardPanel card = new CardPanel(new GridBagLayout(), 20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(6, 12, 6, 12);

        JLabel title = new JLabel(editing
                ? "Editar / Reasignar Entrenador"
                : "Registrar Entrenador");
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setForeground(ThemeConstants.TEXT_MAIN);

        JLabel subtitle = new JLabel(editing
                ? "El documento no se puede cambiar. Modifica los datos o reasigna disciplina y categoría."
                : "Completa los datos y asígnale una disciplina y una categoría.");
        subtitle.setFont(ThemeConstants.FONT_SMALL);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);

        txtId = new PlaceholderTextField("Ej: 1065432100");
        txtName = new PlaceholderTextField("Ej: Carlos Gómez");
        txtPhone = new PlaceholderTextField("Ej: 3001234567");

        cmbDiscipline = new ModernComboBox(
                disciplines.toArray(String[]::new),
                SpanishLabels::discipline
        );
        cmbCategory = new ModernComboBox(CoachLabels.categoryLabels());

        if (editing) {
            txtId.setEditable(false);
            txtId.setForeground(ThemeConstants.TEXT_MUTED);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 12, 14, 12);
        card.add(header, gbc);

        gbc.insets = new Insets(6, 12, 6, 12);
        addGroup(card, label("Documento de Identidad", true),
                new InputBox(VectorIcon.Type.ID, txtId), gbc, 0, 1);
        addGroup(card, label("Teléfono", false),
                new InputBox(VectorIcon.Type.PHONE, txtPhone), gbc, 1, 1);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        card.add(group(label("Nombre Completo", true),
                new InputBox(VectorIcon.Type.USER, txtName)), gbc);

        addGroup(card, label("Disciplina", true),
                new InputBox(VectorIcon.Type.BALL, cmbDiscipline), gbc, 0, 3);
        addGroup(card, label("Categoría", true),
                new InputBox(VectorIcon.Type.TAG, cmbCategory), gbc, 1, 3);

        lblError = new JLabel(" ");
        lblError.setFont(ThemeConstants.FONT_SMALL);
        lblError.setForeground(ThemeConstants.DANGER_RED);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 14, 0, 12);
        card.add(lblError, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setOpaque(false);

        ModernButton btnCancel = new ModernButton(
                "Cancelar", VectorIcon.Type.CLOSE, ModernButton.Style.SECONDARY, 140);
        btnCancel.addActionListener(event -> dispose());

        ModernButton btnSave = new ModernButton(
                "Guardar", VectorIcon.Type.CHECK, ModernButton.Style.PRIMARY, 160);
        btnSave.addActionListener(this::onSave);

        buttons.add(btnCancel);
        buttons.add(btnSave);
        getRootPane().setDefaultButton(btnSave);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 12, 0, 12);
        gbc.anchor = GridBagConstraints.EAST;
        card.add(buttons, gbc);

        return card;
    }

    private JLabel label(String text, boolean required) {
        JLabel label = new JLabel(required
                ? "<html>" + text + " <font color='#EF4444'>*</font></html>"
                : text);
        label.setFont(ThemeConstants.FONT_BOLD);
        label.setForeground(ThemeConstants.TEXT_MAIN);
        return label;
    }

    private JPanel group(JLabel label, JComponent box) {
        JPanel group = new JPanel(new BorderLayout(0, 6));
        group.setOpaque(false);
        group.add(label, BorderLayout.NORTH);
        group.add(box, BorderLayout.CENTER);
        return group;
    }

    private void addGroup(JPanel panel, JLabel label, JComponent box,
                          GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        panel.add(group(label, box), gbc);
    }

    private void fillFromCoach(Coach coach) {
        txtId.setText(coach.getId());
        txtName.setText(coach.getFullName());
        txtPhone.setText(coach.getPhone());
        cmbDiscipline.setSelectedItem(coach.getDiscipline());
        cmbCategory.setSelectedItem(CoachLabels.categoryLabel(coach.getCategory()));
    }

    private void onSave(ActionEvent event) {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();

            DataValidator.validateId(id);
            DataValidator.validateFullName(name);
            DataValidator.validatePhone(phone);

            Coach coach = new Coach(
                    id,
                    name,
                    phone,
                    (String) cmbDiscipline.getSelectedItem(),
                    CoachLabels.categoryFromLabel((String) cmbCategory.getSelectedItem()),
                    existing == null || existing.isActive()
            );

            saver.save(coach);
            saved = true;
            dispose();
        } catch (ValidationException | IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("No se pudo guardar: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        lblError.setText("<html>" + message + "</html>");
        pack();
    }
}