package com.creatinaboys.ui.panels;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.Footballer;
import com.creatinaboys.model.Payment;
import com.creatinaboys.model.PaymentStatusEnum;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.ISaleService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lista moderna de deportistas con buscador, tabla con insignias de estado y
 * paginación. Cada fila es un registro (documento + disciplina), identificado
 * por su AthleteKey.
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
    /** Opcional: si es null, el panel de detalle no muestra la información de pagos. */
    private final ISaleService saleService;

    private JTable table;
    private DefaultTableModel tableModel;
    private PlaceholderTextField txtSearch;
    private JLabel lblInfo;
    private JPanel paginationPanel;

    private List<Athlete> filtered = new ArrayList<>();
    /** Deportistas de la página visible, en el mismo orden que las filas de la tabla. */
    private List<Athlete> pageRows = new ArrayList<>();
    private int currentPage = 1;

    // Panel de detalle del deportista seleccionado
    private JComponent detailWrap;
    private final JLabel dName = detailTitle();
    private final JLabel dStatus = new JLabel();
    private final JPanel dBody = new JPanel();
    private AthleteKey selectedKey;
    private boolean refreshing;

    public AthleteListPanel(AthleteService athleteService) {
        this(athleteService, null);
    }

    public AthleteListPanel(AthleteService athleteService, ISaleService saleService) {
        this.athleteService = athleteService;
        this.saleService = saleService;

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

        detailWrap = buildDetailPanel();
        detailWrap.setVisible(false);
        container.add(detailWrap, BorderLayout.EAST);
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

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !refreshing) {
                onSelectionChanged();
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

        pageRows = new ArrayList<>(filtered.subList(from, to));

        refreshing = true;
        tableModel.setRowCount(0);
        for (Athlete a : pageRows) {
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

        refreshing = false;
        restoreSelection();

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

    // ------------------------------------------------------------------ Detalle del deportista
    private JComponent buildDetailPanel() {
        CardPanel card = new CardPanel(new BorderLayout(0, 10), 16);
        card.setPreferredSize(new Dimension(300, 0));

        JButton close = new JButton("\u2715");
        close.setFont(ThemeConstants.FONT_BOLD);
        close.setForeground(ThemeConstants.TEXT_MUTED);
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.setToolTipText("Cerrar");
        close.addActionListener(e -> table.clearSelection());

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.add(new IconBadge(Type.USER, ThemeConstants.PRIMARY_BLUE, 48), BorderLayout.WEST);

        JPanel names = new JPanel();
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.setOpaque(false);
        dName.setAlignmentX(LEFT_ALIGNMENT);
        dStatus.setAlignmentX(LEFT_ALIGNMENT);
        dStatus.setFont(ThemeConstants.FONT_BOLD);
        names.add(Box.createVerticalGlue());
        names.add(dName);
        names.add(dStatus);
        names.add(Box.createVerticalGlue());
        top.add(names, BorderLayout.CENTER);
        top.add(close, BorderLayout.EAST);

        dBody.setLayout(new BoxLayout(dBody, BoxLayout.Y_AXIS));
        dBody.setOpaque(false);
        JScrollPane scroll = new JScrollPane(dBody);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(top, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 16, 0, 0));
        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }

    /** Deportista de la fila indicada, o null si la fila no es válida. */
    private Athlete athleteAtRow(int row) {
        return (row >= 0 && row < pageRows.size()) ? pageRows.get(row) : null;
    }

    private void onSelectionChanged() {
        Athlete a = athleteAtRow(table.getSelectedRow());
        if (a == null) {
            selectedKey = null;
            setDetailVisible(false);
        } else {
            selectedKey = a.getKey();
            showDetail(selectedKey);
        }
        revalidate();
        repaint();
    }

    /** Tras recargar la tabla, vuelve a marcar al deportista seleccionado si sigue en la página. */
    private void restoreSelection() {
        if (selectedKey == null) {
            return;
        }
        for (int i = 0; i < pageRows.size(); i++) {
            if (selectedKey.equals(pageRows.get(i).getKey())) {
                table.setRowSelectionInterval(i, i);
                showDetail(selectedKey);
                return;
            }
        }
        selectedKey = null;
        setDetailVisible(false);
    }

    private void showDetail(AthleteKey key) {
        Athlete a = filtered.stream().filter(x -> x.getKey().equals(key)).findFirst().orElse(null);
        if (a == null) {
            setDetailVisible(false);
            return;
        }
        dName.setText(a.getFullName());
        dStatus.setText(a.isActive() ? "\u25CF Activo" : "\u25CF Inactivo");
        dStatus.setForeground(a.isActive() ? new Color(22, 163, 74) : ThemeConstants.DANGER_RED);

        dBody.removeAll();
        addSection("DATOS PERSONALES");
        addRow("Documento", a.getId());
        addRow("Edad", a.getAge() + " años");
        addRow("Teléfono", a.getPhone());

        addSection("INFORMACIÓN DEPORTIVA");
        addRow("Disciplina", SpanishLabels.of(a.getDiscipline()));
        addRow("Categoría", a.getCategory() != null ? SpanishLabels.of(a.getCategory().getName()) : "Sin categoría");
        addRow("Dorsal", String.valueOf(a.getJerseyNumber()));
        if (a instanceof Footballer f) {
            addRow("Posición", f.getPlayingPosition());
        } else if (a instanceof Basketballer b) {
            addRow("Estatura", String.format(CO, "%.2f m", b.getHeight()));
        }

        addSection("MEMBRESÍA");
        addRow("Estado", a.isActive() ? "Activo" : "Inactivo");
        addRow("Cambio de estado", a.isManualOverride() ? "Manual" : "Automático");
        addRow("Vence", a.getPaidUntil().format(DATE_FMT));
        long days = ChronoUnit.DAYS.between(LocalDate.now(), a.getPaidUntil());
        addRow(days >= 0 ? "Días restantes" : "Vencida hace", Math.abs(days) + (Math.abs(days) == 1 ? " día" : " días"));

        addSection("PAGOS");
        addRow("Mensualidad", Money.cop(a.getMonthlyFee()));
        if (saleService != null) {
            Map<AthleteKey, Double> paidMap = saleService.getPaidThisMonthByAthlete();
            double paid = paidMap.getOrDefault(a.getKey(), 0.0);
            PaymentStatusEnum st = PaymentStatusEnum.of(a.getMonthlyFee(), paid);
            addRow("Pagado este mes", Money.cop(paid));
            addRow("Saldo pendiente", Money.cop(Math.max(0, a.getMonthlyFee() - paid)));
            addRow("Estado del mes", st.getLabel());

            // Solo los pagos de este registro (documento + disciplina)
            List<Payment> mine = saleService.getPaymentHistory().stream()
                    .filter(p -> p.belongsTo(a.getKey()))
                    .limit(5).collect(Collectors.toList());
            addSection("ÚLTIMOS PAGOS");
            if (mine.isEmpty()) {
                addRow("Sin pagos registrados", "");
            }
            for (Payment p : mine) {
                addRow(p.getPaymentDate().format(DATE_FMT) + "  (" + p.getPeriod() + ")", Money.cop(p.getAmount()));
            }
        }
        dBody.add(Box.createVerticalGlue());
        dBody.revalidate();
        dBody.repaint();
        setDetailVisible(true);
    }

    /** Con el detalle abierto la tabla queda más angosta: se oculta el texto de conteo para que quepan los botones. */
    private void setDetailVisible(boolean visible) {
        detailWrap.setVisible(visible);
        lblInfo.setVisible(!visible);
    }

    private void addSection(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeConstants.FONT_SECTION);
        l.setForeground(ThemeConstants.PLACEHOLDER);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(dBody.getComponentCount() == 0 ? 6 : 14, 0, 6, 0));
        dBody.add(l);
    }

    private void addRow(String key, String value) {
        JPanel r = new JPanel(new BorderLayout(8, 0));
        r.setOpaque(false);
        r.setAlignmentX(LEFT_ALIGNMENT);
        JLabel k = new JLabel(key);
        k.setFont(ThemeConstants.FONT_REGULAR);
        k.setForeground(ThemeConstants.TEXT_MUTED);
        JLabel v = new JLabel(value == null || value.isBlank() ? "—" : value);
        v.setFont(ThemeConstants.FONT_BOLD);
        v.setForeground(ThemeConstants.TEXT_MAIN);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        r.add(k, BorderLayout.WEST);
        r.add(v, BorderLayout.CENTER);
        r.setBorder(new EmptyBorder(3, 0, 3, 0));
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, r.getPreferredSize().height));
        dBody.add(r);
    }

    private static JLabel detailTitle() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.BOLD, 17));
        l.setForeground(ThemeConstants.TEXT_MAIN);
        return l;
    }

    // ------------------------------------------------------------------ Acciones
    private void toggleSelectedStatus() {
        Athlete a = athleteAtRow(table.getSelectedRow());
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un deportista de la tabla para cambiar su estado.",
                    "Ninguna Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String label = a.getFullName() + " (" + SpanishLabels.of(a.getDiscipline()) + ")";
        String message = a.isActive()
                ? "¿Deseas desactivar a " + label + "?"
                : "¿Deseas activar a " + label + "?\nSi su mensualidad está vencida, se renovará por 1 mes.";

        int confirm = JOptionPane.showConfirmDialog(this, message,
                "Cambiar Estado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            athleteService.toggleAthleteStatus(a.getKey());
            applyFilterKeepingPage();
        }
    }

    private void deleteSelectedAthlete() {
        Athlete a = athleteAtRow(table.getSelectedRow());
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un deportista de la tabla para eliminar.",
                    "Ninguna Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar a " + a.getFullName()
                        + " (ID: " + a.getId() + ") de " + SpanishLabels.of(a.getDiscipline()) + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                athleteService.deleteAthlete(a.getKey());
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