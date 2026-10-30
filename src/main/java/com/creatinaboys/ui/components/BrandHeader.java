package com.creatinaboys.ui.components;

import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.JPanel;
import java.awt.*;

/** Logo del club: insignia con degradado + nombre. Reemplaza el JLabel "CREATINA-BOYS". */
public class BrandHeader extends JPanel {

    private final VectorIcon ball = new VectorIcon(VectorIcon.Type.BALL, 26, Color.WHITE);

    public BrandHeader() {
        setOpaque(false);
        setPreferredSize(new Dimension(240, 96));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int x = 22, size = 46, y = (getHeight() - size) / 2;

        g2.setColor(new Color(37, 99, 235, 60));
        g2.fillRoundRect(x - 2, y + 3, size + 4, size + 4, 18, 18);
        g2.setPaint(new GradientPaint(x, y, ThemeConstants.SIDEBAR_ACTIVE,
                x + size, y + size, ThemeConstants.PRIMARY_INDIGO));
        g2.fillRoundRect(x, y, size, size, 16, 16);
        ball.paintIcon(this, g2, x + (size - 26) / 2, y + (size - 26) / 2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g2.drawString("CREATINA-BOYS", x + size + 14, y + 21);

        g2.setColor(new Color(148, 163, 184));
        g2.setFont(ThemeConstants.FONT_SMALL);
        g2.drawString("Club Deportivo", x + size + 14, y + 39);

        g2.dispose();
    }
}