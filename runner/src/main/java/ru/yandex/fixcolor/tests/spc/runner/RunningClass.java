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
    private boolean distanceRecordEnable = false;
    private int weight;
    private int tik_shelf;
    private int tik_back;
    private int tik_stop;
    private boolean reciveOn = false;
    private boolean workManualOn = false;
    private boolean workCycleOn = false;
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
        distanceRecordEnable = false;

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

        int nCycleMax = getN_CycleMax();

        switch (typePack) {
            case TypePack.MANUAL_ALARM:
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // запрет приема измеренных данных
                reciveOn = false;
                // вывод сообщения кода ошибки
                outFrameError(bytes[5] & 0x000000ff);
                //
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_BACK:
                if (!workManualOn) return;
                if (oldTypePack != TypePack.MANUAL_FORWARD) { sequenceError(typePack); return; }
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // время штока назад
                tik_back = tik;
//                // вывод времени штока назад
//                plot.setPointBackMove_time(tik_back);
                //
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_STOP:
                // запрет приема измеренных данных
                reciveOn = false;
                // запрет записи измеренных данных
                distanceRecordEnable = false;
                // отключить сообщение об ошибке
                outFrameErrorOff();
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // сохранение записей в БД
                if (oldTypePack == TypePack.CYCLE_BACK) {
                    if (n_cycle > 0) { n_cycle++; }
                    if (distanceOut.size() > 2) { sendOutData(); }
                }
                // сброс счетчика циклов
                n_cycle = 0;
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_FORWARD:
                if (oldTypePack != TypePack.MANUAL_STOP) { sequenceError(typePack); return; }
                // режим ручной включен
                workManualOn = true;
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // сбросить зум по времени на начальное значение
                plot.setZommXzero();
                // установить нулевое положение штока
                dist0Set = true;
                // выключить запись данных измерений
                distanceRecordEnable = false;
                // очистить записи данных измерений
                distanceOut.clear();
                // очистка данных из графика
                plot.allDataClear();
                // нулевое значение временного штампа
                tik0 = tik;
                // включить данных замеров
                reciveOn = true;
                // номер цикла
                mainFrame.setFieldCurrentCycle(1);
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_SHELF:
                if (!workManualOn) return;
                if (oldTypePack != TypePack.MANUAL_FORWARD) { sequenceError(typePack); return; }
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // время начало полки
                tik_shelf = tik;
//                // вывод времени начало полки
//                plot.setPointBeginShelf_time(tik_shelf);
                oldTypePack = typePack;
                break;
//            case TypePack.CYCLE_ALARM:
//                //mainFrame.outStatusWork("CYCLE_ALARM");
//                oldTypePack = typePack;
//                distanceRecordEnable = false;
//                break;
            case TypePack.CYCLE_BACK:
                if (!workCycleOn) return;
                if (oldTypePack != TypePack.CYCLE_SHELF) { sequenceError(typePack); return; }
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // время штока назад
                tik_back = tik;
                // вывод времени штока назад
                plot.setPointBackMove_time(tik_back);
                oldTypePack = typePack;
                break;
            case TypePack.CYCLE_DELAY:
                if (!workCycleOn) return;
                if (oldTypePack != TypePack.CYCLE_BACK) { sequenceError(typePack); return; }
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // блокировка приема/отрисовки замеров
                reciveOn = false;
                // запрет накопления записей по замерам
                distanceRecordEnable = false;
                // номер цикла (начинается с нуля)
                n_cycle++;
                // отправка записей в БД
                if (oldTypePack == TypePack.CYCLE_BACK) { sendOutData(); }
                // очистка записей замеров
                distanceOut.clear();
                // проверка окончания цикла
                checkEndCycle(n_cycle, nCycleMax);
                oldTypePack = typePack;
                break;
            case TypePack.CYCLE_FORWARD:
                if (    oldTypePack != TypePack.MANUAL_STOP &&
                        oldTypePack != TypePack.CYCLE_DELAY ) { sequenceError(typePack); return; }
                // режим автоматического цикла
                workCycleOn =true;
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // при запуске цикла сбросить зум Х на дефолт
                if (n_cycle == 0) plot.setZommXzero();
                // установить нулевое положение штока
                dist0Set = true;
                // очистка массива для записи данных измерений
                distanceOut.clear();
                // включить запись данных измерений
                distanceRecordEnable = true;
                // очистка данных из графика
                plot.allDataClear();
                // нулевое значение временного штампа
                tik0 = tik;
                // включить данных замеров
                reciveOn = true;
                // вывод номера цикла
                mainFrame.setFieldCurrentCycle(n_cycle + 1);
                oldTypePack = typePack;
                break;
            case TypePack.CYCLE_SHELF:
                if (!workCycleOn) return;
                if (oldTypePack != TypePack.CYCLE_FORWARD) { sequenceError(typePack); return; }
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // время начало полки
                tik_shelf = tik;
                // вывод времени начало полки
                plot.setPointBeginShelf_time(tik_shelf);
                oldTypePack = typePack;
                break;
            case TypePack.CURENT_DATA:
                if (reciveOn) {
                    {
                        int dist_in_adc = getDistanceFromPack(bytes);
                        int dist_in = (int) Math.round(Point.renderValue(dist_in_adc, distance_pointK));
                        if (dist0Set) {
                            dist0Set = false;
                            dist0 = dist_in;
                        }
                        int dist = Math.abs(dist_in - dist0);
                        int weight_adc = getForceFromPack(bytes);
                        int weight = (int) Math.round(Point.renderValue(weight_adc, weight_pointK));
                        paintTrends((short) dist, (short) weight);
                        if (distanceRecordEnable) {
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

    private int getDistanceFromPack(byte[] bytes) {
        return (bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8);
    }

    private int getForceFromPack(byte[] bytes) {
        return (bytes[7 + 0] & 0xff) + ((bytes[7 + 1] & 0xff) << 8);
    }

    private void sequenceError(int typePack) {
        // вывод статуса программы
        outFrameStatus(TypePack.toString(typePack) + " - error");
        // сброс режимов работы
        workManualOn = false;
        workCycleOn = false;
        myLog.log(Level.WARNING, "ошибка последовательности режима: " + TypePack.toString(oldTypePack) + " -> " + TypePack.toString(typePack) );
        oldTypePack = TypePack.ERROR;
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

    // ==========================================
    private void outFrameError(int code) {
        AlarmMessage alarmMessage = AlarmMessage.ALARM_CODE_NONE;
        alarmMessage.setAlarmCode(code);
        mainFrame.getLabelAlarm().setText(alarmMessage.toString());
    }
    private void outFrameErrorOff() {
        mainFrame.getLabelAlarm().setVisible(false);
    }
    private void outFrameStatus(String text) {
        mainFrame.outStatusWork(text);
    }
    // ==========================================

    private int getN_CycleMax() {
        int cycle = 1;
        try {
            cycle = Integer.parseInt(MainFrame.mainFrame.s_nCicle.getText());
        } catch (NumberFormatException e) {
            MyLogger.myLog.log(Level.SEVERE, "максимальное число итераций, ( установленно " + cycle + " )", e);
        }
        return cycle;
    }
    // ----------------------------
    private void checkEndCycle(int nCycle, int nCycleMax) {
        // ***** send stop *****
        if (nCycle >= nCycleMax) {
            //callBack.sendStopNcycleMax(nCycleMax);
            callBack.sendMessageStop();
            reciveOn = false;
        }
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
        weight_pointK = new PointK(config.getForce_k(), config.getForce_offset());
    }
}
