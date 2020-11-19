package ru.yandex.fixcolor.tests.spc.lib.plot2;

import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

class PlotSwing extends PlotParent {
    private final Graphics2D g2d;
    private final MPanel panel;
    public PlotSwing(Plot.Parameters parameters, MPanel panel) {
        super(parameters, panel.getWidth(), panel.getHeight());
        this.panel = panel;
        width = panel.getWidth();
        height = panel.getHeight();
        panel.image = new BufferedImage((int) width, (int) height,BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) panel.image.getGraphics();
        // тренд1
        trends[0] = new Trend();
        // тренд2
        trends[1] = new Trend();
        // sets
        setParametersTrends(parameters);
        //
        fistZoomRender();
        //
        threadCycle = new Thread(new Cycle(), "cycle swing");
        threadCycle.start();
    }

    private class Cycle implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            flOnWork = true;
            while (flOnWork) {
                try {
                    if (paintQueue.isEmpty()) {
                        Thread.yield();
                        Thread.sleep(1);
                    } else {
                        SwingUtilities.invokeAndWait(this::runQueueDoCycle);
                    }
                } catch (InterruptedException | InvocationTargetException e) {
                    e.printStackTrace();
                    flOnWork = false;
                }
            }
        }

        private void runQueueDoCycle() {
            DataQueue dataQueue;
            try {
                while ((dataQueue = paintQueue.poll(1, TimeUnit.MILLISECONDS)) != null) {
                    doCicle(dataQueue);
                }
            } catch (Exception exception) {
                MyLogger.myLog.log(Level.SEVERE, "ошибка выполнения очереди", exception);
            }
        }
    }

    protected void __paint_trend(ArrayList<GraphDataUnit> graphData, Trend trend) {
        int graphData_size = graphData.size();
        //
        int[] x = new int[graphData_size];
        int[] y = new int[graphData_size];
        for (int i = 0; i < graphData_size; i++) {
            x[i] = (int) ((graphData.get(i).x - memX_begin) * kX + positionLeft);
            y[i] = (int) (positionBottom - (graphData.get(i).y * trend.kY));
        }
        g2d.setColor(trend.lineColor);
        g2d.setStroke(new BasicStroke((float) trend.lineWidth));
        g2d.drawPolyline(x, y, x.length);
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
        g2d.drawRect((int) (x - lineWidth / 2), (int) (y - lineWidth / 2), (int) (width + lineWidth), (int) (height + lineWidth));
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
    public void drawTrend(Trend trend, ArrayList<Double> ms, ArrayList<Double> yt) {
        int[] x = new int[ms.size()];
        int[] y = new int[x.length];
        int y0 = (int) (fieldSizeTop + windowHeight);
        double kY = windowHeight / (trend.netY_max - trend.netY_min);
        for (int i = 0; i < x.length; i++) {
            x[i] = (int) ((ms.get(i) - memX_begin) * kX + fieldSizeLeft);
            y[i] = y0 - (int) (yt.get(i) * kY);
        }
        g2d.setColor(trend.lineColor);
        g2d.setStroke(new BasicStroke((float) trend.lineWidth));
        g2d.drawPolyline(x, y, x.length);
    }

    protected void __ReFresh() {
        panel.repaint();
    }

    @Override
    public MyRecWidthHeight getRecWidthHeight(String text, double textFontSize) {
        Rectangle2D textRec = g2d.getFontMetrics(g2d.getFont().deriveFont((float) textFontSize)).getStringBounds(text, g2d);
        return new MyRecWidthHeight(textRec.getWidth(), textRec.getHeight());
    }

    @Override
    public void drawStringAlignment(String text, Color textColor, double textFontSize, double x, double y, MyRecWidthHeight textRec, int alignment) {
        float x2 = 0;
        if (alignment == TrendPosition.left) {
            x2 = (float) (x - textRec.width);
        }
        if (alignment == TrendPosition.center) {
            x2 = (float) (x - textRec.width / 2);
        }
        if (alignment == TrendPosition.right) {
            x2 = (float) x;
        }
        g2d.setFont(g2d.getFont().deriveFont((float) textFontSize));
        g2d.setColor(textColor);
        g2d.drawString(text, x2, (float) (y + textRec.height / 3));
    }
}
