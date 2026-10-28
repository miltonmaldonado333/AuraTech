package com.creatinaboys.ui.panels;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tabla de consulta de deportistas con botones personalizados de Buscar y Eliminar.
 * 
 * @author Milton Andrés Maldonado Salcedo
 */
public class AthleteListPanel extends JPanel {

    private final AthleteService athleteService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public AthleteListPanel(AthleteService athleteService) {
        this.athleteService = athleteService;
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initHeader();
        initTable();
        initFooter();
        loadAthleteData();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 10));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Lista de Deportistas");
        lblTitle.setFont(ThemeConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeConstants.TEXT_MAIN);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(25);
        txtSearch.setFont(ThemeConstants.FONT_REGULAR);
        txtSearch.setPreferredSize(new Dimension(320, 40));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnSearch = createCustomButton("Buscar", ThemeConstants.PRIMARY_BLUE, Color.WHITE, ThemeConstants.PRIMARY_HOVER, Color.WHITE, 100);
        btnSearch.addActionListener(e -> filterData(txtSearch.getText().trim()));

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"ID", "Nombre", "Edad", "Categoría", "Disciplina", "Estado", "Tarifa (COP)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(ThemeConstants.FONT_REGULAR);
        table.setRowHeight(40);
        table.getTableHeader().setFont(ThemeConstants.FONT_BOLD);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(ThemeConstants.TEXT_MAIN);
        table.setShowVerticalLines(false);
        table.setGridColor(ThemeConstants.BORDER_COLOR);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void initFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        footerPanel.setOpaque(false);

        JButton btnDelete = createCustomButton("Eliminar Seleccionado", ThemeConstants.DANGER_RED, Color.WHITE, ThemeConstants.DANGER_HOVER, Color.WHITE, 180);
        btnDelete.addActionListener(e -> deleteSelectedAthlete());

        footerPanel.add(btnDelete);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadAthleteData() {
        tableModel.setRowCount(0);
        if (athleteService == null) return;

        List<Athlete> list = athleteService.getAllAthletes();
        for (Athlete a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getFullName(),
                    a.getAge(),
                    a.getCategory() != null ? a.getCategory().getName() : "N/A",
                    a.getDiscipline(),
                    "Activo",
                    "$" + String.format("%,.0f", a.getMonthlyFee())
            });
        }
    }

    private void filterData(String query) {
        if (query.isEmpty()) {
            loadAthleteData();
            return;
        }
        tableModel.setRowCount(0);
        athleteService.getAllAthletes().stream()
                .filter(a -> a.getId().contains(query) || 
                             a.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                             a.getDiscipline().toLowerCase().contains(query.toLowerCase()))
                .forEach(a -> tableModel.addRow(new Object[]{
                        a.getId(),
                        a.getFullName(),
                        a.getAge(),
                        a.getCategory() != null ? a.getCategory().getName() : "N/A",
                        a.getDiscipline(),
                        "Activo",
                        "$" + String.format("%,.0f", a.getMonthlyFee())
                }));
    }

    private void deleteSelectedAthlete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un deportista de la tabla para eliminar.", "Ninguna Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar a " + name + " (ID: " + id + ")?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                athleteService.deleteAthlete(id);
                loadAthleteData();
                JOptionPane.showMessageDialog(this, "Deportista eliminado correctamente.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createCustomButton(String text, Color bgNormal, Color fgNormal, Color bgHover, Color fgHover, int width) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                FontMetrics fm = g2.getFontMetrics();
                Rectangle bounds = fm.getStringBounds(getText(), g2).getBounds();
                int x = (getWidth() - bounds.width) / 2;
                int y = (getHeight() - bounds.height) / 2 + fm.getAscent();
                
                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };

        btn.setFont(ThemeConstants.FONT_BOLD);
        btn.setPreferredSize(new Dimension(width, 40));
        btn.setBackground(bgNormal);
        btn.setForeground(fgNormal);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgHover);
                btn.setForeground(fgHover);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgNormal);
                btn.setForeground(fgNormal);
                btn.repaint();
            }
        });

        return btn;
    }
}