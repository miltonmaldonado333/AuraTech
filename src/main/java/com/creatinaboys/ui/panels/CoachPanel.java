package com.creatinaboys.ui.panels;

import com.creatinaboys.model.Coach;
import com.creatinaboys.service.ICoachService;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lista de entrenadores con buscador, tabla (disciplina, teléfono, estado y
 * acciones de editar/eliminar) y paginación.
 */
public class CoachPanel extends JPanel {

    private static final Color SELECTION_BG = new Color(191, 219, 254);
    private static final Color GRID_COLOR = new Color(51, 65, 85);

    private static final String STATUS_ACTIVE = "Activo";
    private static final String STATUS_INACTIVE = "Inactivo";

    private static final int COL_DISCIPLINE = 2;
    private static final int COL_PHONE = 3;
    private static final int COL_STATUS = 4;
    private static final int COL_ACTIONS = 5;

    private static final int PAGE_SIZE = 5;
    private static final int ICON_SIZE = 20;
    private static final int EDIT_X = 14;     // posición del lápiz dentro de la celda Acciones
    private static final int TRASH_X = 50;    // posición de la papelera

    private final ICoachService coachService;

    private JTable table;
    private DefaultTableModel tableModel;
    private PlaceholderTextField txtSearch;
    private JLabel lblInfo;
    private JPanel paginationPanel;

    private List<Coach> filtered = new ArrayList<>();
    private int currentPage = 1;
    private int pageStart = 0;   // índice (en filtered) de la primera fila visible

    public CoachPanel(ICoachService coachService) {
        this.coachService = coachService;

        setLayout(new BorderLayout());
        setBackground(ThemeConstants.MAIN_BG);
        setBorder(new EmptyBorder(30, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);

        loadCoaches();
    }

    // ------------------------------------------------------------------ Encabezado
    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 8, 14, 0));

        JLabel title = new JLabel("Lista de Entrenadores");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Gestiona, busca y administra el cuerpo técnico del club.");
        subtitle.setFont(ThemeConstants.FONT_REGULAR);
        subtitle.setForeground(ThemeConstants.TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        txtSearch = new PlaceholderTextField("Buscar por nombre o disciplina...");
        InputBox searchBox = new InputBox(Type.SEARCH, txtSearch);
        searchBox.setPreferredSize(new Dimension(360, 52));

        ModernButton btnSearch = new ModernButton("Buscar", Type.SEARCH, ModernButton.Style.PRIMARY, 130);
        btnSearch.addActionListener(e -> applyFilter());
        txtSearch.addActionListener(e -> applyFilter());
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { }
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(searchBox);
        left.add(btnSearch);

        ModernButton btnNew = new ModernButton("Nuevo Entrenador", Type.USER_PLUS,
                ModernButton.Style.PRIMARY, 210);
        btnNew.addActionListener(e -> newCoach());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(btnNew);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(0, -10, 0, 0));
        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(18));
        header.add(row);
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
        String[] columns = {"ID", "Nombre", "Disciplina", "Teléfono", "Estado", "Acciones"};
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
                g.setColor(getGridColor());
                TableColumnModel cm = getColumnModel();
                int x = 0;
                for (int i = 0; i < cm.getColumnCount() - 1; i++) {
                    x += cm.getColumn(i).getWidth();
                    g.drawLine(x - 1, 0, x - 1, getHeight());
                }
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFont(ThemeConstants.FONT_REGULAR);
        table.setRowHeight(48);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setGridColor(GRID_COLOR);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(ThemeConstants.TEXT_MAIN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFocusable(false);
        table.setBackground(Color.WHITE);

        // Celdas de texto
        table.setDefaultRenderer(Object.class, new IconTextRenderer(v -> null));

        // Disciplina y teléfono: ícono + texto
        table.getColumnModel().getColumn(COL_DISCIPLINE).setCellRenderer(new IconTextRenderer(v ->
                new VectorIcon(Type.BALL, 18,
                        "Baloncesto".equals(v) ? new Color(234, 88, 12) : new Color(22, 101, 52))));
        table.getColumnModel().getColumn(COL_PHONE).setCellRenderer(new IconTextRenderer(v ->
                new VectorIcon(Type.PHONE, 16, ThemeConstants.TEXT_MUTED)));

        // Estado: insignia; Acciones: lápiz + papelera
        table.getColumnModel().getColumn(COL_STATUS).setCellRenderer(
                (t, v, sel, focus, row, col) -> new StatusBadge(String.valueOf(v), sel ? SELECTION_BG : Color.WHITE));
        table.getColumnModel().getColumn(COL_ACTIONS).setCellRenderer(
                (t, v, sel, focus, row, col) -> new ActionsCell(sel ? SELECTION_BG : Color.WHITE));

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

        int[] widths = {110, 210, 140, 150, 120, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e);
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                table.setCursor(Cursor.getPredefinedCursor(isClickable(e) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 4, 0, 8));

        paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        paginationPanel.setOpaque(false);

        lblInfo = new JLabel();
        lblInfo.setFont(ThemeConstants.FONT_REGULAR);
        lblInfo.setForeground(ThemeConstants.TEXT_MAIN);

        footer.add(paginationPanel, BorderLayout.WEST);
        footer.add(lblInfo, BorderLayout.EAST);
        return footer;
    }

    // ------------------------------------------------------------------ Datos
    /** Recarga desde el servicio (la llama PrincipalFrames al entrar a la pestaña). */
    public void loadCoaches() {
        applyFilterKeepingPage();
    }

    private void applyFilter() {
        currentPage = 1;
        applyFilterKeepingPage();
    }

    private void applyFilterKeepingPage() {
        if (tableModel == null) {
            return;   // la UI aún se está construyendo
        }
        try {
            String q = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
            filtered = coachService.getAllCoaches().stream()
                    .filter(c -> q.isEmpty()
                            || c.getId().toLowerCase(Locale.ROOT).contains(q)
                            || c.getFullName().toLowerCase(Locale.ROOT).contains(q)
                            || c.getPhone().toLowerCase(Locale.ROOT).contains(q)
                            || SpanishLabels.of(c.getDiscipline()).toLowerCase(Locale.ROOT).contains(q))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            filtered = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los entrenadores: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        refresh();
    }

    private void refresh() {
        int total = filtered.size();
        int pages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        currentPage = Math.min(Math.max(1, currentPage), pages);

        pageStart = (currentPage - 1) * PAGE_SIZE;
        int to = Math.min(pageStart + PAGE_SIZE, total);

        tableModel.setRowCount(0);
        for (int i = pageStart; i < to; i++) {
            Coach c = filtered.get(i);
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getFullName(),
                    SpanishLabels.of(c.getDiscipline()),
                    c.getPhone().isEmpty() ? "—" : c.getPhone(),
                    c.isActive() ? STATUS_ACTIVE : STATUS_INACTIVE,
                    ""
            });
        }

        lblInfo.setText(total == 0
                ? "Sin registros"
                : "Mostrando " + (pageStart + 1) + " a " + to + " de " + total + " registros");

        rebuildPagination(pages);
    }

    private void rebuildPagination(int pages) {
        paginationPanel.removeAll();

        PageButton prev = new PageButton(null, Type.CHEV_LEFT, false);
        prev.setEnabled(currentPage > 1);
        prev.addActionListener(e -> { currentPage--; refresh(); });
        paginationPanel.add(prev);

        int end = Math.min(pages, Math.max(1, currentPage - 1) + 2);
        int start = Math.max(1, end - 2);
        for (int p = start; p <= end; p++) {
            final int page = p;
            PageButton b = new PageButton(String.valueOf(p), null, p == currentPage);
            b.addActionListener(e -> { currentPage = page; refresh(); });
            paginationPanel.add(b);
        }

        PageButton next = new PageButton(null, Type.CHEV_RIGHT, false);
        next.setEnabled(currentPage < pages);
        next.addActionListener(e -> { currentPage++; refresh(); });
        paginationPanel.add(next);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    // ------------------------------------------------------------------ Clics en la tabla
    private Coach coachAt(int row) {
        int idx = pageStart + row;
        return (row >= 0 && idx < filtered.size()) ? filtered.get(idx) : null;
    }

    private boolean hit(int relX, int iconX) {
        return relX >= iconX - 6 && relX <= iconX + ICON_SIZE + 6;
    }

    private boolean isClickable(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (row < 0 || col < 0 || coachAt(row) == null) {
            return false;
        }
        if (col == COL_STATUS) {
            return true;
        }
        if (col == COL_ACTIONS) {
            int relX = e.getX() - table.getCellRect(row, col, false).x;
            return hit(relX, EDIT_X) || hit(relX, TRASH_X);
        }
        return false;
    }

    private void handleClick(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        Coach coach = coachAt(row);
        if (coach == null || col < 0) {
            return;
        }

        if (col == COL_ACTIONS) {
            int relX = e.getX() - table.getCellRect(row, col, false).x;
            if (hit(relX, EDIT_X)) {
                editCoach(coach);
            } else if (hit(relX, TRASH_X)) {
                deleteCoach(coach);
            }
        } else if (col == COL_STATUS) {
            toggleStatus(coach);
        } else if (e.getClickCount() == 2) {
            editCoach(coach);
        }
    }

    // ------------------------------------------------------------------ Acciones
    private void newCoach() {
        CoachFormDialog dialog = new CoachFormDialog(SwingUtilities.getWindowAncestor(this), null, coach -> {
            if (!coachService.registerCoach(coach)) {
                throw new IllegalStateException(
                        "Ya existe un entrenador registrado con el documento " + coach.getId() + ".");
            }
        });
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            applyFilterKeepingPage();
            JOptionPane.showMessageDialog(this, "Entrenador registrado correctamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editCoach(Coach coach) {
        CoachFormDialog dialog = new CoachFormDialog(SwingUtilities.getWindowAncestor(this), coach,
                updated -> coachService.updateCoach(updated));
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            applyFilterKeepingPage();
        }
    }

    private void toggleStatus(Coach coach) {
        String message = coach.isActive()
                ? "¿Deseas desactivar a " + coach.getFullName() + "?"
                : "¿Deseas activar a " + coach.getFullName() + "?";
        int confirm = JOptionPane.showConfirmDialog(this, message,
                "Cambiar Estado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            coachService.toggleCoachStatus(coach.getId());
            applyFilterKeepingPage();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cambiar el estado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCoach(Coach coach) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar a " + coach.getFullName()
                        + " (ID: " + coach.getId() + ")?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            coachService.deleteCoach(coach.getId());
            applyFilterKeepingPage();
            JOptionPane.showMessageDialog(this, "Entrenador eliminado correctamente.",
                    "Eliminado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------ Clases internas
    /** Celda de texto con un ícono opcional a la izquierda. */
    private static class IconTextRenderer extends DefaultTableCellRenderer {

        private final Function<String, Icon> iconFor;

        IconTextRenderer(Function<String, Icon> iconFor) {
            this.iconFor = iconFor;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, false, row, col);
            setIcon(iconFor.apply(String.valueOf(v)));
            setIconTextGap(8);
            setBorder(new EmptyBorder(0, 10, 0, 4));
            setFont(sel ? ThemeConstants.FONT_BOLD : ThemeConstants.FONT_REGULAR);
            setForeground(ThemeConstants.TEXT_MAIN);
            setBackground(sel ? SELECTION_BG : Color.WHITE);
            return this;
        }
    }

    /** Celda Acciones: lápiz (editar) y papelera (eliminar). Los clics se resuelven en la tabla. */
    private static class ActionsCell extends JPanel {

        private final VectorIcon edit = new VectorIcon(Type.EDIT, ICON_SIZE, ThemeConstants.PRIMARY_BLUE);
        private final VectorIcon trash = new VectorIcon(Type.TRASH, ICON_SIZE, ThemeConstants.DANGER_RED);

        ActionsCell(Color rowBg) {
            setOpaque(true);
            setBackground(rowBg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int y = (getHeight() - ICON_SIZE) / 2;
            edit.paintIcon(this, g, EDIT_X, y);
            trash.paintIcon(this, g, TRASH_X, y);
        }
    }

    /** Insignia de estado: verde con check (Activo) o roja con X (Inactivo). */
    private static class StatusBadge extends JPanel {

        private final String text;

        StatusBadge(String text, Color rowBg) {
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
            int iconSize = 12;
            int w = 10 + iconSize + 6 + fm.stringWidth(text) + 11, h = 26;
            int x = 10, y = (getHeight() - h) / 2;

            g2.setColor(bg);
            g2.fillRoundRect(x, y, w, h, h, h);

            new VectorIcon(active ? Type.CHECK : Type.CLOSE, iconSize, fg)
                    .paintIcon(this, g2, x + 9, y + (h - iconSize) / 2);

            g2.setColor(fg);
            g2.setFont(f);
            g2.drawString(text, x + 9 + iconSize + 5, y + (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /** Panel blanco con esquinas redondeadas que recorta a sus hijos (la tabla). */
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

    /** Botón cuadrado de paginación (número o flecha). */
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
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(active ? new Color(226, 232, 240)
                    : (hover && isEnabled() ? ThemeConstants.CANCEL_BG : Color.WHITE));
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