package ru.yandex.fixcolor.tests.spc.loader.calibration;

import ru.yandex.fixcolor.library.converterdigit.ConvertDigit;
import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.bd.BaseDataException;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Point;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.PointK;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.lib.swing.CreateComponents;
import ru.yandex.fixcolor.tests.spc.loader.MainClass;
import ru.yandex.fixcolor.tests.spc.rs232.CommPort;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;

public class Calibration {
    public interface CallBack {
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
    private Point distance_point1;
    private Point distance_point2;
    private Point weight_point1;
    private Point weight_point2;
    // ---------------
    private JFrame frame;
    protected Calibration(CallBack callBack, CommPort commPort) {
        this.callBack = callBack;
        this.commPort = commPort;
        System.out.println("create calib");
        timerSend_On = false;
        // ---------------
        frame = CreateComponents.getFrame("Калибровка датчиков", 640, 480, false,
                null, new FrameClose());
        //
        //
        frame.pack();
        frame.setVisible(true);
        // ---------------
        readK_FromConfig();
        distance_point1 = renderPoint(100.0, distance_k, distance_offset);
        distance_point2 = renderPoint(1000.0, distance_k, distance_offset);
        weight_point1 = renderPoint(20.0, weight_k, weight_offset);
        weight_point2 = renderPoint(160.0, weight_k, weight_offset);
        // ---------------
        try {
            MainClass.commPortOpen(commPort, MainClass.getPortNameFromConfig(), this::reciveRs);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // ---------------
        System.out.println("start com port");
        commPort.ReciveStart();
        // ---------------
        timerSend_Thread = new Thread(this::timerSend_run, "timer active calibration");
        System.out.println("start timer");
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
    //
    private void readK_FromConfig() {
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        distance_k = config.getDistance_k();
        distance_offset = config.getDistance_offset();
        weight_k = config.getWeight_k();
        weight_offset = config.getWeight_offset();
    }
    private Point renderPoint(double value, double k, double offset) {
        return new Point(value, Point.renderAdc(value, k, offset));
    }
    //
    int zxzxzx = -1;
    void reciveRs(byte[] bytes, int lenght) {
        if (zxzxzx < 0) {
            zxzxzx = 0;
            System.out.println("fist recive from comm port");
        }
        int distance_adc = (int) ConvertDigit.bytes2int(bytes, 5, 2);
        int weight_adc = (int) ConvertDigit.bytes2int(bytes, 7, 2);
        double distance = Point.renderValue(distance_adc, distance_k, distance_offset);
        System.out.println(distance);
    }
    // ===========
}
