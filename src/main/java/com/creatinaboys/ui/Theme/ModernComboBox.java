package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

/** JComboBox sin bordes nativos, con chevron vectorial y lista estilizada. */
public class ModernComboBox extends JComboBox<String> {

    public ModernComboBox(String[] items) {
        super(items);
        setUI(new ModernUI());
        setFont(ThemeConstants.FONT_REGULAR);
        setOpaque(false);
        setBorder(null);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, selected, false);
                l.setFont(ThemeConstants.FONT_REGULAR);
                l.setBorder(new EmptyBorder(8, 10, 8, 10));
                if (index == -1) {
                    l.setOpaque(false);
                    l.setForeground(ThemeConstants.TEXT_MAIN);
                    l.setBorder(new EmptyBorder(0, 0, 0, 0));
                } else {
                    l.setOpaque(true);
                    l.setBackground(selected ? ThemeConstants.PRIMARY_LIGHT : Color.WHITE);
                    l.setForeground(selected ? ThemeConstants.PRIMARY_BLUE : ThemeConstants.TEXT_MAIN);
                }
                return l;
            }
        });
    }

    private static class ModernUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton(new VectorIcon(VectorIcon.Type.CHEVRON, 16, ThemeConstants.TEXT_MUTED));
            b.setBorder(null);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            return b;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) { }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup p = (BasicComboPopup) super.createPopup();
            p.setBorder(BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR));
            return p;
        }
    }
}