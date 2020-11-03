package ru.yandex.fixcolor.tests.spc.lib.swing;

public class Scale {
    public static final int PRN_DPI = 360;
    public static final int SCR_DPI = 72;
    public static final float scaleUp = (float) PRN_DPI / (float) SCR_DPI;
    public static final float scaleDn = (float) SCR_DPI / (float) PRN_DPI;
    public static final float MM2UNIT_PRN = 1.0f / 25.4f * (float) PRN_DPI;
    public static final float MM2UNIT_SCR = 1.0f / 25.4f * (float) SCR_DPI;
    public static double minScale(double x0, double y0, double x1, double y1) {
        return Math.min(x0 / x1, y0 / y1);
    }
    // ==============
    private final float scale;

    public Scale(float scale) {
        this.scale = scale;
    }

    public float getFloat(float source) {
        return scale * source;
    }

    public float getFloat(double source) {
        return (float) (scale * source);
    }

    public float getFloat(int source) {
        return scale * source;
    }

    public int getInt(float source) {
        return (int) (scale * source);
    }

    public int getInt(double source) {
        return (int) (scale * source);
    }

    public int getInt(int source) {
        return (int) (scale * source);
    }

    public double getDouble(float source) {
        return scale * source;
    }

    public double getDouble(double source) {
        return (float) (scale * source);
    }

    public double getDouble(int source) {
        return scale * source;
    }
}
