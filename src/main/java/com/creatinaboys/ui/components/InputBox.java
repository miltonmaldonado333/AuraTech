package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** Contenedor redondeado con ícono a la izquierda para cualquier campo. */
public class InputBox extends JPanel {

    private boolean focused = false;
    private final JLabel iconLabel;
    private final VectorIcon icon;

    public InputBox(VectorIcon.Type type, JComponent field) {
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(3, 17, 3, 14));
        setPreferredSize(new Dimension(250, 52));

        icon = new VectorIcon(type, 18, ThemeConstants.PLACEHOLDER);
        iconLabel = new JLabel(icon);
        add(iconLabel, BorderLayout.WEST);

        field.setOpaque(false);
        field.setBorder(null);
        add(field, BorderLayout.CENTER);

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { setFocused(true); }
            @Override public void focusLost(FocusEvent e)   { setFocused(false); }
        });
    }

    public void setIcon(VectorIcon.Type type) {
        remove(iconLabel);
        iconLabel.setIcon(new VectorIcon(type, 18,
                focused ? ThemeConstants.PRIMARY_BLUE : ThemeConstants.PLACEHOLDER));
        add(iconLabel, BorderLayout.WEST);
        revalidate();
        repaint();
    }

    private void setFocused(boolean focused) {
        this.focused = focused;
        icon.setColor(focused ? ThemeConstants.PRIMARY_BLUE : ThemeConstants.PLACEHOLDER);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        if (focused) {                                               // halo de enfoque
            g2.setColor(new Color(37, 99, 235, 45));
            g2.fillRoundRect(0, 0, w, h, 18, 18);
        }

        g2.setColor(focused ? Color.WHITE : ThemeConstants.INPUT_BG);
        g2.fillRoundRect(3, 3, w - 6, h - 6, 14, 14);

        g2.setStroke(new BasicStroke(focused ? 1.6f : 1.2f));
        g2.setColor(focused ? ThemeConstants.PRIMARY_BLUE : ThemeConstants.BORDER_COLOR);
        g2.drawRoundRect(3, 3, w - 7, h - 7, 14, 14);

        g2.dispose();
        super.paintComponent(g);
    }
}