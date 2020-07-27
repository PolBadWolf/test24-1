package org.example.test24.runner;

import javafx.scene.paint.Color;
import org.example.test24.allinterface.commPort.CommPort_Interface;
import org.example.test24.allinterface.screen.MainFrame_interface;
import ru.yandex.fixcolor.my_lib.graphics.Plot;

public class Running implements Runner_Impl {
    private CommPort_Interface commPort = null;
    private MainFrame_interface mainFrame = null;
    private Plot plot = null;

    private int debugN = 0;
    private int indexX = 0;
    private int tik, tik0;

    @Override
    public void init(CommPort_Interface commPort, MainFrame_interface mainFrame) {
        this.commPort = commPort;
        this.mainFrame = mainFrame;

        plot = new Plot(mainFrame.getCanvas(), 50, 50);

        plot.addTrend(Color.WHITE, 2);
        plot.addTrend(Color.YELLOW, 2);

        plot.setFieldBackColor(Color.DARKGRAY);

        plot.setFieldFrameLineColor(Color.LIGHTGREEN);
        plot.setFieldFrameLineWidth(4.0);

        plot.setNetLineColor(Color.DARKGREEN);
        plot.setNetLineWidth(1.0);

        plot.clearScreen();

        plot.setZoomY(0, 1024);
        plot.setZoomYauto(false);

        plot.setZoomX(0, 2000);
        plot.setZoomXlenghtAuto(false);
        plot.setZoomXbeginAuto(true);
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
                break;
            case TypePack.MANUAL_STOP:
                mainFrame.label1_txt("MANUAL_STOP");
                break;
            case TypePack.MANUAL_FORWARD:
                mainFrame.label1_txt("MANUAL_FORWARD");

                plot.allDataClear();
                indexX = 0;
                debugN = 0;
                tik0 = tik;
                break;
            case TypePack.MANUAL_SHELF:
                mainFrame.label1_txt("MANUAL_SHELF");
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
                break;
            default:
        }
    }

    private void paintTrends(byte[] bytes) {
        short dist, ves;
        int x;
        dist = (short) ((bytes[5 + 0] & 0xff) + ((bytes[5 + 1] & 0xff) << 8));
        ves  = (short) ((bytes[5 + 2] & 0xff) + ((bytes[5 + 3] & 0xff) << 8));

        //plot.newDataX(indexX);
        //x = indexX;
        x = (short)((tik - tik0) / 5);
        plot.newDataX(x);
        plot.newDataTrend(0, dist);
        plot.newDataTrend(1, ves);
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
