package ru.yandex.fixcolor.tests.spc.runner;

import ru.yandex.fixcolor.tests.spc.allinterface.bd.DistClass;
import ru.yandex.fixcolor.tests.spc.bd.*;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.*;
import ru.yandex.fixcolor.tests.spc.rs232.CommPort;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.lib.fx.Plot;
import ru.yandex.fixcolor.tests.spc.screen.*;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.logging.Level;

class RunningClass implements Runner {
    private MainFrame_interface mainFrame = null;
    private Plot plot = null;

    private BaseData bdSql = null;
    private ArrayList<DistClass>  distanceOut = null;
    private int ves;
    private int tik_shelf;
    private int tik_back;
    private int tik_stop;
    private boolean reciveOn = false;
    private int n_cicle = 0;

    private int tik, tik0;

    public RunningClass() {
    }

    @Override
    public void init(BaseData bdSql, MainFrame_interface mainFrame) {
        this.mainFrame = mainFrame;
        this.bdSql = bdSql;

        distanceOut = new ArrayList<>();

        plot = new Plot(mainFrame.getCanvas(), 50, 50);

        plot.addTrend(Color.WHITE, 2);
        //plot.addTrend(Color.YELLOW, 2);

        plot.setFieldBackColor(Color.DARKGRAY);

        plot.setFieldFrameLineColor(Color.LIGHTGREEN);
        plot.setFieldFrameLineWidth(4.0);

        plot.setNetLineColor(Color.DARKGREEN);
        plot.setNetLineWidth(1.0);

        plot.clearScreen();

        plot.setZoomY(0, 1024);
        plot.setZoomYauto(false);

        plot.setZoomX(0, 5_000 / 5);
        plot.setZoomXlenghtAuto(true);
        plot.setZoomXbeginAuto(false);
        fillFields();
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

    @Override
    public void reciveRsPush(byte[] bytes, int lenght) {
        int b = bytes[0];
        tik = ((bytes[1] & 0x000000ff))
                + ((bytes[2] & 0x000000ff) <<  8 )
                + ((bytes[3] & 0x000000ff) << 16 )
                + ((bytes[4] & 0x000000ff) << 24 );
        int moveBegin, moveEnd, move, timeUnClenching;
        DistClass distClass;

        switch (b) {
            case TypePack.MANUAL_ALARM:
                mainFrame.outStatusWork("MANUAL_ALARM");
                reciveOn = false;
                break;
            case TypePack.MANUAL_BACK:
                mainFrame.outStatusWork("MANUAL_BACK");
                tik_back = tik;
                break;
            case TypePack.MANUAL_STOP:
                reciveOn = false;
                if (n_cicle > 0) n_cicle++;
                mainFrame.outStatusWork("MANUAL_STOP");
                System.out.println("count = " + distanceOut.size());
                //
                sendOutData();
                n_cicle = 0;
                break;
            case TypePack.MANUAL_FORWARD:
                mainFrame.outStatusWork("MANUAL_FORWARD");
                distanceOut.clear();

                plot.allDataClear();
                tik0 = tik;
                reciveOn = true;
                break;
            case TypePack.MANUAL_SHELF:
                mainFrame.outStatusWork("MANUAL_SHELF");
                tik_shelf = tik;
                break;
            case TypePack.CYCLE_ALARM:
                mainFrame.outStatusWork("CYCLE_ALARM");
                break;
            case TypePack.CYCLE_BACK:
                mainFrame.outStatusWork("CYCLE_BACK");
                tik_back = tik;
                break;
            case TypePack.CYCLE_DELAY:
                mainFrame.outStatusWork("CYCLE_DELAY");
                reciveOn = false;
                n_cicle++;
                System.out.println("count = " + distanceOut.size());
                //
                sendOutData();
                break;
            case TypePack.CYCLE_FORWARD:
                mainFrame.outStatusWork("CYCLE_FORWARD");
                distanceOut.clear();

                plot.allDataClear();
                tik0 = tik;
                reciveOn = true;
                break;
            case TypePack.CYCLE_SHELF:
                mainFrame.outStatusWork("CYCLE_SHELF");
                tik_shelf = tik;
                break;
            case TypePack.CURENT_DATA:
                if (reciveOn) {
                    paintTrends(bytes);
                    {
                        int dist = (bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8);
                        int ves = (bytes[7 + 0] & 0xff) + ((bytes[7 + 1] & 0xff) << 8);
                        distanceOut.add(new DistClass(tik, dist, ves));
                    }
                }
                break;
            case TypePack.VES:
                showVes(bytes);
                break;
            default:
        }
    }

    private void showVes(byte[] bytes) {
        ves  = (short) ((bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8));
        mainFrame.label2_txt(ves + "кг");
    }

    private void paintTrends(byte[] bytes) {
        short dist, ves;
        int x;
        dist = (short) ((bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8));
        //ves  = (short) ((bytes[5 + 2] & 0xff) + ((bytes[5 + 3] & 0xff) << 8));

        x = (short)((tik - tik0) / 5);
        plot.newDataX(x);
        plot.newDataTrend(0, dist);
        //plot.newDataTrend(1, ves);
        plot.newDataPush();
        plot.rePaint();

        if (x >= (3_600_000) / 5 ) {
            plot.allDataClear();
            tik0 = tik;
        }
    }

    @Override
    public void Suspended() {

    }

    @Override
    public void Close() {
        plot.allDataClear();
        plot.removeAllTrends();
        plot.close();
        plot = null;
    }

    void sendOutData () {
        // force
        int idxMid = distanceOut.size() / 2;
        int forceMeasure = distanceOut.get(idxMid).ves - ves;
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
        mainFrame.setFieldsMeasuredPusher(n_cicle, forceMeasure, moveMeasure, timeUnClenching);
        // ***** out to bd *****
        try {
            tik_stop = distanceOut.get(distanceOut.size() - 1).tik;
            System.out.println("count = " + distanceOut.size());
            bdSql.writeDataDist(n_cicle, ves, tik_shelf, tik_back, tik_stop,
                    forceMeasure, moveMeasure, timeUnClenching, new MyBlob(distanceOut));
        } catch (BaseDataException e) {
            MyLogger.myLog.log(Level.SEVERE, "ошибка сохранения данных", e);
        }

    }
}