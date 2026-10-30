package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Botón primario (degradado azul), peligro (rojo) o secundario (blanco con borde), con ícono opcional. */
public class ModernButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER }

    private final Style style;
    private final VectorIcon icon;
    private boolean hover, pressed;

    public ModernButton(String text, VectorIcon.Type iconType, Style style, int width) {
        super(text);
        this.style = style;
        Color iconColor = style == Style.SECONDARY ? ThemeConstants.CANCEL_TEXT : Color.WHITE;
        this.icon = iconType == null ? null : new VectorIcon(iconType, 16, iconColor);

        setFont(ThemeConstants.FONT_BOLD);
        setPreferredSize(new Dimension(width, 46));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)   { hover = false; pressed = false; repaint(); }
            @Override public void mousePressed(MouseEvent e)  { pressed = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), bodyH = getHeight() - 5, off = pressed ? 1 : 0;
        Color fg;

        if (style == Style.SECONDARY) {
            g2.setColor(hover ? ThemeConstants.CANCEL_BG : Color.WHITE);
            g2.fillRoundRect(0, off, w, bodyH, 14, 14);
            g2.setColor(ThemeConstants.BORDER_COLOR);
            g2.drawRoundRect(0, off, w - 1, bodyH - 1, 14, 14);
            fg = ThemeConstants.CANCEL_TEXT;
        } else {
            Color base, c1, c2;
            if (style == Style.PRIMARY) {
                base = ThemeConstants.PRIMARY_BLUE;
                c1 = hover ? ThemeConstants.PRIMARY_HOVER : ThemeConstants.PRIMARY_BLUE;
                c2 = hover ? new Color(67, 56, 202) : ThemeConstants.PRIMARY_INDIGO;
            } else {
                base = ThemeConstants.DANGER_RED;
                c1 = hover ? ThemeConstants.DANGER_HOVER : ThemeConstants.DANGER_RED;
                c2 = hover ? new Color(159, 18, 57) : new Color(225, 29, 72);
            }
            if (!pressed) {
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), hover ? 90 : 55));
                g2.fillRoundRect(3, 5, w - 6, bodyH, 14, 14);
            }
            g2.setPaint(new GradientPaint(0, 0, c1, w, 0, c2));
            g2.fillRoundRect(0, off, w, bodyH, 14, 14);
            fg = Color.WHITE;
        }

        FontMetrics fm = g2.getFontMetrics(getFont());
        int textW = fm.stringWidth(getText());
        int iconW = icon != null ? icon.getIconWidth() + 8 : 0;
        int x = (w - (iconW + textW)) / 2;

        if (icon != null) {
            icon.paintIcon(this, g2, x, off + (bodyH - icon.getIconHeight()) / 2);
        }
        g2.setColor(fg);
        g2.setFont(getFont());
        g2.drawString(getText(), x + iconW, off + (bodyH - fm.getHeight()) / 2 + fm.getAscent());

        g2.dispose();
    }
}