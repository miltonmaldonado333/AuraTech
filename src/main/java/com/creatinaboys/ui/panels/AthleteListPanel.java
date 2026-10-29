package com.creatinaboys.ui.panels;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.ui.components.*;
import com.creatinaboys.ui.components.VectorIcon.Type;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Lista moderna de deportistas con buscador, tabla con insignias de estado y
 * paginación.
 *
 * @author Milton Andrés Maldonado Salcedo
 */
public class AthleteListPanel extends JPanel {

    private static final Color SELECTION_BG = new Color(191, 219, 254);
    private static final Color GRID_COLOR = new Color(51, 65, 85);

    private static final String STATUS_ACTIVE = "Activo";
    private static final String STATUS_INACTIVE = "Inactivo";
    private static final int COL_STATUS = 6;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final int PAGE_SIZE = 5;
    private static final Locale CO = Locale.forLanguageTag("es-CO");

    private final AthleteService athleteService;

    private JTable table;
    private DefaultTableModel tableModel;
    private PlaceholderTextField txtSearch;
    private JLabel lblInfo;
    private JPanel paginationPanel;

    private List<Athlete> filtered = new ArrayList<>();
    private int currentPage = 1;

    public AthleteListPanel(AthleteService athleteService) {
        this.athleteService = athleteService;

        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(new EmptyBorder(30, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);

        loadAthleteData();
    }

    // ------------------------------------------------------------------ Encabezado
    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 8, 10, 0));

        JLabel title = new JLabel("Lista de Deportistas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Gestiona, busca y administra los deportistas afiliados al club.");
        subtitle.setFont(ThemeConstants.FONT_REGULAR);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        txtSearch = new PlaceholderTextField("Buscar por nombre, documento o disciplina...");
        InputBox searchBox = new InputBox(Type.SEARCH, txtSearch);
        searchBox.setPreferredSize(new Dimension(400, 52));

        ModernButton btnSearch = new ModernButton("Buscar", Type.SEARCH, ModernButton.Style.PRIMARY, 130);
        btnSearch.addActionListener(e -> applyFilter());
        txtSearch.addActionListener(e -> applyFilter());
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);
        searchRow.setAlignmentX(LEFT_ALIGNMENT);
        searchRow.setBorder(new EmptyBorder(0, -10, 0, 0));
        searchRow.add(searchBox);
        searchRow.add(btnSearch);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(18));
        header.add(searchRow);
        return header;
    }

    // ------------------------------------------------------------------ Tarjeta con tabla
    private JComponent buildCard() {
        CardPanel card = new CardPanel(new BorderLayout(0, 14), 14);

        RoundedPanel tableWrap = new RoundedPanel();
        tableWrap.add(buildTable(), BorderLayout.CENTER);

        card.add(tableWrap, BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(card, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildTable() {
        String[] columns = {"ID", "Nombre", "Edad", "Categoría", "Disciplina", "Vence", "Estado", "Tarifa (COP)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Extiende los separadores verticales hasta el final, incluso donde no hay filas
                g.setColor(getGridColor());
                TableColumnModel cm = getColumnModel();
                int x = 0;
                for (int i = 0; i < cm.getColumnCount() - 1; i++) {
                    x += cm.getColumn(i).getWidth();
                    g.drawLine(x - 1, 0, x - 1, getHeight());
                }
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);   // ahora sí, después de crear la tabla
        table.setFont(ThemeConstants.FONT_REGULAR);
        table.setRowHeight(48);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setGridColor(GRID_COLOR);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(ThemeConstants.TEXT_MAIN);
        table.setFocusable(false);
        table.setBackground(Color.WHITE);

        // Celdas de texto
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 4));
                setFont(sel ? ThemeConstants.FONT_BOLD : ThemeConstants.FONT_REGULAR);
                setForeground(ThemeConstants.TEXT_MAIN);
                setBackground(sel ? SELECTION_BG : Color.WHITE);
                return this;
            }
        });

        // Columna Estado: insignia
        table.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new BadgeRenderer());

        // Encabezado con separadores
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 48));
        table.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean focus, int row, int col) {
                JLabel l = new JLabel(String.valueOf(v));
                l.setOpaque(true);
                l.setFont(ThemeConstants.FONT_BOLD);
                l.setForeground(ThemeConstants.TEXT_MAIN);
                l.setBackground(new Color(241, 245, 249));
                boolean last = col == t.getColumnCount() - 1;
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, last ? 0 : 1, GRID_COLOR),
                        new EmptyBorder(0, 10, 0, 4)));
                return l;
            }
        });

        // Anchos: ID, Nombre, Edad, Categoría, Disciplina, Vence, Estado, Tarifa
        int[] widths = {115, 170, 55, 100, 100, 105, 105, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 4, 0, 4));

        paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        paginationPanel.setOpaque(false);

        lblInfo = new JLabel();
        lblInfo.setFont(ThemeConstants.FONT_REGULAR);
        lblInfo.setForeground(ThemeConstants.TEXT_MAIN);

        ModernButton btnToggle = new ModernButton("Activar / Desactivar", Type.CHECK,
                ModernButton.Style.SECONDARY, 210);
        btnToggle.addActionListener(e -> toggleSelectedStatus());

        ModernButton btnDelete = new ModernButton("Eliminar Seleccionado", Type.TRASH,
                ModernButton.Style.DANGER, 220);
        btnDelete.addActionListener(e -> deleteSelectedAthlete());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnToggle);
        actions.add(btnDelete);

        footer.add(paginationPanel, BorderLayout.WEST);
        footer.add(actions, BorderLayout.EAST);
        return footer;
    }

    // ------------------------------------------------------------------ Datos
    /**
     * Recarga desde el servicio (lo llama PrincipalFrames al entrar a la
     * pestaña).
     */
    public void loadAthleteData() {
        applyFilterKeepingPage();
    }

    private void applyFilter() {
        currentPage = 1;
        applyFilterKeepingPage();
    }

    private void applyFilterKeepingPage() {
        if (athleteService == null) {
            filtered = new ArrayList<>();
        } else {
            String q = txtSearch == null ? "" : SearchText.normalize(txtSearch.getText());
            filtered = athleteService.getAllAthletes().stream()
                    .filter(a -> q.isEmpty()
                    || SearchText.contains(a.getId(), q)
                    || SearchText.contains(a.getFullName(), q)
                    || SearchText.contains(SpanishLabels.of(a.getDiscipline()), q)
                    || SearchText.contains(a.getCategory() != null
                            ? SpanishLabels.of(a.getCategory().getName()) : "Sin categoría", q))
                    .collect(Collectors.toList());
        }
        refresh();
    }

    private void refresh() {
        int total = filtered.size();
        int pages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        currentPage = Math.min(Math.max(1, currentPage), pages);

        int from = (currentPage - 1) * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);

        tableModel.setRowCount(0);
        for (int i = from; i < to; i++) {
            Athlete a = filtered.get(i);
            tableModel.addRow(new Object[]{
                a.getId(),
                a.getFullName(),
                String.valueOf(a.getAge()),
                a.getCategory() != null ? SpanishLabels.of(a.getCategory().getName()) : "Sin categoría",
                SpanishLabels.of(a.getDiscipline()),
                a.getPaidUntil().format(DATE_FMT),
                a.isActive() ? STATUS_ACTIVE : STATUS_INACTIVE,
                "$" + String.format(CO, "%,.0f", a.getMonthlyFee())
            });
        }

        lblInfo.setText(total == 0
                ? "Sin registros"
                : "Mostrando " + (from + 1) + " a " + to + " de " + total + " registros");
        lblInfo.setBorder(new EmptyBorder(0, 6, 0, 0));

        rebuildPagination(pages);   // restaurado: dibuja los botones de página
    }

    private void rebuildPagination(int pages) {
        paginationPanel.removeAll();

        PageButton prev = new PageButton(null, Type.CHEV_LEFT, false);
        prev.setEnabled(currentPage > 1);
        prev.addActionListener(e -> {
            currentPage--;
            refresh();
        });
        paginationPanel.add(prev);

        int end = Math.min(pages, Math.max(1, currentPage - 1) + 2);
        int start = Math.max(1, end - 2);
        for (int p = start; p <= end; p++) {
            final int page = p;
            PageButton b = new PageButton(String.valueOf(p), null, p == currentPage);
            b.addActionListener(e -> {
                currentPage = page;
                refresh();
            });
            paginationPanel.add(b);
        }

        PageButton next = new PageButton(null, Type.CHEV_RIGHT, false);
        next.setEnabled(currentPage < pages);
        next.addActionListener(e -> {
            currentPage++;
            refresh();
        });
        paginationPanel.add(next);

        paginationPanel.add(lblInfo);
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    // ------------------------------------------------------------------ Acciones
    private void toggleSelectedStatus() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un deportista de la tabla para cambiar su estado.",
                    "Ninguna Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = String.valueOf(tableModel.getValueAt(row, 0));
        String name = String.valueOf(tableModel.getValueAt(row, 1));
        boolean currentlyActive = STATUS_ACTIVE.equals(tableModel.getValueAt(row, COL_STATUS));

        String message = currentlyActive
                ? "¿Deseas desactivar a " + name + "?"
                : "¿Deseas activar a " + name + "?\nSi su mensualidad está vencida, se renovará por 1 mes.";

        int confirm = JOptionPane.showConfirmDialog(this, message,
                "Cambiar Estado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            athleteService.toggleAthleteStatus(id);
            applyFilterKeepingPage();
        }
    }

    private void deleteSelectedAthlete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un deportista de la tabla para eliminar.",
                    "Ninguna Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = String.valueOf(tableModel.getValueAt(row, 0));
        String name = String.valueOf(tableModel.getValueAt(row, 1));

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar a " + name + " (ID: " + id + ")?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                athleteService.deleteAthlete(id);
                applyFilterKeepingPage();
                JOptionPane.showMessageDialog(this, "Deportista eliminado correctamente.",
                        "Eliminado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ------------------------------------------------------------------ Clases internas
    /**
     * Panel blanco con esquinas redondeadas que recorta a sus hijos (tabla).
     */
    private static class RoundedPanel extends JPanel {

        RoundedPanel() {
            super(new BorderLayout());
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.clip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
            super.paintChildren(g2);
            g2.dispose();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ThemeConstants.BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
        }
    }

    /**
     * Renderer de la columna Estado: píldora verde (Activo) o roja (Inactivo).
     */
    private static class BadgeRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                boolean focus, int row, int col) {
            return new Badge(String.valueOf(v), sel ? SELECTION_BG : Color.WHITE);
        }
    }

    private static class Badge extends JPanel {

        private final String text;

        Badge(String text, Color rowBg) {
            this.text = text;
            setOpaque(true);
            setBackground(rowBg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            boolean active = STATUS_ACTIVE.equalsIgnoreCase(text);
            Color bg = active ? new Color(187, 247, 208) : new Color(254, 202, 202);
            Color fg = active ? new Color(22, 101, 52) : new Color(153, 27, 27);

            Font f = new Font("Segoe UI", Font.PLAIN, 13);
            FontMetrics fm = g2.getFontMetrics(f);
            int w = fm.stringWidth(text) + 22, h = 26;
            int x = 10, y = (getHeight() - h) / 2;

            g2.setColor(bg);
            g2.fillRoundRect(x, y, w, h, h, h);
            g2.setColor(fg);
            g2.setFont(f);
            g2.drawString(text, x + 11, y + (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /**
     * Botón cuadrado de paginación (número o flecha).
     */
    private static class PageButton extends JButton {

        private final boolean active;
        private final VectorIcon icon;
        private boolean hover;

        PageButton(String text, Type type, boolean active) {
            super(text == null ? "" : text);
            this.active = active;
            this.icon = type == null ? null : new VectorIcon(type, 16, ThemeConstants.CANCEL_TEXT);
            setPreferredSize(new Dimension(36, 36));
            setFont(ThemeConstants.FONT_REGULAR);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(active ? new Color(226, 232, 240) : (hover && isEnabled() ? ThemeConstants.CANCEL_BG : Color.WHITE));
            g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
            g2.setColor(ThemeConstants.BORDER_COLOR);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

            if (icon != null) {
                icon.setColor(isEnabled() ? ThemeConstants.CANCEL_TEXT : ThemeConstants.PLACEHOLDER);
                icon.paintIcon(this, g2, (w - 16) / 2, (h - 16) / 2);
            } else {
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setColor(ThemeConstants.TEXT_MAIN);
                g2.setFont(getFont());
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                        (h - fm.getHeight()) / 2 + fm.getAscent());
            }
            g2.dispose();
        }
    }
}