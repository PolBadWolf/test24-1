package ru.yandex.fixcolor.tests.spc.bd.usertypes;

public class Point {
    public double value;
    public int adc;

    public Point(double value, int adc) {
        this.value = value;
        this.adc = adc;
    }

    public static double renderValue(int adc, PointK pointK) {
        return (adc * pointK.k) + pointK.offset;
    }
    public static double renderValue(int adc, double k, double offset) {
        return (adc * k) + offset;
    }

    public static int renderAdc(double value, PointK pointK) {
        return (int) Math.round((value - pointK.offset) / pointK.k);
    }
    public static int renderAdc(double value, double k, double offset) {
        return (int) Math.round((value - offset) / k);
    }
}
