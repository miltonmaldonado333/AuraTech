package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Cursor;

/**
 * Botón estilizado para el menú de navegación lateral.
 */
public class SidebarButton extends JButton {

    private boolean active = false;

    public SidebarButton(String text) {
        super(text);
        setFont(ThemeConstants.FONT_BOLD);
        setForeground(ThemeConstants.SIDEBAR_TEXT);
        setBackground(ThemeConstants.SIDEBAR_BG);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(12, 20, 12, 20));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!active) {
                    setBackground(ThemeConstants.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!active) {
                    setBackground(ThemeConstants.SIDEBAR_BG);
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            setBackground(ThemeConstants.SIDEBAR_ACTIVE);
            setForeground(java.awt.Color.WHITE);
        } else {
            setBackground(ThemeConstants.SIDEBAR_BG);
            setForeground(ThemeConstants.SIDEBAR_TEXT);
        }
    }
}