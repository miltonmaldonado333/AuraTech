package com.creatinaboys.ui.panels;

import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.Footballer;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.ui.components.*;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Formulario moderno de registro de deportistas.
 *
 * @author Milton Andrés Maldonado Salcedo
 */
public class AthleteRegisterPanel extends JPanel {

    private final AthleteService athleteService;

    private PlaceholderTextField txtId, txtName, txtAge, txtPhone, txtJersey, txtSpecific;
    private ModernComboBox cmbDiscipline;
    private InputBox boxSpecific;
    private JLabel lblSpecific;

    public AthleteRegisterPanel(AthleteService athleteService) {
        this.athleteService = athleteService;

        setLayout(new BorderLayout());
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(new EmptyBorder(30, 32, 20, 32));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildScrollableForm(), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ Encabezado
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 8, 14, 0));

        JComponent badge = new JComponent() {
            final VectorIcon ic = new VectorIcon(Type.USER_PLUS, 26, Color.WHITE);
            { setPreferredSize(new Dimension(54, 54)); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, ThemeConstants.PRIMARY_BLUE,
                        54, 54, ThemeConstants.PRIMARY_INDIGO));
                g2.fillRoundRect(0, 0, 54, 54, 18, 18);
                ic.paintIcon(this, g2, 14, 14);
                g2.dispose();
            }
        };

        JLabel title = new JLabel("Registrar Deportista");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);

        JLabel subtitle = new JLabel("Completa la información para afiliar un nuevo deportista al club.");
        subtitle.setFont(ThemeConstants.FONT_REGULAR);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        texts.add(title);
        texts.add(Box.createVerticalStrut(3));
        texts.add(subtitle);

        header.add(badge, BorderLayout.WEST);
        header.add(texts, BorderLayout.CENTER);
        return header;
    }

    // ------------------------------------------------------------------ Formulario
    private JComponent buildScrollableForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(buildCard(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JComponent buildCard() {
        CardPanel card = new CardPanel(new GridBagLayout(), 26);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(8, 12, 8, 12);

        txtId = field("Ej: 1065432100");
        txtName = field("Ej: Juan Pérez");
        txtAge = field("Ej: 10");
        txtPhone = field("Ej: 3001234567");
        txtJersey = field("Ej: 10");
        txtSpecific = field("Ej: Delantero / Volante");

        cmbDiscipline = new ModernComboBox(new String[]{"Fútbol", "Baloncesto"});
        cmbDiscipline.addActionListener(e -> updateDisciplineFields());

        boxSpecific = new InputBox(Type.FLAG, txtSpecific);
        lblSpecific = label("Posición de juego");

        // --- Sección: personal
        addFull(card, section("INFORMACIÓN PERSONAL", Type.USER), gbc, 0);
        addGroup(card, label("Documento de Identidad"), new InputBox(Type.ID, txtId), gbc, 0, 1);
        addGroup(card, label("Nombre Completo"), new InputBox(Type.USER, txtName), gbc, 1, 1);
        addGroup(card, label("Edad"), new InputBox(Type.CALENDAR, txtAge), gbc, 0, 2);
        addGroup(card, label("Teléfono"), new InputBox(Type.PHONE, txtPhone), gbc, 1, 2);

        // --- Sección: deportiva
        gbc.insets = new Insets(20, 12, 8, 12);
        addFull(card, section("INFORMACIÓN DEPORTIVA", Type.TROPHY), gbc, 3);
        gbc.insets = new Insets(8, 12, 8, 12);
        addGroup(card, label("Número de Camiseta (Dorsal)"), new InputBox(Type.HASH, txtJersey), gbc, 0, 4);
        addGroup(card, label("Disciplina"), new InputBox(Type.BALL, cmbDiscipline), gbc, 1, 4);
        addGroup(card, lblSpecific, boxSpecific, gbc, 0, 5);

        // --- Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setOpaque(false);

        ModernButton btnCancel = new ModernButton("Cancelar", Type.CLOSE, ModernButton.Style.SECONDARY, 140);
        btnCancel.addActionListener(e -> clearForm());
        ModernButton btnSave = new ModernButton("Guardar", Type.CHECK, ModernButton.Style.PRIMARY, 160);
        btnSave.addActionListener(e -> saveAthlete());
        buttons.add(btnCancel);
        buttons.add(btnSave);

        gbc.insets = new Insets(26, 12, 0, 12);
        gbc.anchor = GridBagConstraints.EAST;
        addFull(card, buttons, gbc, 6);

        return card;
    }

    // ------------------------------------------------------------------ Helpers de UI
    private PlaceholderTextField field(String placeholder) {
        return new PlaceholderTextField(placeholder);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel();
        l.setFont(ThemeConstants.FONT_BOLD);
        l.setForeground(ThemeConstants.TEXT_MAIN);
        setRequiredText(l, text);
        return l;
    }

    private void setRequiredText(JLabel l, String text) {
        l.setText("<html>" + text + " <font color='#EF4444'>*</font></html>");
    }

    private JComponent section(String text, Type iconType) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        JLabel l = new JLabel(text, new VectorIcon(iconType, 16, ThemeConstants.PRIMARY_BLUE), SwingConstants.LEFT);
        l.setIconTextGap(8);
        l.setFont(ThemeConstants.FONT_SECTION);
        l.setForeground(ThemeConstants.TEXT_MUTED);

        JComponent line = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(ThemeConstants.BORDER_COLOR);
                g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        };
        p.add(l, BorderLayout.WEST);
        p.add(line, BorderLayout.CENTER);
        return p;
    }

    private void addGroup(JPanel panel, JLabel label, JComponent box, GridBagConstraints gbc, int x, int y) {
        JPanel group = new JPanel(new BorderLayout(0, 6));
        group.setOpaque(false);
        group.add(label, BorderLayout.NORTH);
        group.add(box, BorderLayout.CENTER);

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(group, gbc);
    }

    private void addFull(JPanel panel, JComponent comp, GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        panel.add(comp, gbc);
    }

    // ------------------------------------------------------------------ Lógica
    private void updateDisciplineFields() {
        boolean football = "Fútbol".equals(cmbDiscipline.getSelectedItem());
        setRequiredText(lblSpecific, football ? "Posición de juego" : "Estatura (metros, ej: 1.85)");
        txtSpecific.setPlaceholder(football ? "Ej: Delantero / Volante" : "Ej: 1.85");
        boxSpecific.setIcon(football ? Type.FLAG : Type.RULER);
        txtSpecific.setText("");
        revalidate();
        repaint();
    }

    private void saveAthlete() {
        try {
            String id = required(txtId);
            String name = required(txtName);
            int age = Integer.parseInt(required(txtAge));
            String phone = required(txtPhone);
            int jersey = Integer.parseInt(required(txtJersey));
            String discipline = (String) cmbDiscipline.getSelectedItem();
            String specific = required(txtSpecific);

            if ("Fútbol".equals(discipline)) {
                Footballer f = new Footballer(id, name, age, phone, jersey, specific);
                athleteService.registerAthlete(f);
                showSuccessDialog(f.getCategory().getName(), f.getMonthlyFee());
            } else {
                double height = Double.parseDouble(specific.replace(',', '.'));
                Basketballer b = new Basketballer(id, name, age, phone, jersey, height);
                athleteService.registerAthlete(b);
                showSuccessDialog(b.getCategory().getName(), b.getMonthlyFee());
            }
            clearForm();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa números válidos para edad, dorsal o estatura.",
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el atleta: " + ex.getMessage(),
                    "Error al Registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String required(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.requestFocusInWindow();
            throw new IllegalArgumentException("Todos los campos marcados con (*) son obligatorios.");
        }
        return text;
    }

    private void showSuccessDialog(String category, double fee) {
        JOptionPane.showMessageDialog(this,
                "¡Deportista afiliado correctamente!\n\n" +
                        "• Categoría: " + category + "\n" +
                        "• Tarifa Mensual: $" + String.format("%,.0f", fee) + " COP",
                "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{txtId, txtName, txtAge, txtPhone, txtJersey}) {
            f.setText("");
        }
        cmbDiscipline.setSelectedIndex(0);
        updateDisciplineFields();
    }
}