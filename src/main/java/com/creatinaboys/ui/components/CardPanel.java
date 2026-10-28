package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Panel blanco redondeado con sombra suave. */
public class CardPanel extends JPanel {

    private static final int SHADOW = 8;

    public CardPanel(LayoutManager layout, int padding) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(padding + SHADOW, padding + SHADOW,
                padding + SHADOW + 2, padding + SHADOW));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        for (int i = 0; i < SHADOW; i++) {
            g2.setColor(new Color(15, 23, 42, 5));
            g2.fillRoundRect(i, i + 3, w - i * 2, h - i * 2 - 2, 24, 24);
        }
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(SHADOW, SHADOW, w - SHADOW * 2, h - SHADOW * 2 - 2, 20, 20);
        g2.setColor(ThemeConstants.BORDER_COLOR);
        g2.drawRoundRect(SHADOW, SHADOW, w - SHADOW * 2 - 1, h - SHADOW * 2 - 3, 20, 20);

        g2.dispose();
        super.paintComponent(g);
    }
}