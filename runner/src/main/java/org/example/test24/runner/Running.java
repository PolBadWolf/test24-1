package org.example.test24.runner;

import javafx.scene.paint.Color;
import org.example.bd.BdWork;
import org.example.bd.MyBlob;
import org.example.test24.RS232.CommPort_Interface;
import org.example.test24.allinterface.bd.DistClass;
import org.example.test24.allinterface.screen.MainFrame_interface;
import ru.yandex.fixcolor.my_lib.graphics.Plot;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class Running implements Runner_Interface {
    private CommPort_Interface commPort = null;
    private MainFrame_interface mainFrame = null;
    private Plot plot = null;

    private BdWork bdWork = null;
    private ArrayList<DistClass>  distanceOut = null;
    private int ves;
    private int tik_shelf;
    private int tik_back;
    private int tik_stop;

    private int debugN = 0;
    private int indexX = 0;
    private int tik, tik0;

    @Override
    public void init(String selDataBase, CommPort_Interface commPort, MainFrame_interface mainFrame) {
        this.commPort = commPort;
        this.mainFrame = mainFrame;

        try {
            bdWork = new BdWork(selDataBase);
            bdWork.getConnect();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

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
        tik = ((bytes[1] & 0x000000ff) <<  0 )
                + ((bytes[2] & 0x000000ff) <<  8 )
                + ((bytes[3] & 0x000000ff) << 16 )
                + ((bytes[4] & 0x000000ff) << 24 );

        switch (b) {
            case TypePack.MANUAL_ALARM:
                mainFrame.label1_txt("MANUAL_ALARM");
                break;
            case TypePack.MANUAL_BACK:
                mainFrame.label1_txt("MANUAL_BACK");
                tik_back = tik;
                break;
            case TypePack.MANUAL_STOP:
                mainFrame.label1_txt("MANUAL_STOP");
                tik_stop = distanceOut.get(distanceOut.size() - 1).tik;
            {
                int tikSampl = distanceOut.get(0).tik;
                int tikCurr = 0;
                for (int i = 1; i < distanceOut.size(); i++) {
                    tikCurr = distanceOut.get(i).tik;
                    if ( (tikCurr - tikSampl) != 5) {
                        tikSampl = -1;
                    }
                    tikSampl = tikCurr;
                }
            }
                bdWork.pushDataDist(new Date(), 0, 0, ves, tik_shelf, tik_back, tik_stop, new MyBlob(distanceOut));
                break;
            case TypePack.MANUAL_FORWARD:
                mainFrame.label1_txt("MANUAL_FORWARD");
                distanceOut.clear();

                plot.allDataClear();
                indexX = 0;
                debugN = 0;
                tik0 = tik;
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
                break;
            case TypePack.CYCLE_DELAY:
                mainFrame.label1_txt("CYCLE_DELAY");
                break;
            case TypePack.CYCLE_FORWARD:
                mainFrame.label1_txt("CYCLE_FORWARD");
                break;
            case TypePack.CYCLE_SHELF:
                mainFrame.label1_txt("CYCLE_SHELF");
                break;
            case TypePack.CURENT_DATA:
                paintTrends(bytes);
                distanceOut.add(new DistClass(tik, (bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8)));
                break;
            case TypePack.VES:
                showVes(bytes);
                break;
            default:
        }
    }

    private void showVes(byte[] bytes) {
        ves  = (short) ((bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8));
        mainFrame.label2_txt(String.valueOf(ves + "кг"));
    }

    private void paintTrends(byte[] bytes) {
        short dist, ves;
        int x;
        dist = (short) ((bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8));
        //ves  = (short) ((bytes[5 + 2] & 0xff) + ((bytes[5 + 3] & 0xff) << 8));

        //plot.newDataX(indexX);
        //x = indexX;
        x = (short)((tik - tik0) / 5);
        plot.newDataX(x);
        plot.newDataTrend(0, dist);
        //plot.newDataTrend(1, ves);
        plot.newDataPush();
        plot.rePaint();

        indexX++;
        if (x >= (3_600_000) / 5 ) {
            plot.allDataClear();
            indexX = 0;
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
