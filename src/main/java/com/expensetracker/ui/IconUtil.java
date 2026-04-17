package com.expensetracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class IconUtil {

    private static final int S = 18; // Size

    public static Icon createAddIcon() {
        return new CustomIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = initG2(g);
                g2.translate(x, y);
                g2.setPaint(new Color(40, 167, 69)); // Greenish
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Double(S/2.0, 3, S/2.0, S-3));
                g2.draw(new Line2D.Double(3, S/2.0, S-3, S/2.0));
                g2.dispose();
            }
        };
    }

    public static Icon createResetIcon() {
        return new CustomIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = initG2(g);
                g2.translate(x, y);
                g2.setPaint(new Color(220, 53, 69)); // Reddish
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Circle arc
                g2.draw(new Arc2D.Double(2, 2, S-4, S-4, 45, 270, Arc2D.OPEN));
                // Arrow head
                g2.drawPolygon(new int[]{S-3, S-3, S-7}, new int[]{S/2, 2, 4}, 3);
                g2.dispose();
            }
        };
    }

    public static Icon createChartIcon() {
        return new CustomIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = initG2(g);
                g2.translate(x, y);
                g2.setPaint(new Color(0, 123, 255)); // Blueish
                g2.fill(new Arc2D.Double(2, 2, S-4, S-4, 0, 240, Arc2D.PIE));
                g2.setPaint(new Color(255, 193, 7)); // Yellow
                g2.fill(new Arc2D.Double(3, 1, S-6, S-6, 240, 120, Arc2D.PIE));
                g2.dispose();
            }
        };
    }

    public static Icon createHistoryIcon() {
        return new CustomIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = initG2(g);
                g2.translate(x, y);
                g2.setPaint(new Color(108, 117, 125)); // Gray
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Ellipse2D.Double(2, 2, S-4, S-4));
                g2.draw(new Line2D.Double(S/2.0, 4, S/2.0, S/2.0));
                g2.draw(new Line2D.Double(S/2.0, S/2.0, S-6, S/2.0+2));
                g2.dispose();
            }
        };
    }

    public static Icon createExportIcon() {
        return new CustomIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = initG2(g);
                g2.translate(x, y);
                g2.setPaint(new Color(23, 162, 184)); // Cyan
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Arrow down
                g2.draw(new Line2D.Double(S/2.0, 2, S/2.0, S-6));
                g2.draw(new Line2D.Double(S/2.0-3, S-9, S/2.0, S-6));
                g2.draw(new Line2D.Double(S/2.0+3, S-9, S/2.0, S-6));
                // Tray
                g2.draw(new Line2D.Double(3, S-3, S-3, S-3));
                g2.draw(new Line2D.Double(3, S-6, 3, S-3));
                g2.draw(new Line2D.Double(S-3, S-6, S-3, S-3));
                g2.dispose();
            }
        };
    }

    public static Icon createSettingsIcon() {
        return new CustomIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = initG2(g);
                g2.translate(x, y);
                g2.setPaint(new Color(100, 100, 100));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Ellipse2D.Double(4, 4, S-8, S-8));
                g2.setStroke(new BasicStroke(2f));
                for(int i=0; i<8; i++) {
                    double angle = Math.PI / 4 * i;
                    double cx = S/2.0, cy = S/2.0;
                    double x1 = cx + Math.cos(angle)*4;
                    double y1 = cy + Math.sin(angle)*4;
                    double x2 = cx + Math.cos(angle)*7;
                    double y2 = cy + Math.sin(angle)*7;
                    g2.draw(new Line2D.Double(x1, y1, x2, y2));
                }
                g2.dispose();
            }
        };
    }

    private static abstract class CustomIcon implements Icon {
        @Override public int getIconWidth() { return S; }
        @Override public int getIconHeight() { return S; }

        protected Graphics2D initG2(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            return g2;
        }
    }
}
