package ru.yandex.fixcolor.tests.spc.lib.plot2;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.awt.*;

class PlotFx extends PlotParent { //implements Trend.TrendCallBack {
    private Canvas canvas;
    private GraphicsContext gc;
    public PlotFx(Plot.Parameters parameters, Canvas canvas) {
        super(parameters, canvas.getWidth(), canvas.getHeight());
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        // тренд1
        trends[0] = new Trend();
        // тренд2
        trends[1] = new Trend();
        // sets
        setParametersTrends(parameters);
    }

//    @Override
//    public void ll(TrendUnit[] units) {
//
//    }

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
        try {
            gc.beginPath();
            Trend trend = trends[nTrend];
            gc.setFill(colorAwtToFx(trend.textFontColor));
            javafx.scene.text.Font fnt = gc.getFont();
            gc.setFont(new javafx.scene.text.Font(trend.textFontSize));
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
                gc.setTextAlign(TextAlignment.RIGHT);
            } else {
                x1 = (int) (fieldSizeLeft + windowWidth + 5);
                gc.setTextAlign(TextAlignment.LEFT);
            }
            Bounds textRec;
            String text;
            for (int i = 0; i < (baseN); i++) {
                yZ = (i + offsetC) * trend.netY_step;
                if (yZ > trend.netY_max) break;
                y = (i * step * k) - offset;
                yInv = (windowHeight + fieldSizeTop) - y;
                text = String.valueOf((int) yZ) + "" + trend.text;
                final Text oText = new Text(text);
                oText.setFont(gc.getFont());
                textRec = oText.getLayoutBounds();
                textRec.getWidth();
                gc.fillText(text, x1, yInv + textRec.getHeight() / 4);
            }
        } finally {
            gc.closePath();
        }
    }

    @Override
    public void drawTitleX() {

    }
}
