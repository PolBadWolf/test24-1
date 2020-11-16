package ru.yandex.fixcolor.tests.spc.lib.plot2;

import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

class PlotSwing extends PlotParent implements Trend.TrendCallBack {
    private MPanel panel;
    private Graphics2D g2d;
    public PlotSwing(Plot.Parameters parameters, MPanel panel) {
        super(parameters, panel.getWidth(), panel.getHeight());
        this.panel = panel;
        width = panel.getWidth();
        height = panel.getHeight();
        panel.image = new BufferedImage((int) width, (int) height,BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) panel.image.getGraphics();
        // тренд1
        trends[0] = new Trend(this);
        // тренд2
        trends[1] = new Trend(this);
        // sets
        setParametersTrends(parameters);
    }

    @Override
    public void fillRect(Color color, double x, double y, double width, double height) {
        g2d.setColor(color);
        g2d.fillRect((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void drawRect(Color color, double lineWidth, double x, double y, double width, double height) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke((float) lineWidth));
        g2d.drawRect((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void ll(TrendUnit[] units) {

    }

    @Override
    public void drawLines(Color lineColor, double lineWidth, LineParameters[] lines) {
        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke((float) lineWidth));
        for (LineParameters line : lines) {
            g2d.drawLine((int) Math.round(line.x1), (int) Math.round(line.y1), (int) Math.round(line.x2), (int) Math.round(line.y2));
        }
    }

    @Override
    public void drawTitleY(int nTrend) {
        Trend trend = trends[nTrend];
        g2d.setColor(trend.textFontColor);
        Font fnt = g2d.getFont();
        g2d.setFont(new Font(
                fnt.getName(),
                fnt.getStyle(),
                (int) trend.textFontSize
        ));
        //
        double k = windowHeight / (trend.netY_max - trend.netY_min);
        int baseN = netY_n;
        int step = trend.netY_step;
        double offset = k * (trend.netY_min % step);
        int offsetC = trend.netY_min / step;
        double y, yZ, yInv;
        int x1, x2;
        if (trend.positionFromWindow == TrendPosition.left) {
            x1 = (int) (fieldSizeLeft - 5);
        } else {
            x1 = (int) (fieldSizeLeft + windowWidth + 5);
        }
        Rectangle2D textRec;
        String text;
        for (int i = 0; i < (baseN); i++) {
            yZ = (i + offsetC) * trend.netY_step;
            if (yZ > trend.netY_max) break;
            y = (i * step * k) - offset;
            yInv = (windowHeight + fieldSizeTop) - y;
            text = String.valueOf(yZ);
            textRec = g2d.getFontMetrics(g2d.getFont()).getStringBounds(text, g2d);
            if (trend.positionFromWindow == TrendPosition.right) {
                x2 = (int) x1;
            } else {
                x2 = (int) (x1 - textRec.getWidth());
            }
            g2d.drawString(text, x2, (int) (yInv + textRec.getHeight() / 3));
        }
    }
}
