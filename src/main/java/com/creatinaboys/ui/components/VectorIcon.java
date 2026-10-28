package com.creatinaboys.ui.components;

import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.*;

/** Íconos vectoriales dibujados con Java2D (cuadrícula de 24x24). */
public class VectorIcon implements Icon {

    public enum Type {
    HOME, USERS, USER_PLUS, TAG, TROPHY, WHISTLE, CARD, CHART, GEAR,
    ID, USER, PHONE, HASH, BALL, CALENDAR, CHECK, CLOSE, RULER, FLAG, CHEVRON,
    SEARCH, TRASH, LOGOUT, CHEV_LEFT, CHEV_RIGHT
    }

    private final Type type;
    private final int size;
    private Color color;

    public VectorIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    public void setColor(Color color) { this.color = color; }

    @Override public int getIconWidth()  { return size; }
    @Override public int getIconHeight() { return size; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        g2.scale(size / 24.0, size / 24.0);
        g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);
        draw(g2);
        g2.dispose();
    }

    private void draw(Graphics2D g) {
        switch (type) {
            case HOME -> {
                poly(g, false, 3, 11, 12, 3, 21, 11);
                poly(g, false, 5, 9.5, 5, 20, 19, 20, 19, 9.5);
                poly(g, false, 10, 20, 10, 14, 14, 14, 14, 20);
            }
            case USER -> {
                g.draw(new Ellipse2D.Double(8, 3.5, 8, 8));
                g.draw(new Arc2D.Double(4, 14, 16, 14, 0, 180, Arc2D.OPEN));
            }
            case USERS -> {
                g.draw(new Ellipse2D.Double(5, 5, 8, 8));
                g.draw(new Arc2D.Double(2, 15, 14, 12, 0, 180, Arc2D.OPEN));
                g.draw(new Ellipse2D.Double(15, 6, 6, 6));
                g.draw(new Arc2D.Double(16.5, 14.5, 6, 8, 0, 180, Arc2D.OPEN));
            }
            case USER_PLUS -> {
                g.draw(new Ellipse2D.Double(4, 5, 8, 8));
                g.draw(new Arc2D.Double(1, 15, 14, 12, 0, 180, Arc2D.OPEN));
                line(g, 18, 8, 18, 14);
                line(g, 15, 11, 21, 11);
            }
            case TAG -> {
                poly(g, true, 3, 12, 12, 3, 21, 3, 21, 12, 12, 21);
                g.draw(new Ellipse2D.Double(15, 6, 3, 3));
            }
            case TROPHY -> {
                Path2D cup = new Path2D.Double();
                cup.moveTo(7, 4); cup.lineTo(17, 4); cup.lineTo(17, 9);
                cup.curveTo(17, 12, 14.8, 14, 12, 14);
                cup.curveTo(9.2, 14, 7, 12, 7, 9);
                cup.closePath();
                g.draw(cup);
                Path2D left = new Path2D.Double();
                left.moveTo(7, 6); left.lineTo(3.5, 6); left.curveTo(3.5, 10, 5, 11, 7, 11);
                g.draw(left);
                Path2D right = new Path2D.Double();
                right.moveTo(17, 6); right.lineTo(20.5, 6); right.curveTo(20.5, 10, 19, 11, 17, 11);
                g.draw(right);
                line(g, 12, 14, 12, 18);
                line(g, 8, 20.5, 16, 20.5);
            }
            case WHISTLE -> {
                g.draw(new Ellipse2D.Double(3, 10, 11, 11));
                g.fill(new Ellipse2D.Double(7.5, 14.5, 2, 2));
                poly(g, false, 11, 11.5, 21, 8, 21, 12.5, 13.5, 13);
            }
            case CARD -> {
                g.draw(new RoundRectangle2D.Double(3, 5, 18, 14, 3, 3));
                line(g, 3, 10, 21, 10);
                line(g, 7, 15, 11, 15);
            }
            case CHART -> {
                poly(g, false, 4, 4, 4, 20, 20, 20);
                g.draw(new Rectangle2D.Double(8, 12, 3, 8));
                g.draw(new Rectangle2D.Double(13, 7, 3, 13));
                g.draw(new Rectangle2D.Double(18, 10, 2.5, 10));
            }
            case GEAR -> {
                g.draw(new Ellipse2D.Double(9, 9, 6, 6));
                g.draw(new Ellipse2D.Double(5.5, 5.5, 13, 13));
                for (int i = 0; i < 8; i++) {
                    double a = Math.toRadians(i * 45);
                    line(g, 12 + Math.cos(a) * 6.5, 12 + Math.sin(a) * 6.5,
                            12 + Math.cos(a) * 9.5, 12 + Math.sin(a) * 9.5);
                }
            }
            case ID -> {
                g.draw(new RoundRectangle2D.Double(3, 5, 18, 14, 3, 3));
                g.draw(new Ellipse2D.Double(6.5, 8.5, 4, 4));
                g.draw(new Arc2D.Double(5.5, 13, 6, 6, 0, 180, Arc2D.OPEN));
                line(g, 14, 10, 18, 10);
                line(g, 14, 14, 18, 14);
            }
            case PHONE -> {
                g.draw(new RoundRectangle2D.Double(6.5, 2.5, 11, 19, 3, 3));
                line(g, 10.5, 18, 13.5, 18);
            }
            case HASH -> {
                line(g, 9.5, 4, 7.5, 20);
                line(g, 16.5, 4, 14.5, 20);
                line(g, 4, 9, 20, 9);
                line(g, 4, 15, 20, 15);
            }
            case BALL -> {
                g.draw(new Ellipse2D.Double(3, 3, 18, 18));
                g.draw(new Ellipse2D.Double(9.5, 9.5, 5, 5));
                for (int i = 0; i < 5; i++) {
                    double a = Math.toRadians(90 + i * 72);
                    line(g, 12 + Math.cos(a) * 2.5, 12 - Math.sin(a) * 2.5,
                            12 + Math.cos(a) * 9, 12 - Math.sin(a) * 9);
                }
            }
            case CALENDAR -> {
                g.draw(new RoundRectangle2D.Double(3, 5, 18, 16, 3, 3));
                line(g, 3, 10, 21, 10);
                line(g, 8, 3, 8, 7);
                line(g, 16, 3, 16, 7);
            }
            case CHECK -> poly(g, false, 5, 12.5, 10, 17.5, 19, 7);
            case CLOSE -> { line(g, 6, 6, 18, 18); line(g, 18, 6, 6, 18); }
            case RULER -> {
                g.draw(new RoundRectangle2D.Double(2.5, 8, 19, 8, 2, 2));
                line(g, 7, 8, 7, 12);
                line(g, 11, 8, 11, 11);
                line(g, 15, 8, 15, 12);
                line(g, 19, 8, 19, 11);
            }
            case FLAG -> {
                line(g, 5, 3, 5, 21);
                poly(g, true, 5, 4, 19, 4, 15, 8.5, 19, 13, 5, 13);
            }
            case CHEVRON -> poly(g, false, 6, 9, 12, 15, 18, 9);
                        case SEARCH -> {
                g.draw(new Ellipse2D.Double(4, 4, 12, 12));
                line(g, 14.5, 14.5, 20, 20);
            }
            case TRASH -> {
                line(g, 4, 7, 20, 7);
                poly(g, false, 9, 7, 9, 4, 15, 4, 15, 7);
                poly(g, false, 6, 7, 7, 20, 17, 20, 18, 7);
                line(g, 10, 11, 10, 16);
                line(g, 14, 11, 14, 16);
            }
            case LOGOUT -> {
                poly(g, false, 10, 4, 5, 4, 5, 20, 10, 20);
                line(g, 10, 12, 20, 12);
                poly(g, false, 16, 8, 20, 12, 16, 16);
            }
            case CHEV_LEFT -> poly(g, false, 15, 6, 9, 12, 15, 18);
            case CHEV_RIGHT -> poly(g, false, 9, 6, 15, 12, 9, 18);
        }
    }

    private static void line(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private static void poly(Graphics2D g, boolean close, double... pts) {
        Path2D p = new Path2D.Double();
        p.moveTo(pts[0], pts[1]);
        for (int i = 2; i < pts.length; i += 2) p.lineTo(pts[i], pts[i + 1]);
        if (close) p.closePath();
        g.draw(p);
    }
}