package ru.yandex.fixcolor.tests.spc.lib.plot2;

import java.awt.*;
import java.util.ArrayList;

class PlotParent implements Plot {
    // размер холста
    protected double width;
    protected double height;
    //          поля
    // размер полей
    protected double fieldSizeTop;
    protected double fieldSizeLeft;
    protected double fieldSizeRight;
    protected double fieldSizeBottom;
    // цвет шрифта на полях
    protected Color fieldFontColorTop;
    protected Color fieldFontColorLeft;
    protected Color fieldFontColorRight;
    protected Color fieldFontColorBottom;
    // рамер шрифта на полях
    protected double fieldFontSizeTop;
    protected double fieldFontSizeLeft;
    protected double fieldFontSizeRight;
    protected double fieldFontSizeBottom;
    // цвет фона полей
    protected Color fieldBackColor;
    // цвет рамки
    protected Color fieldFrameColor;
    // ширина рамки
    protected double fieldFrameWidth;
    //          окно
    // цвет фона
    protected Color windowBackColor;
    // цвет линий сетки
    protected Color netLineColor;
    // ширина линий сетки
    protected double netLineWidth;
    // ==========================
    // тренды, всегда двое: учитель и ученик
    protected final Trend[] trends = new Trend[2];
    // конструктор
    protected PlotParent(Parameters parameters) {
        // размер холста
        width = parameters.width;
        height = parameters.height;
        //          поля
        // размер полей
        fieldSizeTop = parameters.fieldSizeTop;
        fieldSizeLeft = parameters.fieldSizeLeft;
        fieldSizeRight = parameters.fieldSizeRight;
        fieldSizeBottom = parameters.fieldSizeBottom;
        // цвет шрифта на полях
        fieldFontColorTop = parameters.fieldFontColorTop;
        fieldFontColorLeft = parameters.fieldFontColorLeft;
        fieldFontColorRight = parameters.fieldFontColorRight;
        fieldFontColorBottom = parameters.fieldFontColorBottom;
        // рамер шрифта на полях
        fieldFontSizeTop = parameters.fieldFontSizeTop;
        fieldFontSizeLeft = parameters.fieldFontSizeLeft;
        fieldFontSizeRight = parameters.fieldFontSizeRight;
        fieldFontSizeBottom = parameters.fieldFontSizeBottom;
        // цвет фона полей
        fieldBackColor = parameters.fieldBackColor;
        // цвет рамки
        fieldFrameColor = parameters.fieldFrameColor;
        // ширина рамки
        fieldFrameWidth = parameters.fieldFrameWidth;
        //          окно
        // цвет фона
        windowBackColor = parameters.windowBackColor;
        // цвет линий сетки
        netLineColor = parameters.netLineColor;
        // ширина линий сетки
        netLineWidth = parameters.netLineWidth;
        // ========================================
    }
    protected void setParametersTrends(Parameters parameters) {
        // ***** тренд1 *****
        // начальные значения миниму и максимума
        trends[0].zeroY_min = parameters.trend1_zeroY_min;
        trends[0].zeroY_max = parameters.trend1_zeroY_max;
        // толщина линии
        trends[0].lineWidth = parameters.trend1_lineWidth;
        // цвет линии
        trends[0].lineColor = parameters.trend1_lineColor;
        // размер шрифта для надписи
        trends[0].textFontSize = parameters.trend1_textFontSize;
        // цвет шрифта надписи
        trends[0].textFontColor = parameters.trend1_textFontColor;
        // текст надписи
        trends[0].text = parameters.trend1_text;
        // позитция надписи тренда относительно окна
        trends[0].positionFromWindow = parameters.trend1_positionFromWindow;
        // =============
        // ***** тренд2 *****
        // начальные значения миниму и максимума
        trends[1].zeroY_min = parameters.trend2_zeroY_min;
        trends[1].zeroY_max = parameters.trend2_zeroY_max;
        // толщина линии
        trends[1].lineWidth = parameters.trend2_lineWidth;
        // цвет линии
        trends[1].lineColor = parameters.trend2_lineColor;
        // размер шрифта для надписи
        trends[1].textFontSize = parameters.trend2_textFontSize;
        // цвет шрифта надписи
        trends[1].textFontColor = parameters.trend2_textFontColor;
        // текст надписи
        trends[1].text = parameters.trend2_text;
        // позитция надписи тренда относительно окна
        trends[1].positionFromWindow = parameters.trend2_positionFromWindow;
        // =============
    }
    // ==========================
    interface TrendCallBack {
        void ll(TrendUnit[] units);
    }
    class Trend {
        private TrendCallBack callBack;
        // начальные значения минимума и максимума
        private double zeroY_min;
        private double zeroY_max;
        // текущие значения минимума и максимума
        private double curnY_min;
        private double curnY_max;
        // толщина линии
        private double lineWidth;
        // цвет линии
        private Color lineColor;
        // размер шрифта надписи
        private double textFontSize;
        // цвет шрифта надписи
        private Color textFontColor;
        // текст надписи
        private String text;
        // позитция тренда относительно окна
        private int positionFromWindow;

        //
        private final ArrayList<TrendUnit> mass;

        public Trend(TrendCallBack callBack) {
            this.callBack = callBack;
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
    }
}
