package com.creatinaboys.ui.components;

import javax.swing.JComponent;
import java.awt.*;

/** Círculo de color suave con un ícono vectorial centrado. */
public class IconBadge extends JComponent {

    private final VectorIcon icon;
    private final Color tint;

    public IconBadge(VectorIcon.Type type, Color accent, int size) {
        this.icon = new VectorIcon(type, size / 2, accent);
        this.tint = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28);
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(tint);
        g2.fillOval(0, 0, getWidth(), getHeight());
        icon.paintIcon(this, g2, (getWidth() - icon.getIconWidth()) / 2,
                (getHeight() - icon.getIconHeight()) / 2);
        g2.dispose();
    }
}