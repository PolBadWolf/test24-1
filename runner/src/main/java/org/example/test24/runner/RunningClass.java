package org.example.test24.runner;

import javafx.scene.paint.Color;
import org.example.test24.bd.BaseData;
import org.example.test24.bd.MyBlob;
import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.bd.DistClass;
import org.example.test24.allinterface.screen.MainFrame_interface;
import ru.yandex.fixcolor.my_lib.graphics.Plot;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class RunningClass implements Runner {
    private Consumer closer;
    private CommPort commPort = null;
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

    public RunningClass(Consumer closer) {
        this.closer = closer;
    }

    @Override
    public void init(BaseData bdSql, CommPort commPort, MainFrame_interface mainFrame) {
        this.commPort = commPort;
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
    }

    @Override
    public void reciveRsPush(byte[] bytes, int lenght) {
        int b = bytes[0];
        tik = ((bytes[1] & 0x000000ff))
                + ((bytes[2] & 0x000000ff) <<  8 )
                + ((bytes[3] & 0x000000ff) << 16 )
                + ((bytes[4] & 0x000000ff) << 24 );

        switch (b) {
            case TypePack.MANUAL_ALARM:
                mainFrame.label1_txt("MANUAL_ALARM");
                reciveOn = false;
                break;
            case TypePack.MANUAL_BACK:
                mainFrame.label1_txt("MANUAL_BACK");
                tik_back = tik;
                break;
            case TypePack.MANUAL_STOP:
                reciveOn = false;
                if (n_cicle > 0) n_cicle++;
                try {
                    tik_stop = distanceOut.get(distanceOut.size() - 1).tik;
                    mainFrame.label1_txt("MANUAL_STOP");
                    System.out.println("count = " + distanceOut.size());
                    bdSql.pushDataDist(new Date(), 0, n_cicle, ves, tik_shelf, tik_back, tik_stop, new MyBlob(distanceOut));
                } catch (java.lang.Throwable e) {
                    e = null;
                }
                n_cicle = 0;
            break;
            case TypePack.MANUAL_FORWARD:
                mainFrame.label1_txt("MANUAL_FORWARD");
                distanceOut.clear();

                plot.allDataClear();
                tik0 = tik;
                reciveOn = true;
                break;
            case TypePack.MANUAL_SHELF:
                mainFrame.label1_txt("MANUAL_SHELF");
                tik_shelf = tik;
                break;
            case TypePack.CYCLE_ALARM:
                mainFrame.label1_txt("CYCLE_ALARM");
                break;
            case TypePack.CYCLE_BACK:
                mainFrame.label1_txt("CYCLE_BACK");
                tik_back = tik;
                break;
            case TypePack.CYCLE_DELAY:
                mainFrame.label1_txt("CYCLE_DELAY");
                reciveOn = false;
                n_cicle++;
                try {
                    tik_stop = distanceOut.get(distanceOut.size() - 1).tik;
                    System.out.println("count = " + distanceOut.size());
                    bdSql.pushDataDist(new Date(), 0, n_cicle, ves, tik_shelf, tik_back, tik_stop, new MyBlob(distanceOut));
                } catch (java.lang.Throwable e) {
                    e = null;
                }
                break;
            case TypePack.CYCLE_FORWARD:
                mainFrame.label1_txt("CYCLE_FORWARD");
                distanceOut.clear();

                plot.allDataClear();
                tik0 = tik;
                reciveOn = true;
                break;
            case TypePack.CYCLE_SHELF:
                mainFrame.label1_txt("CYCLE_SHELF");
                tik_shelf = tik;
                break;
            case TypePack.CURENT_DATA:
                if (reciveOn) {
                    paintTrends(bytes);
                    int dist = (bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8);
                    distanceOut.add(new DistClass(tik, dist));
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

    }
}
