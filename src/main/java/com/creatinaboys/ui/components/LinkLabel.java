package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

/** Texto con apariencia de enlace (azul, subrayado al pasar el mouse) que ejecuta una acción al hacer clic. */
public class LinkLabel extends JLabel {

    public LinkLabel(String text, Runnable action) {
        super(text, SwingConstants.CENTER);
        final Font normal = ThemeConstants.FONT_BOLD.deriveFont(13f);
        final Font hover = normal.deriveFont(Map.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON));
        setFont(normal);
        setForeground(ThemeConstants.PRIMARY_BLUE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setFont(hover); }
            @Override public void mouseExited(MouseEvent e)  { setFont(normal); }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
    }
}