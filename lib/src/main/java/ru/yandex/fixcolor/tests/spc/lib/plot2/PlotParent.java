package ru.yandex.fixcolor.tests.spc.lib.plot2;

import java.awt.*;
import java.util.ArrayList;

public class PlotParent implements Plot, LocalInt {
    // размер холста
    protected double width;
    protected double height;
    // размер окна
    protected double windowWidth;
    protected double windowHeight;
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
    //
    protected int netY_n;
    // ширина рамки
    protected double fieldFrameWidth;
    //          окно
    // цвет фона
    protected Color windowBackColor;
    // цвет линий сетки
    protected Color netLineColor;
    protected Color netTextColor;
    protected double netTextSize;
    // ширина линий сетки
    protected double netLineWidth;
    protected int zeroX_zoom; // 0 - off, 1 - shrink, 2 - shift
    protected double zeroX_max;
    // значения минимума из прошлого цикла
    protected double memX_begin;
    protected int memX_beginIndx;
    protected double memX_end;
    protected double kX;
    // ==========================
    // тренды, всегда двое: учитель и ученик
    public final Trend[] trends = new Trend[2];
    // ===============================================
    protected ArrayList<TimeUnit> timeUnits = new ArrayList<>();
    protected double newDataX;
    protected int newDataIndx;
    protected double[] newDataTrends;
    protected int xStep;
    protected int xN;
    protected double xCena;
    // ===============================================
    // конструктор
    protected PlotParent(Parameters parameters, double paneWidth, double paneHeight) {
        // размер холста ( задается в основном конструкторе )
        width = paneWidth;
        height = paneHeight;
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
        // размер окна
        windowWidth = width - fieldSizeLeft - fieldSizeRight;
        windowHeight = height - fieldSizeTop - fieldSizeBottom;
        // цвет линий сетки
        netLineColor = parameters.netLineColor;
        netTextColor = parameters.netTextColor;
        netTextSize = parameters.netTextSize;
        // ширина линий сетки
        netLineWidth = parameters.netLineWidth;
        //
        zeroX_zoom = parameters.zeroX_zoom;
        zeroX_max = parameters.zeroX_max;
        // значения минимума из прошлого цикла
        memX_begin = 0;
        memX_beginIndx = 0;
        memX_end = zeroX_max;
        {
            double xLenght = memX_end - memX_begin;
            int xStep = (MultiplicityRender.render.multiplicity(xLenght));
            int xN = ((int) Math.ceil(xLenght / xStep));
            kX = windowWidth / (xN * xStep);
        }
        //
        newDataTrends = new double[2];
        // ========================================
    }
    protected void setParametersTrends(Parameters parameters) {
        // ***** тренд1 *****
        // начальные значения миниму и максимума
        trends[0].curnY_min = trends[0].zeroY_min = parameters.trend1_zeroY_min;
        trends[0].curnY_max = trends[0].zeroY_max = parameters.trend1_zeroY_max;
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
    @Override
    public void clear() {
        // top
        fillRect(fieldBackColor, fieldSizeLeft, 0, windowWidth, fieldSizeTop);
        // left
        fillRect(fieldBackColor, 0, 0, fieldSizeLeft, height);
        // right
        fillRect(fieldBackColor, width - fieldSizeRight, 0, fieldSizeRight, height);
        // bottom
        fillRect(fieldBackColor, fieldSizeLeft, height - fieldSizeBottom, windowWidth, fieldSizeBottom);
        // окно
        fillRect(windowBackColor, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
        // рамка
        drawRect(fieldFrameColor, fieldFrameWidth, fieldSizeLeft, fieldSizeTop, windowWidth, windowHeight);
        // расчет минимум и максимум
        MultiplicityRender.Section sectionTr1 = MultiplicityRender.render.multiplicity(
                Math.min(trends[0].curnY_min, trends[0].zeroY_min),
                Math.max(trends[0].curnY_max, trends[0].zeroY_max)
        );
        MultiplicityRender.Section sectionTr2 = MultiplicityRender.render.multiplicityT2(
                sectionTr1,
                Math.min(trends[1].curnY_min, trends[1].zeroY_min),
                Math.max(trends[1].curnY_max, trends[1].zeroY_max)
        );
        trends[0].netY_min = sectionTr1.fist;
        trends[0].netY_max = sectionTr1.end;
        trends[0].netY_step = sectionTr1.multiplicity;
        trends[1].netY_min = sectionTr2.fist;
        trends[1].netY_max = sectionTr2.end;
        trends[1].netY_step = sectionTr2.multiplicity;
        netY_n = sectionTr1.n;
        drawNetY();
        // ===========
    }

    // ====================
    @Override
    public void fillRect(Color color, double x, double y, double width, double height) { }
    @Override
    public void drawRect(Color color, double lineWidth, double x, double y, double width, double height) { }

    @Override
    public void drawNetY() {
        double k = windowHeight / (trends[0].netY_max - trends[0].netY_min);
        int fistN = 0;
        if ((trends[0].netY_min % trends[0].netY_step) == 0) fistN = 1;
        int baseN = netY_n;
        int step = trends[0].netY_step;
        double offset = k * (trends[0].netY_min % step);
        LineParameters[] lines = new LineParameters[baseN - fistN];
        double x1 = fieldSizeLeft + netLineWidth / 2;
        double x2 = fieldSizeLeft + windowWidth - netLineWidth / 2;
        double y, yInv;
        for (int i = fistN, indx = 0; i < (baseN); i++, indx++) {
            y = (i * step * k) - offset;
            yInv = (windowHeight + fieldSizeTop) - y;
            lines[indx] = new LineParameters(x1, yInv, x2, yInv);
        }
        drawLines(netLineColor, netLineWidth, lines);
        drawTitleY(0);
        drawTitleY(1);
        drawTitleX();
    }
boolean flData  = false;
    @Override
    public void newData(double ms) {
        newDataIndx = 0;
        newDataX = ms;
        if (memX_end < ms) {
            if (zeroX_zoom > 0) {
                memX_end = ms;
                flData = true;
            } else flData = false;
        } else
            flData = true;
    }

    @Override
    public void addTrend(double zn) {
        newDataTrends[newDataIndx] = zn;
        newDataIndx++;
    }
//Object objLock = new Object();
    @Override
    public void setData() {
        if (!flData) return;
        flData = false;
//        synchronized (objLock)
        {
            timeUnits.add(new TimeUnit(newDataX));
            for (int i = 0; i < trends.length; i++) {
                trends[i].trendAddPoint(new TrendUnit(newDataTrends[i]));
            }
        }
    }

    @Override
    public void paint() {
//        synchronized (objLock)
        {
            clear();
            if (timeUnits.isEmpty())
                return;
            // текущее крайнее положение memX_end
            if (zeroX_zoom == 2) { // shift
                // длина окна zeroX_max
                memX_begin = timeUnits.get(memX_beginIndx).ms;
                double lenghtSample = memX_end - zeroX_max;
                if (memX_begin < lenghtSample) {
                    // поск позитции начала
                    double tmp;
                    for (int i = memX_beginIndx; i < timeUnits.size(); i++) {
                        tmp = timeUnits.get(i).ms;
                        if (tmp < lenghtSample) continue;
                        memX_begin = tmp;
                        memX_beginIndx = i;
                        break;
                    }
                }
            } else {
                memX_begin = 0;
                memX_beginIndx = 0;
            }
            // === дроп
            ArrayList<Double> mX = new ArrayList<>();
            ArrayList<Double>[] mY = new ArrayList[trends.length];
            for (int i = 0; i < trends.length; i++) {
                mY[i] = new ArrayList<>();
            }
            double curMs;
            int pixOld = -1_000;
            int pixCur;
            for (int i_ms = memX_beginIndx; i_ms < timeUnits.size(); i_ms++) {
                curMs = timeUnits.get(i_ms).ms;
                pixCur = (int) (curMs * kX);
                if (pixOld >= pixCur) continue;
                pixOld = pixCur;
                mX.add(curMs);
                for (int t = 0; t < trends.length; t++) {
                    mY[t].add(trends[t].getValueFromMass(i_ms));
                }
            }

            for (int i = 0; i < trends.length; i++) {
                drawTrend(
                        trends[i],
                        mX,
                        mY[i]
                );
            }
            mX.clear();
            for (int i = 0; i < mY.length; i++) {
                mY[i].clear();
                mY[i] = null;
            }
        }
        reFresh();
    }

    // ===========================================================================
    @Override
    public void drawLines(Color lineColor, double lineWidth, LineParameters[] lines) { }
    @Override
    public void drawTitleY(int nTrend) { }
    @Override
    public void drawTitleX() { }
    @Override
    public void drawTrend(Trend trend, ArrayList<Double> ms, ArrayList<Double> y) { }
    @Override
    public void reFresh() { }
}
