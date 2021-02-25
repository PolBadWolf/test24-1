package ru.yandex.fixcolor.tests.spc.lib.plot2;

import javafx.scene.text.TextAlignment;
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
//        width = panel.getWidth() * scale_img;
//        height = panel.getHeight() * scale_img;
        panel.image = new BufferedImage((int) width, (int) height,BufferedImage.TYPE_INT_ARGB);
        panel.scale_img = scale_img;
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
            DataQueue dataQueue;
            flOnWork = true;
            try {
                while (flOnWork) {
                    if ((dataQueue = paintQueue.poll(1, TimeUnit.MILLISECONDS)) == null) {
                        deferredWork();
                        Thread.sleep(1);
                    } else  {
                        doCicle(dataQueue);
                    }
                }
            } catch (Exception exception) {
                MyLogger.myLog.log(Level.SEVERE, "ошибка выполнения очереди", exception);
            }
        }
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
    // ========================================================
    private static class TrendPaintUnit {
        public int[] x;
        public int[] y;
        Color trendColor;
        double trendWidth;

        public TrendPaintUnit(int[] x, int[] y, Color trendColor, double trendWidth) {
            this.x = x;
            this.y = y;
            this.trendColor = trendColor;
            this.trendWidth = trendWidth;
        }
    }
    private static class TitleText {
        public double x;
        public double y;
        public Color color;
        public TextAlignment alignment;
        public String text;
        public double textFontSize;

        public TitleText(double x, double y, Color color, TextAlignment alignment, String text, double textFontSize) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.alignment = alignment;
            this.text = text;
            this.textFontSize = textFontSize;
        }
    }
    private static class LinesParameters {
        public LineParameters[] lines;
        public Color lineColor;
        public double lineWidth;

        public LinesParameters(LineParameters[] lines, Color lineColor, double lineWidth) {
            this.lines = lines;
            this.lineColor = lineColor;
            this.lineWidth = lineWidth;
        }
    }
    // ========================================================
    private TrendPaintUnit __createPaintTrend(ArrayList<GraphDataUnit> graphData, Trend trend, double kY) {
        int graphData_size = graphData.size();
        //
        int[] x = new int[graphData_size];
        int[] y = new int[graphData_size];
        for (int i = 0; i < graphData_size; i++) {
            x[i] = (int) ((graphData.get(i).x - memX_begin) * kX + positionLeft);
            y[i] = (int) (positionBottom - renderY_zoom(kY, trend.netY_min, trend.netY_max, graphData.get(i).y));
        }
        return new TrendPaintUnit(x, y, trend.lineColor, trend.lineWidth);
    }
    private void __createLinesAndTitle(ArrayList<TitleText> arrayTitleText, ArrayList<LinesParameters> arrayLines) {
        if (trends == null || trends.length < 2) return;
        __createLinesAndTitleX(arrayTitleText, arrayLines);
        __createLinesAndTitleY(arrayTitleText, arrayLines);
    }
    private void __createLinesAndTitleX(ArrayList<TitleText> arrayTitleText, ArrayList<LinesParameters> arrayLines) {
        String text;
        MyRecWidthHeight textRec;
        double polNetLineWidth = netLineWidth / 2;
        double x, y1 = polNetLineWidth, y2 = windowHeight - polNetLineWidth;
        LineParameters[] lines = new LineParameters[xN];
        double offsetS2 = (xStep - (memX_begin % xStep)) % xStep;
        int offsetCel = ((int) Math.ceil(memX_begin / xStep))* xStep;
        for (int i = 0; i < xN; i++) {
            x = (i * xStep + offsetS2) * kX + fieldSizeLeft;
            text = String.valueOf((double) ((i * xStep) + offsetCel) / 1_000);
            textRec = getRecWidthHeight(text, fieldFontSizeBottom);
            arrayTitleText.add(drawStringAlignment2(text, fieldFontColorBottom, fieldFontSizeBottom, x - textRec.width / 2, positionBottom + textRec.height / 3, textRec, TrendPosition.center));
            lines[i] = new LineParameters(x, positionBottom - y1, x, positionBottom - y2);
        }
        // подпись сек.
        x = positionRight; //x + textRec.width;
        text = " сек.";
        textRec = getRecWidthHeight(text, fieldFontSizeBottom);
        arrayTitleText.add(drawStringAlignment2(text, fieldFontColorBottom, fieldFontSizeBottom, x - textRec.width / 2, positionBottom + textRec.height / 3, textRec, TrendPosition.center));
        //
        arrayLines.add(new LinesParameters(lines, netLineColor, netLineWidth));
        // линия указания обратного хода
        if (pointBackMove_time > memX_begin && pointBackMove_time < memX_end) {
            lines = new LineParameters[1];
            double x_zero = memX_begin - (memX_begin % xStep);
            x = (pointBackMove_time - x_zero) * kX + fieldSizeLeft;
            lines[0] = new LineParameters(x, positionBottom - y1, x, positionBottom - y2);
            arrayLines.add(new LinesParameters(lines, pointBackMove_color, pointBackMove_lineWidth));
        }
        // линия указания начало полки
        if (pointBeginShelf_time > memX_begin && pointBeginShelf_time < memX_end) {
            lines = new LineParameters[1];
            double x_zero = memX_begin - (memX_begin % xStep);
            x = (pointBeginShelf_time - x_zero) * kX + fieldSizeLeft;
            lines[0] = new LineParameters(x, positionBottom - y1, x, positionBottom - y2);
            arrayLines.add(new LinesParameters(lines, pointBeginShelf_color, pointBeginShelf_lineWidth));
        }
    }
    private void __createLinesAndTitleY(ArrayList<TitleText> arrayTitleText, ArrayList<LinesParameters> arrayLines) {
        __createlinesY(arrayLines);
        __createTitlesY(arrayTitleText);
    }
    private TitleText drawStringAlignment2(String text, Color textColor, double textFontSize, double x, double y, MyRecWidthHeight textRec, int alignment) {
        float x2 = (float) x;
        TextAlignment textAlignment = TextAlignment.CENTER;
        switch (alignment) {
            case TrendPosition.left:
                textAlignment = TextAlignment.RIGHT;
                break;
            case TrendPosition.center:
                textAlignment = TextAlignment.CENTER;
                break;
            case TrendPosition.right:
                textAlignment = TextAlignment.LEFT;
                break;
        }
        return new TitleText(x2, y + textRec.height / 4, textColor, textAlignment, text, textFontSize);
    }
    private void __createlinesY(ArrayList<LinesParameters> arrayLines) {
        if (trends[0] == null) return;
        LineParameters[] lines;
        if ((trends[0].netY_min % trends[0].netY_step) == 0) y_FistN = 1;
        else y_FistN = 0;
        //
        int step = trends[0].netY_step;
        double offset = trends[0].kY * (trends[0].netY_min % step);
        lines = new LineParameters[y_netN - y_FistN];
        double x1 = fieldSizeLeft + netLineWidth / 2;
        double x2 = fieldSizeLeft + windowWidth - netLineWidth / 2;
        double y, yInv;
        for (int i = y_FistN, indx = 0; i < (y_netN); i++, indx++) {
            y = (i * step * trends[0].kY) - offset;
            yInv = (windowHeight + fieldSizeTop) - y;
            lines[indx] = new LineParameters(x1, yInv, x2, yInv);
        }
        arrayLines.add(new LinesParameters(lines, netLineColor, netLineWidth));
    }
    private void __createTitlesY(ArrayList<TitleText> arrayTitleText) {
        double y, yZ;
        double x1, x2;
        MyRecWidthHeight textRec;
        for (int i = 0; i < 2; i++) {
            Trend trend = trends[i];
            if (trend == null) break;
            int baseN = y_netN;
            int step = trend.netY_step;
            double offset = trend.kY * (trend.netY_min % step);
            int offsetC = trend.netY_min / step;
            //
            if (trend.positionFromWindow == TrendPosition.left) {
                x1 = positionLeft - 5 * scale_img;
            } else {
                x1 = positionRight + 5 * scale_img;
            }
            String text;
            double textFontSize = trend.textFontSize;
            for (int j = 0; j < (baseN); j++) {
                yZ = (j + offsetC) * trend.netY_step;
                if (yZ > trend.netY_max) break;
                y = (j * step * trend.kY) - offset;
                text = (int) yZ + "" + trend.text;
                textRec = getRecWidthHeight(text, textFontSize);
                switch (trend.positionFromWindow) {
                    case TrendPosition.left:
                        x2 = x1 - textRec.width;
                        break;
                    case TrendPosition.center:
                        x2 = x1 - textRec.width / 2;
                        break;
                    default:
                        x2 = x1;
                }
                arrayTitleText.add(drawStringAlignment2(text, trend.textFontColor, textFontSize, x2, positionBottom - y - textRec.height / 3, textRec, trend.positionFromWindow));
            }
        }
    }
    // ========================================================
    private void __drawlines(ArrayList<LinesParameters> arrayLines) {
        for (LinesParameters linesUnit : arrayLines) {
            g2d.setColor(linesUnit.lineColor);
            g2d.setStroke(new BasicStroke((float) linesUnit.lineWidth));
            for (LineParameters line : linesUnit.lines) {
                g2d.drawLine((int) Math.round(line.x1), (int) Math.round(line.y1), (int) Math.round(line.x2), (int) Math.round(line.y2));
            }
        }
    }
    private void __drawTitles(ArrayList<TitleText> arrayTitleText) {
        Rectangle2D textRec;
        for (TitleText titleText : arrayTitleText) {
            g2d.setFont(g2d.getFont().deriveFont((float) titleText.textFontSize));
            g2d.setColor(titleText.color);
            textRec = g2d.getFontMetrics(g2d.getFont()).getStringBounds(titleText.text, g2d);
            g2d.drawString(titleText.text, (float) titleText.x, (float) (titleText.y + textRec.getHeight() / 3));
        }
    }
    // ========================================================

    // оптимизированная отрисовка трендов с очисткой экрана
    protected void __paint(GraphData[] datGraph) {
        try {
            TrendPaintUnit[] trendPaint = new TrendPaintUnit[datGraph.length];
            for (int t = 0; t < datGraph.length; t++) {
                trendPaint[t] = __createPaintTrend(datGraph[t].zn, trends[t], datGraph[t].kY);
            }
            ArrayList<TitleText> arrayTitleText = new ArrayList<>();
            ArrayList<LinesParameters> arrayLines = new ArrayList<>();
            __createLinesAndTitle(arrayTitleText, arrayLines);
            SwingUtilities.invokeAndWait(()->{
                try {
                    // окно
                    fillRect(windowBackColor, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
                    // сетка
                    __drawlines(arrayLines);
                    // отрисовка трендов
                    for (int t = 0; t < datGraph.length; t++) {
                        g2d.setColor(trendPaint[t].trendColor);
                        g2d.setStroke(new BasicStroke((float) trendPaint[t].trendWidth));
                        g2d.drawPolyline(trendPaint[t].x, trendPaint[t].y, trendPaint[t].x.length);
                    }
                    // top
                    fillRect(fieldBackColor, fieldSizeLeft, 0, windowWidth, fieldSizeTop);
                    // left
                    fillRect(fieldBackColor, 0, 0, fieldSizeLeft, height);
                    // right
                    fillRect(fieldBackColor, width - fieldSizeRight, 0, fieldSizeRight, height);
                    // bottom
                    fillRect(fieldBackColor, fieldSizeLeft, height - fieldSizeBottom, windowWidth, fieldSizeBottom);
                    // рамка
                    drawRect(fieldFrameColor, fieldFrameWidth, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
                    __drawTitles(arrayTitleText);
                } catch (Exception exception) {
                    System.out.println(flOnWork);
                    exception.printStackTrace();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void __clear() throws InvocationTargetException, InterruptedException {
        ArrayList<TitleText> arrayTitleText = new ArrayList<>();
        ArrayList<LinesParameters> arrayLines = new ArrayList<>();
        __createLinesAndTitle(arrayTitleText, arrayLines);
        SwingUtilities.invokeAndWait(()->{
            try {
                // окно
                fillRect(windowBackColor, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
                // сетка
                __drawlines(arrayLines);
                // top
                fillRect(fieldBackColor, fieldSizeLeft, 0, windowWidth, fieldSizeTop);
                // left
                fillRect(fieldBackColor, 0, 0, fieldSizeLeft, height);
                // right
                fillRect(fieldBackColor, width - fieldSizeRight, 0, fieldSizeRight, height);
                // bottom
                fillRect(fieldBackColor, fieldSizeLeft, height - fieldSizeBottom, windowWidth, fieldSizeBottom);
                // рамка
                drawRect(fieldFrameColor, fieldFrameWidth, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
                // титлы
                __drawTitles(arrayTitleText);
            } catch (Exception exception) {
                System.out.println(flOnWork);
                exception.printStackTrace();
            }
        });
    }

    protected void __ReFresh() {
//        panel.validate();
        panel.repaint();

    }

    @Override
    public MyRecWidthHeight getRecWidthHeight(String text, double textFontSize) {
        Rectangle2D textRec = g2d.getFontMetrics(g2d.getFont().deriveFont((float) textFontSize)).getStringBounds(text, g2d);
        return new MyRecWidthHeight(textRec.getWidth(), textRec.getHeight());
    }

}
