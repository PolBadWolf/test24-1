package ru.yandex.fixcolor.tests.spc.lib.plot2;

import java.awt.*;
import java.util.ArrayList;

public class Trend {
//    interface TrendCallBack {
//        void ll(TrendUnit[] units);
//    }
    // ---------------
//    private TrendCallBack callBack;
    // начальные значения минимума и максимума
    public double zeroY_min;
    public double zeroY_max;
    // значения минимума и максимума из данных тренда
    public double curnY_min;
    public double curnY_max;
    // расчетные значения минима, максимума и шаг сетки
    public int netY_min;
    public int netY_max;
    public int netY_step;
    // толщина линии
    public double lineWidth;
    // цвет линии
    public Color lineColor;
    // размер шрифта надписи
    public double textFontSize;
    // цвет шрифта надписи
    public Color textFontColor;
    // текст надписи
    public String text;
    // позитция тренда относительно окна
    public int positionFromWindow;

    //
    private final ArrayList<TrendUnit> mass;

//    public Trend(TrendCallBack callBack) {
//        this.callBack = callBack;
    public Trend() {
        mass = new ArrayList<>();
    }

    public void trendClear() {
        mass.clear();
    }

    public void trendAddPoint(TrendUnit unit) {
        mass.add(unit);
        if (curnY_min > unit.y) curnY_min = unit.y;
        if (curnY_max < unit.y) curnY_max = unit.y;
    }
    public double getValueFromMass(int indx) {
        return mass.get(indx).y;
    }
}
