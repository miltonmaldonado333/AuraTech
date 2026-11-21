package com.creatinaboys.ui.theme;

import com.creatinaboys.ui.components.VectorIcon;
import com.creatinaboys.ui.theme.ThemeConstants;
import java.awt.*;
import java.util.function.UnaryOperator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class ModernComboBox extends JComboBox<String> {

    public ModernComboBox(String[] items) {
        this(items, UnaryOperator.identity());
    }

    public ModernComboBox(String[] items, UnaryOperator<String> itemLabel) {
        super(items);
        setUI(new ModernUI());
        setFont(ThemeConstants.FONT_REGULAR);
        setOpaque(false);
        setBorder(null);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean selected, boolean focus) {

                String label = value == null ? "" : itemLabel.apply((String) value);

                JLabel cell = (JLabel) super.getListCellRendererComponent(
                        list, label, index, selected, false);

                cell.setFont(ThemeConstants.FONT_REGULAR);
                cell.setBorder(new EmptyBorder(8, 10, 8, 10));

                if (index == -1) {
                    cell.setOpaque(false);
                    cell.setForeground(ThemeConstants.TEXT_MAIN);
                    cell.setBorder(new EmptyBorder(0, 0, 0, 0));
                } else {
                    cell.setOpaque(true);
                    cell.setBackground(selected ? ThemeConstants.PRIMARY_LIGHT : Color.WHITE);
                    cell.setForeground(selected ? ThemeConstants.PRIMARY_BLUE : ThemeConstants.TEXT_MAIN);
                }

                return cell;
            }
        });
    }

    private static class ModernUI extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton(
                    new VectorIcon(VectorIcon.Type.CHEVRON, 16, ThemeConstants.TEXT_MUTED));

            button.setBorder(null);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);

            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR));
            return popup;
        }
    }
}