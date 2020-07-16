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
    }

    @Override
    public void reciveRsPush(byte[] bytes, int lenght) {
        int b = bytes[0];

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
                debugN = 0;
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
                break;
            default:
        }
    }

    @Override
    public void Suspended() {

    }

    @Override
    public void Close() {

    }
}