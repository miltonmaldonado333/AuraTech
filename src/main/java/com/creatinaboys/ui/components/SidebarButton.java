package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Botón del menú lateral con ícono, hover suave y estado activo con degradado. */
public class SidebarButton extends JButton {

    private static final Color TEXT_IDLE = new Color(203, 213, 225);

    private boolean active = false;
    private boolean hover = false;
    private final VectorIcon icon;

    /** Constructor compatible: el ícono se deduce del texto. */
    public SidebarButton(String text) {
        this(text, iconFor(text));
    }

    public SidebarButton(String text, VectorIcon.Type type) {
        super(text);
        this.icon = new VectorIcon(type, 20, TEXT_IDLE);
        setFont(ThemeConstants.FONT_BOLD);
        setForeground(TEXT_IDLE);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(13, 18, 13, 18));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
        });
    }

    private static VectorIcon.Type iconFor(String text) {
        return switch (text.trim().toLowerCase()) {
            case "inicio" -> VectorIcon.Type.HOME;
            case "deportistas" -> VectorIcon.Type.USERS;
            case "registrar" -> VectorIcon.Type.USER_PLUS;
            case "categorías", "categorias" -> VectorIcon.Type.TAG;
            case "disciplinas" -> VectorIcon.Type.TROPHY;
            case "entrenadores" -> VectorIcon.Type.WHISTLE;
            case "pagos" -> VectorIcon.Type.CARD;
            case "reportes" -> VectorIcon.Type.CHART;
            case "configuración", "configuracion" -> VectorIcon.Type.GEAR;
            default -> VectorIcon.Type.BALL;
        };
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        if (active) {
            g2.setColor(new Color(37, 99, 235, 60));                       // resplandor
            g2.fillRoundRect(0, 3, w, h - 1, 14, 14);
            g2.setPaint(new GradientPaint(0, 0, ThemeConstants.SIDEBAR_ACTIVE,
                    w, 0, ThemeConstants.PRIMARY_INDIGO));
            g2.fillRoundRect(0, 0, w, h - 2, 14, 14);
        } else if (hover) {
            g2.setColor(ThemeConstants.SIDEBAR_HOVER);
            g2.fillRoundRect(0, 0, w, h - 2, 14, 14);
        }

        Color fg = (active || hover) ? Color.WHITE : TEXT_IDLE;
        icon.setColor(fg);
        int iconY = (h - 2 - icon.getIconHeight()) / 2;
        icon.paintIcon(this, g2, getInsets().left, iconY);

        FontMetrics fm = g2.getFontMetrics(getFont());
        int textX = getInsets().left + icon.getIconWidth() + 14;
        int textY = (h - 2 - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(fg);
        g2.setFont(getFont());
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}