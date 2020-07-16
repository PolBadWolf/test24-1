package org.example.test24.runner;

import javafx.scene.paint.Color;
import org.example.test24.allinterface.commPort.CommPort_Interface;
import org.example.test24.allinterface.screen.MainFrame_interface;
import ru.yandex.fixcolor.my_lib.graphics.Plot;

public class Running implements Runner_Impl {
    private CommPort_Interface commPort = null;
    private MainFrame_interface mainFrame = null;
    private Plot plot = null;

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

    }

    @Override
    public void Suspended() {

    }

    @Override
    public void Close() {

    }
}
