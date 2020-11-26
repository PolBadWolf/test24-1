package ru.yandex.fixcolor.tests.spc.loader.calibration;

import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.lib.swing.CreateComponents;
import ru.yandex.fixcolor.tests.spc.loader.MainClass;
import ru.yandex.fixcolor.tests.spc.rs232.CommPort;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

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
    private boolean timerSend_On;
    private Thread timerSend_Thread;
    // =========================================
    private double distance_k;
    private double distance_offset;
    private double weight_k;
    private double weight_offset;
    // ---------------
    private JFrame frame;
    protected Calibration(CallBack callBack, CommPort commPort) {
        this.callBack = callBack;
        this.commPort = commPort;
        timerSend_On = false;
        // ---------------
        frame = CreateComponents.getFrame("Калибровка датчиков", 800, 600, false,
                null, new FrameClose());
        //
        //
        frame.pack();
        frame.setVisible(true);
        // ---------------
        try {
            MainClass.commPortOpen(commPort, MainClass.getPortNameFromConfig(), this::reciveRs);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // ---------------
        commPort.ReciveStart();
        // ---------------
        timerSend_Thread = new Thread(this::timerSend_run, "timer active calibration");
        timerSend_Thread.start();
        // ---------------
    }
    private class FrameClose extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            moduleClose();
            if (callBack != null) {
                callBack.messageClose();
            }
        }
    }
    private void moduleClose() {
        timerSend_On = false;
        while (timerSend_Thread.isAlive()) Thread.yield();
        commPort.ReciveStop();
        commPort.close();
        calibration = null;
    }
    private void timerSend_run() {
        timerSend_On = true;
        int t = 0;
        try {
            while (timerSend_On) {
                if (t <= 0) {
                    t = 100;
                    commPort.sendMessageCalibrationMode();
                } else {
                    Thread.sleep(10);
                    t--;
                }
            }
        } catch (Exception e) {
            timerSend_On = false;
            MyLogger.myLog.log(Level.SEVERE, "таймер активизации режима калибровки", e);
        }
    }
    void reciveRs(byte[] bytes, int lenght) {

    }
    // ===========
}
