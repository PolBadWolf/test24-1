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
    private int force0 = 0;

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
                if (oldTypePack != TypePack.MANUAL_SHELF) {
                    sequenceError(typePack);
                    return;
                }
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // время штока назад
                tik_back = tik;
//                // вывод времени штока назад
                plot.setPointBackMove_time(tik_back);
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
                //
                if (oldTypePack == TypePack.CYCLE_BACK
                ||  oldTypePack == TypePack.MANUAL_BACK) {
                    tik_stop = tik;
                    // вывод времени
                    plot.setPointStopBack_time(tik_stop);
                }
                // сохранение записей в БД
                if (oldTypePack == TypePack.CYCLE_BACK) {
                    if (n_cycle > 0) { n_cycle++; }
                    if (distanceOut.size() > 2) { sendOutData(); }
                }
                 // расчет времени подъема
                if (oldTypePack == TypePack.MANUAL_BACK) {
                    int c_cur_i =1;
                    int c_cur_dist = distanceOut.get(c_cur_i).distance;
                    int c_cur_begin =0, c_cur_end = 0;
                    int c_cur_dalay = 0;
                    boolean c_cur_find = false;
                    // поиск начала
                    c_cur_dalay = 0;
                    for (;c_cur_i < distanceOut.size();c_cur_i++) {
                        if (distanceOut.get(c_cur_i).distance - c_cur_dist > 2) {
                            c_cur_dalay++;
                            if (c_cur_dalay > 10) {
                                c_cur_begin = c_cur_i - 10;
                                c_cur_find = true;
                                break;
                            }
                        } else {
                            c_cur_dalay = 0;
                        }
                    }
                    // поиск конца
                    if (c_cur_find) {
                        c_cur_find = false;
                        c_cur_dalay = 0;
                        for (;c_cur_i < distanceOut.size();c_cur_i++) {
                            int cur_d = distanceOut.get(c_cur_i).distance;
                            if (cur_d - c_cur_dist > 1) {
                                c_cur_dist = cur_d;
                                c_cur_dalay = 0;
                            } else {
                                c_cur_dalay++;
                                if (c_cur_dalay > 10) {
                                    c_cur_end = c_cur_i - 10;
                                    c_cur_find = true;
                                    break;
                                }
                            }
                        }
                    }
                    // индикация
                    String c_cur_string;
                    if (c_cur_find) {
                        int sub = distanceOut.get(c_cur_end).tik - distanceOut.get(c_cur_begin).tik;
                        c_cur_string = String.valueOf(sub);
                    } else c_cur_string = "==error==";
                    MainFrame.mainFrame.setT_imp_up(c_cur_string);
                }

                // сброс счетчика циклов
                n_cycle = 0;
                oldTypePack = typePack;
                break;
            case TypePack.MANUAL_FORWARD:
                if (oldTypePack != TypePack.MANUAL_STOP) {
                    sequenceError(typePack);
                    return;
                }
                // очистка таймингов
                tik_shelf = -999;
                tik_back = -999;
                tik_stop = -999;
                // режим ручной включен
                workManualOn = true;
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // сбросить зум по времени на начальное значение
                plot.setZommXzero();
                // установить нулевое положение штока
                dist0Set = true;
                // выключить запись данных измерений
                distanceRecordEnable = true; //false;
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
                if (oldTypePack != TypePack.MANUAL_FORWARD) {
                    sequenceError(typePack);
                    return;
                }
                // вывод статуса программы
                outFrameStatus(TypePack.toString(typePack));
                // время начало полки
                tik_shelf = tik;
                // вывод времени начало полки
                plot.setPointBeginShelf_time(tik_shelf);
                oldTypePack = typePack;
                break;
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
                        // дистанция
                        int dist_in_adc = getDistanceFromPack(bytes);
                        int dist_in = (int) Math.round(Point.renderValue(dist_in_adc, distance_pointK));
                        // усилие
                        int weight_adc = getForceFromPack(bytes);
                        int weight_in = (int) Math.round(Point.renderValue(weight_adc, weight_pointK));
                        // нулевое положение
                        if (dist0Set) {
                            dist0Set = false;
                            dist0 = dist_in;
                            force0 = 0;//weight_in;
                        }
                        // нормирование
                        int dist = dist_in - dist0;
                        int weight = weight_in - force0;
                        if (dist < 0) dist = 0;
                        if (weight < 0) weight = 0;
                        // отрисовка
                        paintTrends((short) dist, (short) weight);
                        // сохранение
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
        myLog.log(Level.WARNING, "ошибка последовательности режима: " + TypePack.toString(oldTypePack) + " -> " + TypePack.toString(typePack), new Exception("ошибка последовательности режима") );
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
        // нормирование
        int tik_max = distanceOut.get(distanceOut.size() - 1).tik;
        if (tik_stop > tik_max) tik_stop = tik_max;
        if (tik_stop < tik_shelf) tik_stop = tik_max;
        // force
        int forceMeasure = -1;
        // move
        int moveMeasureBegin = distanceOut.get(0).distance;
        int moveMeasure = -1;
        DistClass distClass;
        for (int i = 0; i < distanceOut.size(); i++) {
            distClass = distanceOut.get(i);
            if (distClass.tik >= tik_back) {
                moveMeasure = distClass.distance - moveMeasureBegin;
                forceMeasure = distClass.ves - weight;
                break;
            }
        }
        // ===========================
        //
        float timeUnClenching = (float) (tik_shelf - distanceOut.get(0).tik) / 1_000;
        float timeClenching = (float) (tik_stop - tik_back) / 1_000;
        // ****** out screen ******
        mainFrame.setFieldsMeasuredPusher(n_cycle, forceMeasure, moveMeasure, timeUnClenching, timeClenching);
        // ***** out to bd *****
        try {
            bdSql.writeDataDist(n_cycle, weight, tik_shelf, tik_back, tik_stop,
                    forceMeasure, moveMeasure, timeUnClenching, timeClenching, new MyBlob(distanceOut));
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
