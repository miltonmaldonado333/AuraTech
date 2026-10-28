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
 * Vista de la lista de deportistas con búsqueda y tabla de registros (Mockup 4).
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
        txtSearch.setPreferredSize(new Dimension(300, 38));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnSearch = new JButton("Buscar");
        btnSearch.setFont(ThemeConstants.FONT_BOLD);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(ThemeConstants.PRIMARY_BLUE);
        btnSearch.setPreferredSize(new Dimension(100, 38));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        // Centrar y estilizar columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
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
}