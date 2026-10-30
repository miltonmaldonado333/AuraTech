package com.creatinaboys.ui.panels;

import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Payment;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.ICoachService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.ui.components.CardPanel;
import com.creatinaboys.ui.components.IconBadge;
import com.creatinaboys.ui.components.ModernButton;
import com.creatinaboys.ui.components.Money;
import com.creatinaboys.ui.components.SpanishLabels;
import com.creatinaboys.ui.components.VectorIcon;
import com.creatinaboys.ui.theme.ThemeConstants;
import com.creatinaboys.model.AthleteKey;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Pantalla de Inicio: saludo, accesos rápidos, indicadores del club, cobro del
 * mes, distribución por disciplina y categoría, pagos recientes y mensualidades
 * pendientes. Solo muestra datos; la lógica vive en los servicios.
 */
public class HomePanel extends JPanel {

    private static final Locale ES = Locale.forLanguageTag("es-CO");
    private static final Color GREEN = new Color(16, 185, 129);
    private static final Color AMBER = new Color(245, 158, 11);
    private static final Color SKY = new Color(14, 165, 233);
    private static final Color TRACK = new Color(226, 232, 240);
    private static final Color[] PALETTE = {
        ThemeConstants.PRIMARY_BLUE, ThemeConstants.PRIMARY_INDIGO, SKY, GREEN, AMBER
    };
    private static final Font FONT_BIG = new Font("Segoe UI", Font.BOLD, 26);

    private final AthleteService athleteService;
    private final ISaleService saleService;
    private final ICoachService coachService;
    private final Consumer<String> navigate;

    private final ScrollBody body = new ScrollBody();
    private String username = "";

    /**
     * Debajo de este ancho (px) el tablero usa el diseño compacto.
     */
    private static final int COMPACT_BELOW = 1100;
    private boolean compact = false;

    /**
     * @param navigate recibe el nombre de la opción del menú ("Registrar",
     * "Pagos"...) a la que ir.
     */
    public HomePanel(AthleteService athleteService, ISaleService saleService,
            ICoachService coachService, Consumer<String> navigate) {
        this.athleteService = athleteService;
        this.saleService = saleService;
        this.coachService = coachService;
        this.navigate = navigate;

        setLayout(new BorderLayout());
        setBackground(ThemeConstants.MAIN_BG);
        body.setBackground(ThemeConstants.MAIN_BG);
        body.setBorder(new EmptyBorder(24, 28, 24, 28));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ThemeConstants.MAIN_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                boolean nowCompact = getWidth() > 0 && getWidth() < COMPACT_BELOW;
                if (nowCompact != compact) {
                    compact = nowCompact;
                    refresh();
                }
            }
        });

        refresh();
    }

    public void setUsername(String username) {
        this.username = username == null ? "" : username.trim();
        refresh();
    }

    /**
     * Vuelve a leer los datos y reconstruye el tablero.
     */
    public void refresh() {
        body.removeAll();
        body.setLayout(new GridBagLayout());
        try {
            build();
        } catch (RuntimeException ex) {
            body.removeAll();
            JLabel err = new JLabel("No se pudieron cargar los datos del inicio: " + ex.getMessage());
            err.setFont(ThemeConstants.FONT_REGULAR);
            err.setForeground(ThemeConstants.DANGER_RED);
            body.add(err);
        }
        body.revalidate();
        body.repaint();
    }

    // ------------------------------------------------------------------ construcción
    private void build() {
        List<Athlete> athletes = athleteService.getAllAthletes();
        long active = athletes.stream().filter(Athlete::isActive).count();
        int coaches = coachService.getTotalCoachesCount();

        Map<AthleteKey, Double> paidMap = saleService.getPaidThisMonthByAthlete();
        double collected = paidMap.values().stream().mapToDouble(Double::doubleValue).sum();
        double expected = saleService.calculateEstimatedMonthlyRevenue();
        List<Athlete> pending = saleService.getAthletesWithPendingPayment();
        List<Payment> history = saleService.getPaymentHistory();
        Map<String, String> names = athletes.stream()
                .collect(Collectors.toMap(Athlete::getId, Athlete::getFullName, (a, b) -> a));

        int row = 0;
        addRow(buildHero(), row++);

        JPanel stats = compact ? grid(2, 2) : grid(1, 4);
        stats.add(statCard(VectorIcon.Type.USERS, ThemeConstants.PRIMARY_BLUE, "Deportistas",
                String.valueOf(athletes.size()), active + " activos"));
        stats.add(statCard(VectorIcon.Type.WHISTLE, ThemeConstants.PRIMARY_INDIGO, "Entrenadores",
                String.valueOf(coaches), "en el club"));
        stats.add(statCard(VectorIcon.Type.CARD, GREEN, "Recaudado este mes",
                Money.cop(collected), "de " + Money.cop(expected) + " esperados"));
        stats.add(statCard(VectorIcon.Type.FLAG, pending.isEmpty() ? GREEN : AMBER, "Pendientes de pago",
                String.valueOf(pending.size()), pending.isEmpty() ? "todo al día" : "mensualidades por cobrar"));
        addRow(stats, row++);

        JPanel mid = compact ? grid(1, 2) : grid(1, 3);
        mid.add(card("Cobro del mes", "Mensualidades del mes en curso", collectionBody(collected, expected)));
        if (compact) {
            JPanel both = new JPanel(new GridLayout(0, 1, 0, 14));
            both.setOpaque(false);
            both.add(section("POR DISCIPLINA", disciplineBars(athletes)));
            both.add(section("POR CATEGORÍA", categoryBars(athletes)));
            mid.add(card("Distribución del club", "Deportistas por disciplina y categoría", both));
        } else {
            mid.add(card("Por disciplina", "Deportistas inscritos", disciplineBars(athletes)));
            mid.add(card("Por categoría", "Deportistas según su edad", categoryBars(athletes)));
        }
        addRow(mid, row++);

        JPanel bottom = grid(1, 2);
        bottom.add(card("Pagos recientes", "Últimos movimientos registrados", recentPayments(history, names)));
        bottom.add(card("Pendientes de pago", "Mensualidades sin completar este mes",
                pendingList(pending, paidMap)));
        addRow(bottom, row++);

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = row;
        filler.weighty = 1;
        body.add(Box.createVerticalGlue(), filler);
    }

    private void addRow(JComponent c, int row) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = row;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 18, 0);
        body.add(c, g);
    }

    private static JPanel grid(int rows, int cols) {
        JPanel p = new JPanel(new GridLayout(rows, cols, 16, 16));
        p.setOpaque(false);
        return p;
    }

    // ------------------------------------------------------------------ banner
    private JComponent buildHero() {
        JPanel hero = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, 24, 24));
                g2.setPaint(new GradientPaint(0, 0, new Color(30, 58, 138), w, h, ThemeConstants.PRIMARY_INDIGO));
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fill(new Ellipse2D.Double(w - 260, -90, 300, 300));
                g2.setColor(new Color(255, 255, 255, 16));
                g2.fill(new Ellipse2D.Double(w - 120, 40, 240, 240));
                g2.setColor(new Color(96, 165, 250, 40));
                g2.fill(new Ellipse2D.Double(-60, h - 70, 200, 200));
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(26, 30, 26, 30));

        int hour = LocalTime.now().getHour();
        String greet = hour < 12 ? "Buenos días" : hour < 19 ? "Buenas tardes" : "Buenas noches";
        String who = username.isEmpty() ? "" : ", " + capitalize(username);
        JLabel title = new JLabel("¡" + greet + who + "!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", ES));
        JLabel sub = new JLabel(capitalize(date) + "  ·  Resumen del Club Deportivo Creatina-Boys");
        sub.setFont(ThemeConstants.FONT_REGULAR);
        sub.setForeground(new Color(191, 219, 254));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(6));
        text.add(sub);
        JPanel textWrap = new JPanel(new GridBagLayout());
        textWrap.setOpaque(false);
        GridBagConstraints tg = new GridBagConstraints();
        tg.anchor = GridBagConstraints.WEST;
        tg.weightx = 1;
        textWrap.add(text, tg);
        hero.add(textWrap, compact ? BorderLayout.NORTH : BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(compact ? FlowLayout.LEFT : FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        actions.add(quickAction("Registrar deportista", VectorIcon.Type.USER_PLUS, "Registrar", 200));
        actions.add(quickAction("Registrar pago", VectorIcon.Type.CARD, "Pagos", 160));
        actions.add(quickAction("Reportes", VectorIcon.Type.CHART, "Reportes", 140));
        JPanel actionsWrap = new JPanel(new GridBagLayout());
        actionsWrap.setOpaque(false);
        actionsWrap.add(actions);
        if (compact) {
            actionsWrap.setLayout(new BorderLayout());
            actionsWrap.add(actions, BorderLayout.WEST);
            actionsWrap.setBorder(new EmptyBorder(16, 0, 0, 0));
            hero.add(actionsWrap, BorderLayout.SOUTH);
        } else {
            hero.add(actionsWrap, BorderLayout.EAST);
        }
        return hero;
    }

    private ModernButton quickAction(String text, VectorIcon.Type icon, String target, int width) {
        ModernButton b = new ModernButton(text, icon, ModernButton.Style.SECONDARY, width);
        b.addActionListener(e -> navigate.accept(target));
        return b;
    }

    // ------------------------------------------------------------------ tarjetas
    private JComponent statCard(VectorIcon.Type icon, Color accent, String caption, String value, String hint) {
        CardPanel card = new CardPanel(new BorderLayout(14, 0), 14);
        card.add(new IconBadge(icon, accent, 54), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(label(caption, ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED));
        JLabel v = label(value, FONT_BIG, ThemeConstants.TEXT_MAIN);
        v.setBorder(new EmptyBorder(2, 0, 2, 0));
        text.add(v);
        text.add(label(hint, ThemeConstants.FONT_SMALL, accent));
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.weightx = 1;
        wrap.add(text, g);
        card.add(wrap, BorderLayout.CENTER);
        return card;
    }

    private JComponent card(String title, String subtitle, JComponent content) {
        CardPanel card = new CardPanel(new BorderLayout(0, 14), 16);
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(label(title, ThemeConstants.FONT_SUBTITLE, ThemeConstants.TEXT_MAIN));
        head.add(Box.createVerticalStrut(2));
        head.add(label(subtitle, ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED));
        card.add(head, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JComponent collectionBody(double collected, double expected) {
        double fraction = expected <= 0 ? 0 : Math.min(1, collected / expected);
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        Donut donut = new Donut(fraction);
        JPanel donutWrap = new JPanel(new GridBagLayout());
        donutWrap.setOpaque(false);
        donutWrap.add(donut);
        p.add(donutWrap, BorderLayout.CENTER);

        JPanel legend = new JPanel(new GridLayout(0, 1, 0, 4));
        legend.setOpaque(false);
        legend.add(legendRow(GREEN, "Recaudado", Money.cop(collected)));
        legend.add(legendRow(TRACK, "Por recaudar", Money.cop(Math.max(0, expected - collected))));
        p.add(legend, BorderLayout.SOUTH);
        return p;
    }

    private JComponent legendRow(Color dot, String name, String value) {
        JPanel r = new JPanel(new BorderLayout(8, 0));
        r.setOpaque(false);
        r.add(new JComponent() {
            {
                setPreferredSize(new Dimension(12, 12));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dot);
                g2.fillOval(1, (getHeight() - 10) / 2, 10, 10);
                g2.dispose();
            }
        }, BorderLayout.WEST);
        r.add(label(name, ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED), BorderLayout.CENTER);
        JLabel v = label(value, ThemeConstants.FONT_BOLD, ThemeConstants.TEXT_MAIN);
        r.add(v, BorderLayout.EAST);
        return r;
    }

    private JComponent disciplineBars(List<Athlete> athletes) {
        Map<String, Long> counts = new TreeMap<>(athletes.stream()
                .filter(a -> a.getDiscipline() != null)
                .collect(Collectors.groupingBy(a -> SpanishLabels.of(a.getDiscipline()), Collectors.counting())));
        return bars(counts, athletes.size(), "Aún no hay deportistas registrados.");
    }

    private JComponent categoryBars(List<Athlete> athletes) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (CategoryEnum c : CategoryEnum.values()) {
            counts.put(SpanishLabels.of(c.getName()), athletes.stream().filter(a -> c.equals(a.getCategory())).count());
        }
        return bars(counts, athletes.size(), "Aún no hay deportistas registrados.");
    }

    private JComponent bars(Map<String, Long> counts, int total, String emptyText) {
        if (total == 0 || counts.isEmpty()) {
            return emptyState(emptyText, ThemeConstants.TEXT_MUTED);
        }
        JPanel list = new JPanel(new GridLayout(0, 1, 0, 10));
        list.setOpaque(false);
        int i = 0;
        for (Map.Entry<String, Long> e : counts.entrySet()) {
            double pct = e.getValue() * 100.0 / total;
            list.add(new Bar(e.getKey(), e.getValue() + "  ·  " + Math.round(pct) + "%",
                    pct / 100.0, PALETTE[i++ % PALETTE.length]));
        }
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(list, BorderLayout.NORTH);
        return wrap;
    }

    private JComponent recentPayments(List<Payment> history, Map<String, String> names) {
        if (history.isEmpty()) {
            return emptyState("Aún no hay pagos registrados.", ThemeConstants.TEXT_MUTED);
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d 'de' MMM", ES);
        JPanel list = new JPanel(new GridLayout(0, 1, 0, 8));
        list.setOpaque(false);
        int i = 0;
        for (Payment p : history.stream().limit(5).collect(Collectors.toList())) {
            String name = names.getOrDefault(p.getAthleteId(), "Documento " + p.getAthleteId());
            String when = p.getPaymentDate().format(fmt);
            String detail = p.getDiscipline().isEmpty()
                    ? when
                    : when + "  ·  " + SpanishLabels.of(p.getDiscipline());
            list.add(personRow(name, detail, "+ " + Money.cop(p.getAmount()),
                    GREEN, PALETTE[i++ % PALETTE.length]));
        }
        return top(list);
    }

    private JComponent pendingList(List<Athlete> pending, Map<AthleteKey, Double> paidMap) {
        if (pending.isEmpty()) {
            return emptyState("¡Todo al día! No hay mensualidades pendientes.", GREEN);
        }
        JPanel list = new JPanel(new GridLayout(0, 1, 0, 8));
        list.setOpaque(false);
        int shown = 0;
        for (Athlete a : pending) {
            if (shown++ == 5) {
                break;
            }
            double owed = Math.max(0, a.getMonthlyFee() - paidMap.getOrDefault(a.getKey(), 0.0));
            list.add(personRow(a.getFullName(), SpanishLabels.of(a.getDiscipline()) + "  ·  "
                    + SpanishLabels.of(a.getCategory() == null ? null : a.getCategory().getName()),
                    Money.cop(owed), ThemeConstants.DANGER_RED, AMBER));
        }
        if (pending.size() > 5) {
            JLabel more = label("y " + (pending.size() - 5) + " más en Pagos", ThemeConstants.FONT_SMALL,
                    ThemeConstants.TEXT_MUTED);
            JPanel box = new JPanel(new BorderLayout(0, 8));
            box.setOpaque(false);
            box.add(list, BorderLayout.NORTH);
            box.add(more, BorderLayout.CENTER);
            return top(box);
        }
        return top(list);
    }

    private JComponent personRow(String name, String detail, String amount, Color amountColor, Color accent) {
        JPanel r = new JPanel(new BorderLayout(12, 0));
        r.setOpaque(false);
        r.add(new Avatar(name, accent), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(label(name, ThemeConstants.FONT_BOLD, ThemeConstants.TEXT_MAIN));
        text.add(label(detail, ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_MUTED));
        JPanel textWrap = new JPanel(new GridBagLayout());
        textWrap.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.weightx = 1;
        textWrap.add(text, g);
        r.add(textWrap, BorderLayout.CENTER);
        r.add(label(amount, ThemeConstants.FONT_BOLD, amountColor), BorderLayout.EAST);
        return r;
    }

    private JComponent section(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.add(label(title, ThemeConstants.FONT_SECTION, ThemeConstants.PLACEHOLDER), BorderLayout.NORTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JComponent emptyState(String text, Color color) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(200, 90));
        p.add(label(text, ThemeConstants.FONT_REGULAR, color));
        return p;
    }

    private static JComponent top(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(c, BorderLayout.NORTH);
        return p;
    }

    private static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase(ES) + s.substring(1);
    }

    // ------------------------------------------------------------------ componentes dibujados
    /**
     * Contenedor que se ajusta al ancho de la ventana (sin barra horizontal).
     */
    private static class ScrollBody extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return 120;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /**
     * Barra de progreso con etiqueta y valor.
     */
    private static class Bar extends JComponent {

        private final String name, value;
        private final double fraction;
        private final Color color;

        Bar(String name, String value, double fraction, Color color) {
            this.name = name;
            this.value = value;
            this.fraction = fraction;
            this.color = color;
            setPreferredSize(new Dimension(160, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            g2.setFont(ThemeConstants.FONT_BOLD);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(ThemeConstants.TEXT_MAIN);
            g2.drawString(name, 0, fm.getAscent());
            g2.setFont(ThemeConstants.FONT_SMALL);
            g2.setColor(ThemeConstants.TEXT_MUTED);
            g2.drawString(value, w - g2.getFontMetrics().stringWidth(value), fm.getAscent());

            int y = fm.getHeight() + 6, h = 10;
            g2.setColor(TRACK);
            g2.fill(new RoundRectangle2D.Double(0, y, w, h, h, h));
            double fill = Math.max(h, w * fraction);
            g2.setPaint(new GradientPaint(0, 0, color, (float) fill, 0, color.brighter()));
            g2.fill(new RoundRectangle2D.Double(0, y, fill, h, h, h));
            g2.dispose();
        }
    }

    /**
     * Anillo de avance con el porcentaje en el centro.
     */
    private static class Donut extends JComponent {

        private final double fraction;

        Donut(double fraction) {
            this.fraction = fraction;
            setPreferredSize(new Dimension(170, 170));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 24;
            double x = (getWidth() - size) / 2.0, y = (getHeight() - size) / 2.0;
            g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(TRACK);
            g2.draw(new Ellipse2D.Double(x, y, size, size));
            if (fraction > 0) {
                g2.setPaint(new GradientPaint((float) x, (float) y, GREEN, (float) (x + size), (float) (y + size), SKY));
                g2.draw(new Arc2D.Double(x, y, size, size, 90, -360 * fraction, Arc2D.OPEN));
            }
            String pct = Math.round(fraction * 100) + "%";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(ThemeConstants.TEXT_MAIN);
            g2.drawString(pct, (getWidth() - fm.stringWidth(pct)) / 2, getHeight() / 2 + fm.getAscent() / 3);
            g2.setFont(ThemeConstants.FONT_SMALL);
            fm = g2.getFontMetrics();
            g2.setColor(ThemeConstants.TEXT_MUTED);
            String cap = "recaudado";
            g2.drawString(cap, (getWidth() - fm.stringWidth(cap)) / 2, getHeight() / 2 + 24);
            g2.dispose();
        }
    }

    /**
     * Círculo de color con las iniciales de la persona.
     */
    private static class Avatar extends JComponent {

        private final String initials;
        private final Color color;

        Avatar(String name, Color color) {
            this.color = color;
            String[] parts = name.trim().split("\\s+");
            String s = parts[0].substring(0, 1);
            if (parts.length > 1) {
                s += parts[1].substring(0, 1);
            }
            this.initials = s.toUpperCase(ES);
            setPreferredSize(new Dimension(38, 38));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 32));
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(color);
            g2.setFont(ThemeConstants.FONT_BOLD);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(initials, (getWidth() - fm.stringWidth(initials)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
