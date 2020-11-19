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
            Platform.runLater(()->{
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);
            });
            DataQueue dataQueue;
            flOnWork = true;
            try {
                while (flOnWork) {
                    if ((dataQueue = paintQueue.poll(10, TimeUnit.MILLISECONDS)) != null) {
                        doCicle(dataQueue);
                        if (dataQueue.command == command_Paint) {
                            Thread.sleep(8);
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
    @Override
    public void fillRect(Color color, double x, double y, double width, double height) {
        Platform.runLater(()->{
            gc.beginPath();
            gc.setFill(colorAwtToFx(color));
            gc.fillRect(x, y, width, height);
            gc.closePath();
        });
    }

    @Override
    public void drawRect(Color color, double lineWidth, double x, double y, double width, double height) {
        Platform.runLater(()->{
            gc.beginPath();
            gc.setStroke(colorAwtToFx(color));
            gc.setLineWidth(lineWidth);
            gc.strokeRect(x - lineWidth / 2, y - lineWidth / 2, width + lineWidth, height + lineWidth);
            gc.closePath();
        });
    }

    @Override
    public void drawLines(Color lineColor, double lineWidth, LineParameters[] lines) {
        Platform.runLater(()->{
            gc.beginPath();
            gc.setStroke(colorAwtToFx(lineColor));
            gc.setLineWidth(lineWidth);
            for (LineParameters line : lines) {
                gc.strokeLine(line.x1, line.y1, line.x2, line.y2);
            }
            gc.closePath();
        });
    }

    @Override
    public void drawTrend(Trend trend, ArrayList<Double> ms, ArrayList<Double> yt) {
        double[] x = new double[ms.size()];
        double[] y = new double[x.length];
        double y0 = (int) (fieldSizeTop + windowHeight);
        double kY = windowHeight / (trend.netY_max - trend.netY_min);
        for (int i = 0; i < x.length; i++) {
            x[i] = (int) ((ms.get(i) - memX_begin) * kX + fieldSizeLeft);
            y[i] = y0 - (int) (yt.get(i) * kY);
        }
        Platform.runLater(() -> {
            gc.beginPath();
            gc.setStroke(colorAwtToFx(trend.lineColor));
            gc.setLineWidth(trend.lineWidth);
            gc.strokePolyline(x, y, x.length);
            gc.closePath();
        });
    }

    @Override
    public MyRecWidthHeight getRecWidthHeight(String msg, double textFontSize) {
        Text text = new Text(msg);
        text.setFont(new Font(gc.getFont().getName(), textFontSize));
        Bounds bounds = text.getLayoutBounds();
        return new MyRecWidthHeight(bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public void drawStringAlignment(String text, Color textColor, double textFontSize, double x, double y, MyRecWidthHeight textRec, int alignment) {
        float x2 = (float) x;
        Platform.runLater(()->{
            gc.beginPath();
            switch (alignment) {
                case TrendPosition.left:
                    gc.setTextAlign(TextAlignment.RIGHT);
                    break;
                case TrendPosition.center:
                    gc.setTextAlign(TextAlignment.CENTER);
                    break;
                case TrendPosition.right:
                    gc.setTextAlign(TextAlignment.LEFT);
                    break;
            }
            gc.setFont(new Font(gc.getFont().getName(), textFontSize));
            gc.setFill(colorAwtToFx(textColor));
            gc.fillText(text, x2, y + textRec.height / 4);
            gc.closePath();
        });
    }
    protected void __paint_trend(ArrayList<GraphDataUnit> graphData, Trend trend) {
        int graphData_size = graphData.size();
        //
        double[] x = new double[graphData_size];
        double[] y = new double[graphData_size];
        for (int i = 0; i < graphData_size; i++) {
            x[i] = (graphData.get(i).x - memX_begin) * kX + positionLeft;
            y[i] = positionBottom - (graphData.get(i).y * trend.kY);
        }
        Platform.runLater(()->{
            gc.beginPath();
            gc.setStroke(colorAwtToFx(trend.lineColor));
            gc.setLineWidth(trend.lineWidth);
            gc.strokePolyline(x, y, x.length);
            gc.closePath();
        });
    }
}
