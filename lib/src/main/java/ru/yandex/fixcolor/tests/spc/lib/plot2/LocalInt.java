package ru.yandex.fixcolor.tests.spc.lib.plot2;

import java.awt.*;

interface LocalInt {
    void fillRect(Color color, double x, double y, double width, double height);
    void drawRect(Color color, double lineWidth, double x, double y, double width, double height);
    //
    void drawNetY();
    class LineParameters {
        public double x1;
        public double y1;
        public double x2;
        public double y2;

        public LineParameters(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
    void drawLines(Color lineColor, double lineWidth, LineParameters[] lines);
    void drawTitleY(int nTrend);
}
