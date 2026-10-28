package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.JComponent;
import java.awt.*;

/** Barra de proporción redondeada (0.0 a 1.0). */
public class ShareBar extends JComponent {

    private final Color color;
    private double value;

    public ShareBar(Color color) {
        this.color = color;
        setOpaque(false);
        setPreferredSize(new Dimension(100, 8));
        setMinimumSize(new Dimension(20, 8));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
    }

    public void setValue(double value) {
        this.value = Math.max(0, Math.min(1, value));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(ThemeConstants.BORDER_COLOR);
        g2.fillRoundRect(0, 0, w, h, h, h);
        int fill = (int) Math.round(w * value);
        if (fill > 0) {
            g2.setColor(color);
            g2.fillRoundRect(0, 0, Math.max(fill, h), h, h, h);
        }
        g2.dispose();
    }
}