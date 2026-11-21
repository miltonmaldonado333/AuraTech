package com.creatinaboys.ui.components;

import com.creatinaboys.model.AgeCategory;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.StringJoiner;

/**
 * Ventana que pregunta en qué categoría desea estar un deportista cuando su edad
 * pertenece a más de una. El usuario elige entre esas opciones (y solo entre ellas).
 */
public final class CategoryChooser {

    private CategoryChooser() { }

    /**
     * @param parent         componente sobre el que se centra la ventana
     * @param age            edad del deportista
     * @param options        categorías que incluyen esa edad (2 o más)
     * @param preselectName  categoría marcada al abrir (puede ser null)
     * @return la categoría elegida, o null si se canceló
     */
    public static AgeCategory ask(Component parent, int age, List<AgeCategory> options,
                                  String preselectName) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel title = new JLabel("<html>Con <b>" + age + " años</b> puede estar en más de una categoría.<br>"
                + "¿En cuál desea estar?</html>");
        title.setFont(ThemeConstants.FONT_REGULAR);
        title.setForeground(ThemeConstants.TEXT_MAIN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(12));

        ButtonGroup group = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[options.size()];

        for (int i = 0; i < options.size(); i++) {
            AgeCategory option = options.get(i);
            JRadioButton radio = new JRadioButton("<html><b>" + escape(SpanishLabels.of(option.name()))
                    + "</b> &nbsp;·&nbsp; " + Money.cop(option.monthlyFee().doubleValue()) + " / mes<br>"
                    + "<span style='color:#64748B'>" + rangeText(option.ages()) + "</span></html>");
            radio.setFont(ThemeConstants.FONT_REGULAR);
            radio.setOpaque(false);
            radio.setAlignmentX(Component.LEFT_ALIGNMENT);
            radio.setBorder(new EmptyBorder(6, 2, 6, 2));
            radio.setSelected(option.name().equals(preselectName) || (preselectName == null && i == 0)
                    || (preselectName != null && options.stream().noneMatch(o -> o.name().equals(preselectName))
                            && i == 0));
            group.add(radio);
            content.add(radio);
            radios[i] = radio;
        }

        int result = JOptionPane.showConfirmDialog(parent, content, "Elegir categoría",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        for (int i = 0; i < radios.length; i++) {
            if (radios[i].isSelected()) {
                return options.get(i);
            }
        }
        return null;
    }

    private static String rangeText(List<Integer> ages) {
        if (ages.isEmpty()) {
            return "Sin edades";
        }
        StringJoiner ranges = new StringJoiner(", ");
        int start = ages.get(0);
        int end = start;
        for (int age : ages.subList(1, ages.size())) {
            if (age != end + 1) {
                ranges.add(start == end ? Integer.toString(start) : start + "–" + end);
                start = age;
            }
            end = age;
        }
        ranges.add(start == end ? Integer.toString(start) : start + "–" + end);
        return ranges + " años";
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}