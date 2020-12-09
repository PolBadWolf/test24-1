package ru.yandex.fixcolor.tests.spc.lib.plot2;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class PlotFx extends PlotParent {
    private final GraphicsContext gc;
    public PlotFx(Plot.Parameters parameters, Canvas canvas) {
        super(parameters, canvas.getWidth(), canvas.getHeight());
        gc = canvas.getGraphicsContext2D();
        // тренд1
        trends[0] = new Trend();
        // тренд2
        trends[1] = new Trend();
        // sets
        setParametersTrends(parameters);
        //
        fistZoomRender();
        //
        threadCycle = new Thread(new PlotFx.Cycle(), "cycle fx");
        threadCycle.start();
    }

    private class Cycle implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 2);
            Platform.runLater(()-> Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1));
            DataQueue dataQueue;
            flOnWork = true;
            try {
                while (flOnWork) {
                    if ((dataQueue = paintQueue.poll(5, TimeUnit.MILLISECONDS)) != null) {
                        doCicle(dataQueue);
                        if (dataQueue.command == command_Paint) {
                            Thread.sleep(1);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private javafx.scene.paint.Color colorAwtToFx(Color color) {
        return new javafx.scene.paint.Color(
                color.getRed() / 255.0,
                color.getGreen() / 255.0,
                color.getBlue() / 255.0,
                color.getAlpha() / 255.0
        );
    }

    private void fillRect2(Color color, double x, double y, double width, double height) {
        gc.beginPath();
        gc.setFill(colorAwtToFx(color));
        gc.fillRect(x, y, width, height);
        gc.closePath();
    }

    private void drawRect2(Color color, double lineWidth, double x, double y, double width, double height) {
        gc.beginPath();
        gc.setStroke(colorAwtToFx(color));
        gc.setLineWidth(lineWidth);
        gc.strokeRect(x - lineWidth / 2, y - lineWidth / 2, width + lineWidth, height + lineWidth);
        gc.closePath();
    }

    @Override
    public MyRecWidthHeight getRecWidthHeight(String msg, double textFontSize) {
        Text text = new Text(msg);
        text.setFont(new Font(gc.getFont().getName(), textFontSize));
        Bounds bounds = text.getLayoutBounds();
        return new MyRecWidthHeight(bounds.getWidth(), bounds.getHeight());
    }


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
            Platform.runLater(()->{
                try {
                    // окно
                    fillRect2(windowBackColor, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
                    // сетка
                    __drawlines(arrayLines);
                    // отрисовка трендов
                    for (TrendPaintUnit trendPaintUnit : trendPaint) {
                        gc.beginPath();
                        gc.setStroke(trendPaintUnit.trendColor);
                        gc.setLineWidth(trendPaintUnit.trendWidth);
                        gc.strokePolyline(trendPaintUnit.x, trendPaintUnit.y, trendPaintUnit.x.length);
                        gc.closePath();
                    }
                    // top
                    fillRect2(fieldBackColor, fieldSizeLeft, 0, windowWidth, fieldSizeTop);
                    // left
                    fillRect2(fieldBackColor, 0, 0, fieldSizeLeft, height);
                    // right
                    fillRect2(fieldBackColor, width - fieldSizeRight, 0, fieldSizeRight, height);
                    // bottom
                    fillRect2(fieldBackColor, fieldSizeLeft, height - fieldSizeBottom, windowWidth, fieldSizeBottom);
                    // рамка
                    drawRect2(fieldFrameColor, fieldFrameWidth, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
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
    //
    private static class TrendPaintUnit {
        public double[] x;
        public double[] y;
        javafx.scene.paint.Color trendColor;
        double trendWidth;

        public TrendPaintUnit(double[] x, double[] y, javafx.scene.paint.Color trendColor, double trendWidth) {
            this.x = x;
            this.y = y;
            this.trendColor = trendColor;
            this.trendWidth = trendWidth;
        }
    }
    private static class TitleText {
        public double x;
        public double y;
        public javafx.scene.paint.Color color;
        public TextAlignment alignment;
        public String text;
        public double textFontSize;

        public TitleText(double x, double y, javafx.scene.paint.Color color, TextAlignment alignment, String text, double textFontSize) {
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
        public javafx.scene.paint.Color lineColor;
        public double lineWidth;

        public LinesParameters(LineParameters[] lines, javafx.scene.paint.Color lineColor, double lineWidth) {
            this.lines = lines;
            this.lineColor = lineColor;
            this.lineWidth = lineWidth;
        }
    }
    //
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
        for (int i = 0; i < xN && i < lines.length; i++) {
            x = (i * xStep + offsetS2) * kX + fieldSizeLeft;
            text = String.valueOf((double) ((i * xStep) + offsetCel) / 1_000);
            textRec = getRecWidthHeight(text, fieldFontSizeBottom);
            arrayTitleText.add(drawStringAlignment2(text, fieldFontColorBottom, fieldFontSizeBottom, x, positionBottom + textRec.height * 0.7, textRec, TrendPosition.center));
            lines[i] = new LineParameters(x, positionBottom - y1, x, positionBottom - y2);
        }
        arrayLines.add(new LinesParameters(lines, colorAwtToFx(netLineColor), netLineWidth));
    }
    private void __createLinesAndTitleY(ArrayList<TitleText> arrayTitleText, ArrayList<LinesParameters> arrayLines) {
        __createlinesY(arrayLines);
        __createTitlesY(arrayTitleText);
    }
    private void __createlinesY(ArrayList<LinesParameters> arrayLines) {
        if (trends[0] == null) return;
        if ((trends[0].netY_min % trends[0].netY_step) == 0) y_FistN = 1;
        else y_FistN = 0;
        //
        int step = trends[0].netY_step;
        double offset = trends[0].kY * (trends[0].netY_min % step);
        LineParameters[] lines = new LineParameters[y_netN - y_FistN];
        double x1 = fieldSizeLeft + netLineWidth / 2;
        double x2 = fieldSizeLeft + windowWidth - netLineWidth / 2;
        double y, yInv;
        for (int i = y_FistN, indx = 0; i < (y_netN); i++, indx++) {
            y = (i * step * trends[0].kY) - offset;
            yInv = (windowHeight + fieldSizeTop) - y;
            lines[indx] = new LineParameters(x1, yInv, x2, yInv);
        }
        arrayLines.add(new LinesParameters(lines, colorAwtToFx(netLineColor), netLineWidth));
    }
    private void __createTitlesY(ArrayList<TitleText> arrayTitleText) {
        double y, yZ;
        double x1;
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
                x1 = positionLeft - 5;
            } else {
                x1 = positionRight + 5;
            }
            String text;
            double textFontSize = trend.textFontSize;
            for (int j = 0; j < (baseN); j++) {
                yZ = (j + offsetC) * trend.netY_step;
                if (yZ > trend.netY_max) break;
                y = (j * step * trend.kY) - offset;
                text = (int) yZ + "" + trend.text;
                textRec = getRecWidthHeight(text, textFontSize);
                arrayTitleText.add(drawStringAlignment2(text, trend.textFontColor, textFontSize, x1, positionBottom - y, textRec, trend.positionFromWindow));
            }
        }
    }
    private void __drawlines(ArrayList<LinesParameters> arrayLines) {
        for (LinesParameters linesUnit : arrayLines) {
            gc.beginPath();
            gc.setStroke(linesUnit.lineColor);
            gc.setLineWidth(linesUnit.lineWidth);
            for (LineParameters line : linesUnit.lines) {
                gc.strokeLine(line.x1, line.y1, line.x2, line.y2);
            }
            gc.closePath();
        }
    }
    private void __drawTitles(ArrayList<TitleText> arrayTitleText) {
        for (TitleText titleText : arrayTitleText) {
            gc.beginPath();
            gc.setFont(new Font(gc.getFont().getName(), titleText.textFontSize));
            gc.setFill(titleText.color);
            gc.setTextAlign(titleText.alignment);
            gc.fillText(titleText.text, titleText.x, titleText.y);
            gc.closePath();
        }
    }
    private TrendPaintUnit __createPaintTrend(ArrayList<GraphDataUnit> graphData, Trend trend, double kY) {
        int graphData_size = graphData.size();
        //
        double[] x = new double[graphData_size];
        double[] y = new double[graphData_size];
        for (int i = 0; i < graphData_size; i++) {
            x[i] = (graphData.get(i).x - memX_begin) * kX + positionLeft;
            y[i] = positionBottom - renderY_zoom(kY, trend.netY_min, trend.netY_max, graphData.get(i).y);
        }
        return new TrendPaintUnit(x, y, colorAwtToFx(trend.lineColor), trend.lineWidth);
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
        return new TitleText(x2, y + textRec.height / 4, colorAwtToFx(textColor), textAlignment, text, textFontSize);
    }

    @Override
    protected void __clear() {
        ArrayList<TitleText> arrayTitleText = new ArrayList<>();
        ArrayList<LinesParameters> arrayLines = new ArrayList<>();
        __createLinesAndTitle(arrayTitleText, arrayLines);
        Platform.runLater(()->{
            try {
                // окно
                fillRect2(windowBackColor, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
                // сетка
                __drawlines(arrayLines);
                // top
                fillRect2(fieldBackColor, fieldSizeLeft, 0, windowWidth, fieldSizeTop);
                // left
                fillRect2(fieldBackColor, 0, 0, fieldSizeLeft, height);
                // right
                fillRect2(fieldBackColor, width - fieldSizeRight, 0, fieldSizeRight, height);
                // bottom
                fillRect2(fieldBackColor, fieldSizeLeft, height - fieldSizeBottom, windowWidth, fieldSizeBottom);
                // рамка
                drawRect2(fieldFrameColor, fieldFrameWidth, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
                // титлы
                __drawTitles(arrayTitleText);
            } catch (Exception exception) {
                System.out.println(flOnWork);
                exception.printStackTrace();
            }
        });
    }
}
