package com.creatinaboys.ui.panels;

import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.Footballer;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario interactivo de registro de deportistas (Mockups 3 y 5).
 */
public class AthleteRegisterPanel extends JPanel {

    private final AthleteService athleteService;

    private JTextField txtId, txtName, txtAge, txtPhone, txtJersey, txtSpecific;
    private JComboBox<String> cmbDiscipline;
    private JLabel lblSpecific;

    public AthleteRegisterPanel(AthleteService athleteService) {
        this.athleteService = athleteService;
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initUI();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("Registrar Deportista");
        lblTitle.setFont(ThemeConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeConstants.TEXT_MAIN);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = createTextField();
        txtName = createTextField();
        txtAge = createTextField();
        txtPhone = createTextField();
        txtJersey = createTextField();
        txtSpecific = createTextField();

        cmbDiscipline = new JComboBox<>(new String[]{"Fútbol", "Baloncesto"});
        cmbDiscipline.setFont(ThemeConstants.FONT_REGULAR);
        cmbDiscipline.setPreferredSize(new Dimension(250, 38));
        cmbDiscipline.addActionListener(e -> updateDisciplineFields());

        lblSpecific = new JLabel("Posición de juego:");
        lblSpecific.setFont(ThemeConstants.FONT_BOLD);

        // Fila 0
        addFormField(formCard, "Documento de Identidad *", txtId, gbc, 0, 0);
        addFormField(formCard, "Nombre Completo *", txtName, gbc, 1, 0);

        // Fila 1
        addFormField(formCard, "Edad *", txtAge, gbc, 0, 1);
        addFormField(formCard, "Teléfono *", txtPhone, gbc, 1, 1);

        // Fila 2
        addFormField(formCard, "Número de Camiseta (Dorsal) *", txtJersey, gbc, 0, 2);
        addFormField(formCard, "Disciplina *", cmbDiscipline, gbc, 1, 2);

        // Fila 3
        addFormField(formCard, lblSpecific.getText(), txtSpecific, gbc, 0, 3);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setFont(ThemeConstants.FONT_BOLD);
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnCancel.addActionListener(e -> clearForm());

        JButton btnSave = new JButton("Guardar");
        btnSave.setFont(ThemeConstants.FONT_BOLD);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(ThemeConstants.PRIMARY_BLUE);
        btnSave.setPreferredSize(new Dimension(140, 40));
        btnSave.addActionListener(e -> saveAthlete());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formCard.add(btnPanel, gbc);

        add(formCard, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, String label, JComponent comp, GridBagConstraints gbc, int col, int row) {
        gbc.gridx = col;
        gbc.gridy = row;

        JPanel fieldGroup = new JPanel(new BorderLayout(5, 5));
        fieldGroup.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConstants.FONT_BOLD);
        lbl.setForeground(ThemeConstants.TEXT_MAIN);

        fieldGroup.add(lbl, BorderLayout.NORTH);
        fieldGroup.add(comp, BorderLayout.CENTER);

        panel.add(fieldGroup, gbc);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(ThemeConstants.FONT_REGULAR);
        field.setPreferredSize(new Dimension(250, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void updateDisciplineFields() {
        String selected = (String) cmbDiscipline.getSelectedItem();
        if ("Fútbol".equals(selected)) {
            lblSpecific.setText("Posición de juego:");
        } else {
            lblSpecific.setText("Estatura (metros, ej. 1.85):");
        }
        revalidate();
        repaint();
    }

    private void saveAthlete() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            int age = Integer.parseInt(txtAge.getText().trim());
            String phone = txtPhone.getText().trim();
            int jersey = Integer.parseInt(txtJersey.getText().trim());
            String discipline = (String) cmbDiscipline.getSelectedItem();

            if ("Fútbol".equals(discipline)) {
                Footballer f = new Footballer(id, name, age, phone, jersey, txtSpecific.getText().trim());
                athleteService.registerAthlete(f);
                JOptionPane.showMessageDialog(this, 
                        "Categoría Asignada: " + f.getCategory().getName() + "\nTarifa Mensual: $" + String.format("%,.0f", f.getMonthlyFee()) + " COP",
                        "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                double height = Double.parseDouble(txtSpecific.getText().trim());
                Basketballer b = new Basketballer(id, name, age, phone, jersey, height);
                athleteService.registerAthlete(b);
                JOptionPane.showMessageDialog(this, 
                        "Categoría Asignada: " + b.getCategory().getName() + "\nTarifa Mensual: $" + String.format("%,.0f", b.getMonthlyFee()) + " COP",
                        "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos ingresados: " + ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
        txtPhone.setText("");
        txtJersey.setText("");
        txtSpecific.setText("");
    }
}