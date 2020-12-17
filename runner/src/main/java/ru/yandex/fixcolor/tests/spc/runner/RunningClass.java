package ru.yandex.fixcolor.tests.spc.runner;

import ru.yandex.fixcolor.tests.spc.allinterface.bd.DistClass;
import ru.yandex.fixcolor.tests.spc.bd.*;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.*;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Point;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;
import ru.yandex.fixcolor.tests.spc.runner.alarmmessage.AlarmMessage;
import ru.yandex.fixcolor.tests.spc.screen.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;

class RunningClass implements Runner {
    private MainFrame_interface mainFrame = null;
    private ru.yandex.fixcolor.tests.spc.lib.plot2.Plot plot = null;

    private BaseData bdSql = null;
    private ArrayList<DistClass>  distanceOut = null;
    private boolean distanceOutEnable = false;
    private int weight;
    private int tik_shelf;
    private int tik_back;
    private int tik_stop;
    private boolean reciveOn = false;
    private int n_cycle = 0;
    private boolean dist0Set = true;
    private int dist0 = 0;

    private int tik, tik0;
    private final CallBack callBack;

    PointK distance_pointK = new PointK();
    PointK weight_pointK = new PointK();

    public RunningClass(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void init(BaseData bdSql, MainFrame_interface mainFrame) {
        this.mainFrame = mainFrame;
        this.bdSql = bdSql;
        loadConfigK();
        distanceOut = new ArrayList<>();
        distanceOutEnable = false;

        Plot.Parameters plotParameters = new Plot.Parameters();
        // ************ ПОЛЯ ************
        // размер полей
        plotParameters.fieldSizeTop = 10;
        plotParameters.fieldSizeLeft = 70;
        plotParameters.fieldSizeRight = 70;
        plotParameters.fieldSizeBottom = 50;
        // цвет фона
        plotParameters.fieldBackColor = new Color(220, 220, 220);
        // цвет шрифта надписей по полям
        plotParameters.fieldFontColorTop = new Color(100, 100, 255);
        plotParameters.fieldFontColorBottom = new Color(80, 80, 80);
        // размер шрифта надписей по полям
        plotParameters.fieldFontSizeTop = 16;
        plotParameters.fieldFontSizeBottom = 16;
        // цвет рамки
        plotParameters.fieldFrameColor = new Color(120, 120, 120);
        // размер рамки
        plotParameters.fieldFrameWidth = 4;
        // ************ ОКНО ************
        // размер окна опреляется входным компонентом
        // цвет фона окна
        plotParameters.windowBackColor = new Color(255, 255, 255);
        // размер окна в мсек
        plotParameters.scaleZero_maxX = 2_500;
        // тип зумирования
        plotParameters.scaleZero_zoomX = Plot.ZOOM_X_SHRINK;
        // ************ СЕТКА ************
        // цвет линии сетки
        plotParameters.netLineColor = new Color(50, 50, 50);
        // толщина линии сетки
        plotParameters.netLineWidth = 1;
        // ************ ТРЕНД1  ************
        // позитция подписи тренда относительно окна
        plotParameters.trend1_positionFromWindow = Plot.TrendPosition.left;
        // попись условновной еденицы тренда
        plotParameters.trend1_text = "мм";
        // цвет шрифта подписи
        plotParameters.trend1_textFontColor = new Color(255, 0, 0);
        // размер шрифта подписи
        plotParameters.trend1_textFontSize = 16;
        // цвет линии тренда
        plotParameters.trend1_lineColor = new Color(255, 0, 0);
        // размер линии тренда
        plotParameters.trend1_lineWidth = 2;
        // начальное значение шкалы тренда
        plotParameters.trend1_zeroY_min = 0;
        // конечное значение шкалы тренда
        plotParameters.trend1_zeroY_max = 100;
        // режим автомасштабирования шкалы тренда
        plotParameters.trend1_AutoZoomY = Plot.ZOOM_Y_FROM_SCALE;
        // ************ ТРЕНД2  ************
        // позитция подписи тренда относительно окна
        plotParameters.trend2_positionFromWindow = Plot.TrendPosition.right;
        // попись условновной еденицы тренда
        plotParameters.trend2_text = "кг";
        // цвет шрифта подписи
        plotParameters.trend2_textFontColor = new Color(0, 200, 0);
        // размер шрифта подписи
        plotParameters.trend2_textFontSize = 16;
        // цвет линии тренда
        plotParameters.trend2_lineColor = new Color(0, 200, 0);
        // размер линии тренда
        plotParameters.trend2_lineWidth = 2;
        // начальное значение шкалы тренда
        plotParameters.trend2_zeroY_min = 0;
        // конечное значение шкалы тренда
        plotParameters.trend2_zeroY_max = 30;
        // режим автомасштабирования шкалы тренда
        plotParameters.trend2_AutoZoomY = Plot.ZOOM_Y_FROM_SCALE;

        plot = Plot.createFx(plotParameters, mainFrame.getCanvas());
        plot.clearScreen();

        fillFields();
        mainFrame.getLabelAlarm().setText("Нет связи с контроллером !");
    }

    @Override
    public void fillFields() {
        try {
            DataSpec dataSpec = bdSql.getLastDataSpec();
            Pusher pusher = bdSql.getPusher(dataSpec.id_pusher);
            MainFrame.mainFrame.setFieldsSamplePusher(pusher);
        } catch (BaseDataException e) {
            e.printStackTrace();
        }
    }

    int oldTypePack = 0;
    @Override
    public void reciveRsPush(byte[] bytes, int lenght) {
        int typePack = bytes[0] & 0x000000ff;
        tik = ((bytes[1] & 0x000000ff))
                + ((bytes[2] & 0x000000ff) <<  8 )
                + ((bytes[3] & 0x000000ff) << 16 )
                + ((bytes[4] & 0x000000ff) << 24 );

        //if (typePack != TypePack.CURENT_DATA) System.out.println(typePack);
        switch (typePack) {
            case TypePack.MANUAL_ALARM:
                mainFrame.outStatusWork("MANUAL_ALARM");
                reciveOn = false;
                oldTypePack = typePack;
                AlarmMessage alarmMessage = AlarmMessage.ALARM_CODE_NONE;
                alarmMessage.setAlarmCode(bytes[5] & 0x000000ff);
                mainFrame.getLabelAlarm().setText(alarmMessage.toString());
                break;
            case TypePack.MANUAL_BACK:
                mainFrame.outStatusWork("MANUAL_BACK");
                tik_back = tik;
                plot.setPointBackMove_time(tik_back);
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_STOP:
                reciveOn = false;
                distanceOutEnable = false;
                mainFrame.getLabelAlarm().setVisible(false);
                if (n_cycle > 0) {
                    if (distanceOut.size() < 2) {
                        mainFrame.outStatusWork("AUTO_STOP");
                        n_cycle = 0;
                        oldTypePack = typePack;
                        break;
                    }
                    n_cycle++;
                }
                mainFrame.outStatusWork("MANUAL_STOP");
//                System.out.println("count = " + distanceOut.size());
                //
                if (oldTypePack == TypePack.MANUAL_BACK || oldTypePack == TypePack.CYCLE_BACK) {
                    sendOutData();
                }
                n_cycle = 0;
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_FORWARD:
                mainFrame.outStatusWork("MANUAL_FORWARD");
                plot.setZommXzero();
                distanceOut.clear();
                dist0Set = true;

                plot.allDataClear();
                tik0 = tik;
                reciveOn = true;
                mainFrame.setFieldCurrentCycle(n_cycle + 1);
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_SHELF:
                mainFrame.outStatusWork("MANUAL_SHELF");
                tik_shelf = tik;
                plot.setPointBeginShelf_time(tik_shelf);
                oldTypePack = typePack;
                break;
            case TypePack.CYCLE_ALARM:
                mainFrame.outStatusWork("CYCLE_ALARM");
                oldTypePack = typePack;
                distanceOutEnable = false;
                break;
            case TypePack.CYCLE_BACK:
                mainFrame.outStatusWork("CYCLE_BACK");
                tik_back = tik;
                plot.setPointBackMove_time(tik_back);
                oldTypePack = typePack;
                break;
            case TypePack.CYCLE_DELAY:
                mainFrame.outStatusWork("CYCLE_DELAY");
                reciveOn = false;
                distanceOutEnable = false;
                n_cycle++;
                if (oldTypePack == TypePack.CYCLE_BACK) {
                    sendOutData();
                }
                distanceOut.clear();
                oldTypePack = typePack;
                break;
            case TypePack.CYCLE_FORWARD:
                mainFrame.outStatusWork("CYCLE_FORWARD");
                if (n_cycle == 0) plot.setZommXzero();
                dist0Set = true;
                distanceOut.clear();
                distanceOutEnable = true;

                plot.allDataClear();
                tik0 = tik;
                mainFrame.setFieldCurrentCycle(n_cycle + 1);
                reciveOn = true;
                oldTypePack = typePack;
                break;
            case TypePack.CYCLE_SHELF:
                mainFrame.outStatusWork("CYCLE_SHELF");
                tik_shelf = tik;
                plot.setPointBeginShelf_time(tik_shelf);
                oldTypePack = typePack;
                break;
            case TypePack.CURENT_DATA:
                if (reciveOn) {
                    {
                        int dist_in_adc = (bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8);
                        int dist_in = (int) Math.round(Point.renderValue(dist_in_adc, distance_pointK));
                        if (dist0Set) {
                            dist0Set = false;
                            dist0 = dist_in;
                        }
                        int dist = Math.abs(dist_in - dist0);
                        int weight_adc = (bytes[7 + 0] & 0xff) + ((bytes[7 + 1] & 0xff) << 8);
                        int weight = (int) Math.round(Point.renderValue(weight_adc, weight_pointK));
                        paintTrends((short) dist, (short) weight);
                        if (distanceOutEnable) {
                            distanceOut.add(new DistClass(tik, dist, weight));
                        }
                    }
                }
                break;
            case TypePack.FORCE:
                showWeight(bytes);
                break;
            case TypePack.CALIBR_DATA:
                break;
            case TypePack.RESET:
                oldTypePack = typePack;
                break;
            default:
                myLog.log(Level.WARNING, "Неизвестная команда:" + typePack);
        }
    }

    private void showWeight(byte[] bytes) {
        int weight_adc = (bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8);
        weight = (short) Math.round(Point.renderValue(weight_adc, weight_pointK));
        mainFrame.label2_txt(weight + "кг");
    }

    private void paintTrends(short dist, short ves) {
        int subTik;
        subTik = tik - tik0;
        plot.newData(subTik);
        plot.addTrend(dist);
        plot.addTrend(ves);
        plot.setData();
        plot.paint();
        plot.reFresh();

        if (subTik >= 20_000) {
            plot.allDataClear();
            tik0 = tik;
        }
    }

    @Override
    public void Suspended() {

    }

    @Override
    public void Close() {
//        plot.allDataClear();
//        plot.removeAllTrends();
//        plot.close();
        plot = null;
    }

    void sendOutData () {
        if (distanceOut.isEmpty()) return;
        // force
        int idxMid = distanceOut.size() / 2;
        int forceMeasure = distanceOut.get(idxMid).ves - weight;
        // move
        int moveMeasureBegin = distanceOut.get(0).distance;
        int moveMeasureEnd = 0;
        DistClass distClass;
        for (int i = 0; i < distanceOut.size(); i++) {
            distClass = distanceOut.get(i);
            if (distClass.tik == tik_shelf) moveMeasureEnd = distClass.distance;
        }
        int moveMeasure = Math.abs(moveMeasureBegin - moveMeasureEnd);
        //
        int timeUnClenching = Math.abs(distanceOut.get(0).tik - tik_shelf);
        // ****** out screen ******
        mainFrame.setFieldsMeasuredPusher(n_cycle, forceMeasure, moveMeasure, timeUnClenching);
        // ***** send stop *****
        int b = 0;
        try {
            b = Integer.parseInt(MainFrame.mainFrame.s_nCicle.getText());
        } catch (NumberFormatException e) {
            MyLogger.myLog.log(Level.SEVERE, "максимальное число итераций, ( установленно " + b + " )", e);
        }
        if (n_cycle >= b) {
            callBack.sendStopAutoMode();
        }
        // ***** out to bd *****
        try {
            tik_stop = distanceOut.get(distanceOut.size() - 1).tik;
//            System.out.println("count = " + distanceOut.size());
            bdSql.writeDataDist(n_cycle, weight, tik_shelf, tik_back, tik_stop,
                    forceMeasure, moveMeasure, timeUnClenching, new MyBlob(distanceOut));
        } catch (BaseDataException e) {
            MyLogger.myLog.log(Level.SEVERE, "ошибка сохранения данных", e);
        }

    }
    @Override
    public void loadConfigK() {
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        distance_pointK = new PointK(config.getDistance_k(), config.getDistance_offset());
        weight_pointK = new PointK(config.getWeight_k(), config.getWeight_offset());
    }
}
