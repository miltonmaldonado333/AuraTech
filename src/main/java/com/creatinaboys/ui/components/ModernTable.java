package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

/**
 * Tabla con el mismo estilo de la lista de deportistas: encabezado gris, filas altas,
 * separadores y contenedor redondeado. Reutilizable en cualquier pantalla.
 */
public final class ModernTable {

    private static final Color SELECTION_BG = new Color(191, 219, 254);
    private static final Color GRID_COLOR = new Color(51, 65, 85);

    private ModernTable() { }

    /** Crea una tabla estilizada y no editable. Las columnas en {@code rightAligned} van alineadas a la derecha. */
    public static JTable create(String[] columns, int[] widths, int... rightAligned) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(model) {
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
        table.setFocusable(false);
        table.setBackground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                boolean right = false;
                for (int c : rightAligned) {
                    right |= c == col;
                }
                setHorizontalAlignment(right ? SwingConstants.RIGHT : SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, right ? 4 : 10, 0, right ? 10 : 4));
                setFont(sel ? ThemeConstants.FONT_BOLD : ThemeConstants.FONT_REGULAR);
                setForeground(ThemeConstants.TEXT_MAIN);
                setBackground(sel ? SELECTION_BG : Color.WHITE);
                return this;
            }
        });

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
                boolean right = false;
                for (int c : rightAligned) {
                    right |= c == col;
                }
                l.setHorizontalAlignment(right ? SwingConstants.RIGHT : SwingConstants.LEFT);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, last ? 0 : 1, GRID_COLOR),
                        new EmptyBorder(0, right ? 4 : 10, 0, right ? 10 : 4)));
                return l;
            }
        });

        for (int i = 0; i < widths.length && i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        return table;
    }

    /** Aplica a una columna una insignia (píldora) cuyo color depende del texto de la celda. */
    public static void setBadgeColumn(JTable table, int column, Map<String, Color[]> palette) {
        table.getColumnModel().getColumn(column).setCellRenderer((t, v, sel, focus, row, col) ->
                new Badge(String.valueOf(v), sel ? SELECTION_BG : Color.WHITE,
                        palette.get(String.valueOf(v))));
    }

    /** Paleta {fondo, texto} para una píldora. */
    public static Color[] pill(Color bg, Color fg) {
        return new Color[]{bg, fg};
    }

    /** Envuelve la tabla en un scroll con esquinas redondeadas. */
    public static JComponent wrap(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel rounded = new JPanel(new BorderLayout()) {
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
        };
        rounded.setOpaque(false);
        rounded.add(scroll, BorderLayout.CENTER);
        return rounded;
    }

    private static class Badge extends JPanel {
        private final String text;
        private final Color[] colors;

        Badge(String text, Color rowBg, Color[] colors) {
            this.text = text;
            this.colors = colors != null ? colors : pill(new Color(226, 232, 240), new Color(51, 65, 85));
            setOpaque(true);
            setBackground(rowBg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font f = new Font("Segoe UI", Font.PLAIN, 13);
            FontMetrics fm = g2.getFontMetrics(f);
            int w = fm.stringWidth(text) + 22, h = 26;
            int x = 10, y = (getHeight() - h) / 2;

            g2.setColor(colors[0]);
            g2.fillRoundRect(x, y, w, h, h, h);
            g2.setColor(colors[1]);
            g2.setFont(f);
            g2.drawString(text, x + 11, y + (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}