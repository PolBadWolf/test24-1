package ru.yandex.fixcolor.tests.spc.lib.plot2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

class PlotFx extends PlotParent implements Trend.TrendCallBack {
    private Canvas canvas;
    private GraphicsContext gc;
    public PlotFx(Plot.Parameters parameters, Canvas canvas) {
        super(parameters, canvas.getWidth(), canvas.getHeight());
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        // тренд1
        trends[0] = new Trend(this);
        // тренд2
        trends[1] = new Trend(this);
        // sets
        setParametersTrends(parameters);
    }

    @Override
    public void ll(TrendUnit[] units) {

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
        gc.setFill(colorAwtToFx(color));
        gc.fillRect(x, y, width, height);
    }

    @Override
    public void drawRect(Color color, double lineWidth, double x, double y, double width, double height) {
        gc.setStroke(colorAwtToFx(color));
        gc.setLineWidth(lineWidth);
        gc.strokeRect(x, y, width, height);
    }

    @Override
    public void drawLines(Color lineColor, double lineWidth, LineParameters[] lines) {
        gc.beginPath();
        gc.setStroke(colorAwtToFx(lineColor));
        gc.setLineWidth(lineWidth);
        for (LineParameters line : lines) {
            gc.strokeLine(line.x1, line.y1, line.x2, line.y2);
        }
        gc.closePath();
    }

    @Override
    public void drawTitleY(int nTrend) {

    }
}
