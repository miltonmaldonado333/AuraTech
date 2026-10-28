package com.creatinaboys.ui.panels;

import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Coach;
import com.creatinaboys.ui.components.*;
import com.creatinaboys.ui.theme.ThemeConstants;
import com.creatinaboys.validation.DataValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Diálogo modal para registrar un entrenador nuevo o editar/reasignar uno existente.
 * <p>
 * El diálogo no conoce la capa de servicio: recibe un {@link Saver} y solo se cierra
 * cuando el guardado tiene éxito; si falla, muestra el motivo dentro del mismo formulario.
 */
public class CoachFormDialog extends JDialog {

    /** Acción de guardado que ejecuta quien abre el diálogo (registrar o actualizar). */
    @FunctionalInterface
    public interface Saver {
        void save(Coach coach) throws Exception;
    }

    private final Coach existing;
    private final Saver saver;
    private boolean saved;

    private PlaceholderTextField txtId, txtName, txtPhone;
    private ModernComboBox cmbDiscipline, cmbCategory;
    private JLabel lblError;

    public CoachFormDialog(Window owner, Coach existing, Saver saver) {
        super(owner, existing == null ? "Nuevo Entrenador" : "Editar Entrenador",
                ModalityType.APPLICATION_MODAL);
        this.existing = existing;
        this.saver = saver;

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

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public boolean isSaved() {
        return saved;
    }

    // ------------------------------------------------------------------ UI
    private JComponent buildCard() {
        boolean editing = existing != null;
        CardPanel card = new CardPanel(new GridBagLayout(), 20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(6, 12, 6, 12);

        JLabel title = new JLabel(editing ? "Editar / Reasignar Entrenador" : "Registrar Entrenador");
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setForeground(ThemeConstants.TEXT_MAIN);
        JLabel subtitle = new JLabel(editing
                ? "El documento no se puede cambiar. Modifica los datos o reasigna disciplina y categoría."
                : "Completa los datos y asígnale una disciplina y una categoría.");
        subtitle.setFont(ThemeConstants.FONT_SMALL);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        head.add(title);
        head.add(Box.createVerticalStrut(3));
        head.add(subtitle);

        txtId = new PlaceholderTextField("Ej: 1065432100");
        txtName = new PlaceholderTextField("Ej: Carlos Gómez");
        txtPhone = new PlaceholderTextField("Ej: 3001234567");
        cmbDiscipline = new ModernComboBox(CoachLabels.DISCIPLINES_ES);
        cmbCategory = new ModernComboBox(CoachLabels.categoryLabels());

        if (editing) {
            txtId.setEditable(false);
            txtId.setForeground(ThemeConstants.TEXT_MUTED);
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 12, 14, 12);
        card.add(head, gbc);
        gbc.insets = new Insets(6, 12, 6, 12);

        addGroup(card, label("Documento de Identidad", true), new InputBox(VectorIcon.Type.ID, txtId), gbc, 0, 1);
        addGroup(card, label("Teléfono", false), new InputBox(VectorIcon.Type.PHONE, txtPhone), gbc, 1, 1);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        card.add(group(label("Nombre Completo", true), new InputBox(VectorIcon.Type.USER, txtName)), gbc);

        addGroup(card, label("Disciplina", true), new InputBox(VectorIcon.Type.BALL, cmbDiscipline), gbc, 0, 3);
        addGroup(card, label("Categoría", true), new InputBox(VectorIcon.Type.TAG, cmbCategory), gbc, 1, 3);

        lblError = new JLabel(" ");
        lblError.setFont(ThemeConstants.FONT_SMALL);
        lblError.setForeground(ThemeConstants.DANGER_RED);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 14, 0, 12);
        card.add(lblError, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setOpaque(false);
        ModernButton btnCancel = new ModernButton("Cancelar", VectorIcon.Type.CLOSE, ModernButton.Style.SECONDARY, 140);
        btnCancel.addActionListener(e -> dispose());
        ModernButton btnSave = new ModernButton("Guardar", VectorIcon.Type.CHECK, ModernButton.Style.PRIMARY, 160);
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
        JLabel l = new JLabel(required
                ? "<html>" + text + " <font color='#EF4444'>*</font></html>"
                : text);
        l.setFont(ThemeConstants.FONT_BOLD);
        l.setForeground(ThemeConstants.TEXT_MAIN);
        return l;
    }

    private JPanel group(JLabel label, JComponent box) {
        JPanel g = new JPanel(new BorderLayout(0, 6));
        g.setOpaque(false);
        g.add(label, BorderLayout.NORTH);
        g.add(box, BorderLayout.CENTER);
        return g;
    }

    private void addGroup(JPanel panel, JLabel label, JComponent box,
                          GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        panel.add(group(label, box), gbc);
    }

    private void fillFromCoach(Coach c) {
        txtId.setText(c.getId());
        txtName.setText(c.getFullName());
        txtPhone.setText(c.getPhone());
        cmbDiscipline.setSelectedItem(SpanishLabels.of(c.getDiscipline()));
        cmbCategory.setSelectedItem(CoachLabels.categoryLabel(c.getCategory()));
    }

    // ------------------------------------------------------------------ Lógica
    private void onSave(ActionEvent e) {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();

            DataValidator.validateId(id);
            DataValidator.validateFullName(name);
            DataValidator.validatePhone(phone);   // opcional: solo valida si se escribió algo

            Coach coach = new Coach(id, name, phone,
                    CoachLabels.disciplineValue((String) cmbDiscipline.getSelectedItem()),
                    CoachLabels.categoryFromLabel((String) cmbCategory.getSelectedItem()),
                    existing == null || existing.isActive());

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