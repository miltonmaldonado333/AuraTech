package com.creatinaboys.ui.components;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.*;

/** Escudo del club dibujado con Java2D: balón de fútbol, balón de baloncesto y nombre. */
public class ClubLogo extends JPanel {

    private static final Color NAVY = new Color(30, 58, 110);
    private static final Color NAVY_DARK = new Color(15, 33, 72);
    private static final Color SKY = new Color(96, 165, 250);
    private static final Color ORANGE = new Color(249, 115, 22);

    public ClubLogo() {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 310));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double w = 220, h = 250;
        double x = (getWidth() - w) / 2, y = 52, cx = getWidth() / 2.0;

        // Escudo
        Path2D shield = new Path2D.Double();
        shield.moveTo(x, y + 14);
        shield.quadTo(cx, y - 8, x + w, y + 14);
        shield.lineTo(x + w, y + h * 0.52);
        shield.curveTo(x + w, y + h * 0.78, cx + w * 0.22, y + h * 0.92, cx, y + h);
        shield.curveTo(cx - w * 0.22, y + h * 0.92, x, y + h * 0.78, x, y + h * 0.52);
        shield.closePath();

        g2.setColor(new Color(15, 23, 42, 40));                       // sombra
        g2.translate(0, 5);
        g2.fill(shield);
        g2.translate(0, -5);
        g2.setPaint(new GradientPaint(0, (float) y, NAVY, 0, (float) (y + h), NAVY_DARK));
        g2.fill(shield);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(shield);
        g2.setColor(SKY);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(inset(shield, 0.93, cx, y + h * 0.5));

        // Balones (sobre el borde superior del escudo)
        drawSoccerBall(g2, cx - 34, y + 8, 64);
        drawBasketball(g2, cx + 38, y + 4, 58);

        // Nombre (se ajusta al ancho del escudo)
        g2.setColor(Color.WHITE);
        String name = "CREATINA-BOYS";
        int size = 28;
        Font f = new Font("Segoe UI", Font.BOLD, size);
        while (g2.getFontMetrics(f).stringWidth(name) > w - 34 && size > 12) {
            f = new Font("Segoe UI", Font.BOLD, --size);
        }
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(name, (float) (cx - fm.stringWidth(name) / 2.0), (float) (y + 128));

        // Cinta "CLUB DEPORTIVO"
        String sub = "CLUB DEPORTIVO";
        Font fs = new Font("Segoe UI", Font.BOLD, 14);
        g2.setFont(fs);
        FontMetrics fms = g2.getFontMetrics();
        double bw = w - 44, bh = 26, bx = cx - bw / 2, by = y + 142;
        g2.setColor(new Color(37, 99, 235));
        g2.fill(new RoundRectangle2D.Double(bx, by, bw, bh, 8, 8));
        g2.setColor(Color.WHITE);
        g2.drawString(sub, (float) (cx - fms.stringWidth(sub) / 2.0),
                (float) (by + (bh - fms.getHeight()) / 2 + fms.getAscent()));

        // Estrellas
        g2.setColor(Color.WHITE);
        drawStar(g2, cx - 38, y + 196, 8);
        drawStar(g2, cx, y + 200, 15);
        drawStar(g2, cx + 38, y + 196, 8);

        g2.dispose();
    }

    private static Shape inset(Shape s, double scale, double cx, double cy) {
        AffineTransform t = new AffineTransform();
        t.translate(cx, cy);
        t.scale(scale, scale);
        t.translate(-cx, -cy);
        return t.createTransformedShape(s);
    }

    private static void drawSoccerBall(Graphics2D g, double cx, double cy, double d) {
        double r = d / 2;
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(cx - r, cy - r, d, d));
        g.setColor(NAVY_DARK);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, d, d));
        Path2D pent = new Path2D.Double();
        double pr = r * 0.38;
        for (int i = 0; i < 5; i++) {
            double a = Math.toRadians(-90 + i * 72);
            double px = cx + Math.cos(a) * pr, py = cy + Math.sin(a) * pr;
            if (i == 0) pent.moveTo(px, py); else pent.lineTo(px, py);
            double ox = cx + Math.cos(a) * r * 0.98, oy = cy + Math.sin(a) * r * 0.98;
            g.draw(new Line2D.Double(px, py, cx + Math.cos(a) * r * 0.72, cy + Math.sin(a) * r * 0.72));
            g.fill(new Ellipse2D.Double(ox - 3, oy - 3, 6, 6));
        }
        pent.closePath();
        g.fill(pent);
    }

    private static void drawBasketball(Graphics2D g, double cx, double cy, double d) {
        double r = d / 2;
        g.setColor(ORANGE);
        g.fill(new Ellipse2D.Double(cx - r, cy - r, d, d));
        g.setColor(NAVY_DARK);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, d, d));
        Shape oldClip = g.getClip();
        g.clip(new Ellipse2D.Double(cx - r, cy - r, d, d));      // las costuras no salen del balón
        g.draw(new Line2D.Double(cx, cy - r, cx, cy + r));
        g.draw(new Line2D.Double(cx - r, cy, cx + r, cy));
        g.draw(new Arc2D.Double(cx - r * 1.9, cy - r, r * 1.5, d, -60, 120, Arc2D.OPEN));
        g.draw(new Arc2D.Double(cx + r * 0.4, cy - r, r * 1.5, d, 120, 120, Arc2D.OPEN));
        g.setClip(oldClip);
    }

    private static void drawStar(Graphics2D g, double cx, double cy, double r) {
        Path2D star = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double rad = (i % 2 == 0) ? r : r * 0.45;
            double a = Math.toRadians(-90 + i * 36);
            double px = cx + Math.cos(a) * rad, py = cy + Math.sin(a) * rad;
            if (i == 0) star.moveTo(px, py); else star.lineTo(px, py);
        }
        star.closePath();
        g.fill(star);
    }
}