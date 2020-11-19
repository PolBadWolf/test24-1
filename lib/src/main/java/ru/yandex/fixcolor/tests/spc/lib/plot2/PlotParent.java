package ru.yandex.fixcolor.tests.spc.lib.plot2;

import ru.yandex.fixcolor.tests.spc.lib.MyLogger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

public class PlotParent implements Plot, LocalInt {
    private CallBack callBack;
    // размер холста
    protected double width;
    protected double height;
    // размер окна
    protected double windowWidth;
    protected double windowHeight;
    // начало позитций
    protected double positionLeft;
    protected double positionRight;
    protected double positionTop;
    protected double positionBottom;
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
    // ====================================================
    protected double kX;
    // ====================================================
    protected int y_FistN;
    protected int y_netN;
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
    public CallBack getCallBack() {
        return callBack;
    }
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
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
        // позитции
        positionLeft = fieldSizeLeft;
        positionRight = width - fieldSizeRight;
        positionTop = fieldSizeTop;
        positionBottom = height - fieldSizeBottom;
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
        trends[0].autoZoomY = parameters.trend1_AutoZoomY;
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
        trends[1].autoZoomY = parameters.trend2_AutoZoomY;
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
    protected boolean flOnWork;
    protected Thread threadCycle;
    protected final BlockingQueue<DataQueue> paintQueue = new ArrayBlockingQueue<>(100);
    protected static final int command_Clear = 1;
    protected static final int command_Paint = 2;
    protected static final int command_ReFresh = 3;
    // ===
    protected static final DataQueue queueClear = new DataQueue(command_Clear, null);
    protected static final DataQueue queueReFresh = new DataQueue(command_ReFresh, null);
    // ==========================
    protected void doCicle(DataQueue dataQueue) {
        try {
            switch (dataQueue.command) {
                case command_Clear:
                    __clear();
                    break;
                case command_Paint:
                    __paint(dataQueue.datGraph);
                    break;
                case command_ReFresh:
                    __ReFresh();
                    break;
                default:
                    throw new Exception("Неизвестная команда");
            }
        } catch (Exception exception) {
            MyLogger.myLog.log(Level.SEVERE, "Ошибка цикла", exception);
        }
    }

    protected void __clear() { }
    protected void __ReFresh(){ }
    protected void __paint(GraphData[] datGraph) { }
    @Override
    public void fillRect(Color color, double x, double y, double width, double height) { }
    @Override
    public void drawRect(Color color, double lineWidth, double x, double y, double width, double height) { }

    @Override
    public void clear() {
        try {
            paintQueue.add(queueClear);
        } catch (IllegalStateException i) {
            //System.out.println("переполнение буфера команд: " + i.getMessage());
        }
    }

    // ====================

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
    @Override
    public void setData() {
        if (!flData) return;
        flData = false;
        try {
            timeUnits.add(new TimeUnit(newDataX));
            for (int i = 0; i < trends.length; i++) {
                trends[i].trendAddPoint(new TrendUnit(newDataTrends[i]));
            }
        } catch (Exception exception) {
            //exception.printStackTrace();
        }
    }

    // предварительный рачет и передача в очередь для отрисовки
    @Override
    public void paint() {
        if (timeUnits.isEmpty()) return;
        if (trends == null) return;
        // текущее крайнее положение memX_end
        if (zeroX_zoom == 2) {
            // shift
            // длина окна zeroX_max
            memX_begin = timeUnits.get(memX_beginIndx).ms;
            double lenghtSample = memX_end - zeroX_max;
            if (memX_begin < lenghtSample) {
                // поск позитции начала
                double tmp;
                int timeUnits_size = timeUnits.size();
                for (int i = memX_beginIndx; i < timeUnits_size; i++) {
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
        // === дроп и поиск минимума и максимума Y для каждого тренда
        ArrayList<Double> mX = new ArrayList<>();
        ArrayList<Double>[] mY = new ArrayList[trends.length];
        for (int i = 0; i < trends.length; i++) {
            mY[i] = new ArrayList<>();
        }
        double curMs;
        int pixOld = -1_000;
        int pixCur;
        double[] _netY_min = new double[trends.length];
        double[] _netY_max = new double[trends.length];
        double[] localY_zn = new double[trends.length];
        int[] localYrend = new int[trends.length];
        double _curY;
        for (int t = 0; t < trends.length; t++) {
            _netY_min[t] = trends[t].zeroY_min;
            _netY_max[t] = trends[t].zeroY_max;
            localY_zn[t] = trends[t].getValueFromMass(0);
            localYrend[t] = 0;
        }
        for (int i_ms = memX_beginIndx; i_ms < timeUnits.size(); i_ms++) {
            curMs = timeUnits.get(i_ms).ms;
            pixCur = (int) (curMs * kX);
            //
            for (int t = 0; t < trends.length; t++) {
                _curY = trends[t].getValueFromMass(i_ms);
                if (localYrend[t] == 0) {
                    if (_curY > localY_zn[t]) {
                        localYrend[t] = 1;
                    } else {
                        localYrend[t] = 2;
                    }
                    localY_zn[t] = _curY;
                }
                if (localYrend[t] == 1) {
                    if (localY_zn[t] < _curY) localY_zn[t] = _curY;
                } else {
                    if (localY_zn[t] > _curY) localY_zn[t] = _curY;
                }
            }
            //
            if (pixOld >= pixCur) {
                continue;
            }
            //
            pixOld = pixCur;
            mX.add(curMs);
            for (int t = 0; t < trends.length; t++) {
                if (_netY_min[t] > localY_zn[t]) _netY_min[t] = localY_zn[t];
                if (_netY_max[t] < localY_zn[t]) _netY_max[t] = localY_zn[t];
                mY[t].add(localY_zn[t]);
                localYrend[t] = 0;
            }
        }
        DataQueue dataQueue = new DataQueue(command_Paint, new GraphData[trends.length]);
        for (int t = 0; t < trends.length; t++) {
            trends[t].curnY_min = _netY_min[t];
            trends[t].curnY_max = _netY_max[t];
            dataQueue.datGraph[t] = new GraphData();
        }
        __zoomRender();
        //
        int mX_size = mX.size();
        for (int t = 0; t < trends.length; t++) {
            for (int i = 0; i < mX_size; i++) {
                dataQueue.datGraph[t].zn.add(new GraphDataUnit(mX.get(i), mY[t].get(i)));
            }
        }
        try {
            paintQueue.add(dataQueue);
            paintQueue.add(queueReFresh);
        } catch (IllegalStateException i) {
            //System.out.println("переполнение буфера команд: " + i.getMessage());
        }
    }

    protected void __zoomRender() {
        double t1_min, t1_max, t2_min, t2_max;
        if (trends[0].autoZoomY) {
            t1_min = Math.min(trends[0].curnY_min, trends[0].zeroY_min);
            t1_max = Math.max(trends[0].curnY_max, trends[0].zeroY_max);
        } else {
            t1_min = trends[0].zeroY_min;
            t1_max = trends[0].zeroY_max;
        }
        if (trends[1].autoZoomY) {
            t2_min = Math.min(trends[1].curnY_min, trends[1].zeroY_min);
            t2_max = Math.max(trends[1].curnY_max, trends[1].zeroY_max);
        } else {
            t2_min = trends[1].zeroY_min;
            t2_max = trends[1].zeroY_max;
        }
        // расчет минимум и максимум
        MultiplicityRender.Section sectionTr1 = MultiplicityRender.render.multiplicity(
                t1_min, t1_max
        );
        MultiplicityRender.Section sectionTr2 = MultiplicityRender.render.multiplicityT2(
                sectionTr1, t2_min, t2_max
        );
        trends[0].netY_min = sectionTr1.min;
        trends[0].netY_max = sectionTr1.max;
        trends[0].netY_step = sectionTr1.step;
        trends[0].kY = windowHeight / (trends[0].netY_max - trends[0].netY_min);
        //
        trends[1].netY_min = sectionTr2.min;
        trends[1].netY_max = sectionTr2.max;
        trends[1].netY_step = sectionTr2.step;
        trends[1].kY = windowHeight / (trends[1].netY_max - trends[1].netY_min);
        //
        y_netN = sectionTr1.n;
        double xLenght = memX_end - memX_begin;
        xStep = (MultiplicityRender.render.multiplicity(xLenght));
        xN = ((int) Math.ceil(xLenght / xStep));
        xCena = (double) xStep / 1_000;
        kX = windowWidth / (xN * xStep);
        //
    }
    protected void fistZoomRender() {
        for (Trend trend : trends) {
            trend.curnY_min = trend.zeroY_min;
            trend.curnY_max = trend.zeroY_max;
        }
        memX_begin = 0;
        memX_beginIndx = 0;
        memX_end = zeroX_max;
        __zoomRender();
    }

    @Override
    public void closeApp() {
        flOnWork = false;
        try {
            while (threadCycle.isAlive()) {
                Thread.yield();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Arrays.fill(trends, null);
        paintQueue.clear();
    }
    @Override
    public void reFresh() {
        try {
            paintQueue.add(queueReFresh);
        } catch (IllegalStateException i) {
            //System.out.println("переполнение буфера команд: " + i.getMessage());
        }
    }
    // ===========================================================================
    @Override
    public MyRecWidthHeight getRecWidthHeight(String text, double textFontSize) { return null; }

}
