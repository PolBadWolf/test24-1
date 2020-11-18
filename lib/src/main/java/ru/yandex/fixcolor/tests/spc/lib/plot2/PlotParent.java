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

    protected void __clear() {
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
        // сетка
        __drawNet();
    }
    protected void __ReFresh(){ }
    protected void __paint(GraphData[] datGraph) {
        for (int t = 0; t < datGraph.length; t++) {
            __paint_trend(datGraph[t].zn, trends[t]);
        }
    }
    protected void __paint_trend(ArrayList<GraphDataUnit> graphData, Trend trend) {

    }

    protected void __drawNet() {
        if (trends == null || trends.length < 2) return;
        __drawNetX();
        __drawNetY();
    }
    protected void __drawNetY() {
        __drawNetYlines();
        __drawNetYtitle();
    }
    protected void __drawNetX() {
        String text;
        MyRecWidthHeight textRec;
        double polNetLineWidth = netLineWidth / 2;
        double x, y1 = polNetLineWidth, y2 = windowHeight - polNetLineWidth;
        LineParameters[] lines = new LineParameters[xN];
        double offsetS2 = (xStep - (memX_begin % xStep)) % xStep;
        int offsetCel = ((int) Math.ceil(memX_begin / xStep))* xStep;
        for (int i = 0; i < xN; i++) {
            x = (i * xStep + offsetS2) * kX + fieldSizeLeft;
            text = String.valueOf((double) ((i * xStep) + offsetCel) / 1_000);
            textRec = getRecWidthHeight(text, netTextSize);
            drawStringAlignment(text, netTextColor, netTextSize, x, positionBottom + textRec.height * 0.7, textRec, TrendPosition.center);
            lines[i] = new LineParameters(x, positionBottom - y1, x, positionBottom - y2);
        }
        drawLines(netLineColor, netLineWidth, lines );
    }
    protected void __drawNetYlines() {
        if ((trends[0].netY_min % trends[0].netY_step) == 0) y_FistN = 1;
        else y_FistN = 0;
        //
        int step = trends[0].netY_step;
        double offset = trends[0].kY * (trends[0].netY_min % step);
        LineParameters[] lines = new LineParameters[y_netN - y_FistN];
        double x1 = fieldSizeLeft + netLineWidth / 2;
        double x2 = fieldSizeLeft + windowWidth - netLineWidth / 2;
        double y, yInv;
        for (int i = y_FistN, indx = 0; i < (y_netN); i++, indx++) {
            y = (i * step * trends[0].kY) - offset;
            yInv = (windowHeight + fieldSizeTop) - y;
            lines[indx] = new LineParameters(x1, yInv, x2, yInv);
        }
        drawLines(netLineColor, netLineWidth, lines);
    }
    protected void __drawNetYtitle() {
        double y, yZ;
        double x1;
        MyRecWidthHeight textRec;
        for (int i = 0; i < 2; i++) {
            Trend trend = trends[i];
            int baseN = y_netN;
            int step = trend.netY_step;
            double offset = trend.kY * (trend.netY_min % step);
            int offsetC = trend.netY_min / step;
            //
            if (trend.positionFromWindow == TrendPosition.left) {
                x1 = positionLeft - 5;
            } else {
                x1 = positionRight + 5;
            }
            String text;
            double textFontSize = trend.textFontSize;
            for (int j = 0; j < (baseN); j++) {
                yZ = (j + offsetC) * trend.netY_step;
                if (yZ > trend.netY_max) break;
                y = (j * step * trend.kY) - offset;
                text = (int) yZ + "" + trend.text;
                textRec = getRecWidthHeight(text, textFontSize);
                drawStringAlignment(text, trend.textFontColor, textFontSize, x1, positionBottom - y, textRec, trend.positionFromWindow);
            }
        }
    }

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
        trends[0].netY_min = sectionTr1.min;
        trends[0].netY_max = sectionTr1.max;
        trends[0].netY_step = sectionTr1.step;
        trends[1].netY_min = sectionTr2.min;
        trends[1].netY_max = sectionTr2.max;
        trends[1].netY_step = sectionTr2.step;
        y_netN = sectionTr1.n;
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
        int baseN = y_netN;
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

    @Override
    public void paint() {
        clear();
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
        double _curY;
        for (int i = 0; i < trends.length; i++) {
            _netY_min[i] = trends[i].zeroY_min;
            _netY_max[i] = trends[i].zeroY_max;
        }
        for (int i_ms = memX_beginIndx; i_ms < timeUnits.size(); i_ms++) {
            curMs = timeUnits.get(i_ms).ms;
            pixCur = (int) (curMs * kX);
            if (pixOld >= pixCur) continue;
            pixOld = pixCur;
            mX.add(curMs);
            for (int t = 0; t < trends.length; t++) {
                _curY = trends[t].getValueFromMass(i_ms);
                if (_netY_min[t] > _curY) _netY_min[t] = _curY;
                if (_netY_max[t] < _curY) _netY_max[t] = _curY;
                mY[t].add(_curY);
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

        }
        //
//        for (int i = 0; i < trends.length; i++) {
//            drawTrend(
//                    trends[i],
//                    mX,
//                    mY[i]
//            );
//        }
//        mX.clear();
//        for (int i = 0; i < mY.length; i++) {
//            mY[i].clear();
//            mY[i] = null;
//        }
    }

    protected void __zoomRender() {
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
        }catch (IllegalStateException i) {

        }
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
    public MyRecWidthHeight getRecWidthHeight(String text, double textFontSize) { return null; }

    @Override
    public void drawStringAlignment(String text, Color textColor, double textFontSize, double x, double y, MyRecWidthHeight recWidthHeight, int alignment) { }
}
