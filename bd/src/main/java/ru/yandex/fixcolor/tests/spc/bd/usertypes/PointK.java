package ru.yandex.fixcolor.tests.spc.bd.usertypes;

public class PointK {
    public double k;
    public double offset;

    public PointK(double k, double offset) {
        this.k = k;
        this.offset = offset;
    }

    public static PointK render(Point point1, Point point2) {
        double k = (point1.value - point2.value) / (point1.adc - point2.adc);
        double offset = point1.value - (point1.adc * k);
        return new PointK(k, offset);
    }
}
