package ru.yandex.fixcolor.tests.spc.loader.dialog;

import ru.yandex.fixcolor.tests.spc.lib.swing.CreateComponents;
import ru.yandex.fixcolor.tests.spc.loader.MainClass;
import ru.yandex.fixcolor.tests.spc.rs232.CommPort;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Calibration {
    interface CallBack {
        void messageClose();
    }
    private static Calibration calibration = null;
    public static Calibration init(CallBack callBack, CommPort commPort) {
        if (calibration == null)
            calibration = new Calibration(callBack, commPort);
        return calibration;
    }
    private CallBack callBack;
    // =========================================
    //               Comm Port
    private CommPort commPort;
    // =========================================
    // ---------------
    private JFrame frame;
    protected Calibration(CallBack callBack, CommPort commPort) {
        this.callBack = callBack;
        this.commPort = commPort;
        // ---------------
        frame = CreateComponents.getFrame("Калибровка датчиков", 800, 600, false,
                null, new FrameClose());
        //
        //
        frame.pack();
        frame.setVisible(true);
        // ---------------
        commPort.ReciveStop();
        try {
            MainClass.commPortOpen(commPort, MainClass.getPortNameFromConfig(), this::reciveRs);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // ---------------
        commPort.ReciveStart();
        // ---------------
    }
    private class FrameClose extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            if (callBack != null) {
                callBack.messageClose();
            }
            calibration = null;
        }
    }
    void reciveRs(byte[] bytes, int lenght) {

    }
}
