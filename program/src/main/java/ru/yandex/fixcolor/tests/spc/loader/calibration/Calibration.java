package ru.yandex.fixcolor.tests.spc.loader.calibration;

import ru.yandex.fixcolor.library.converterdigit.ConvertDigit;
import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.bd.BaseDataException;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Point;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.PointK;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.lib.swing.CreateComponents;
import ru.yandex.fixcolor.tests.spc.lib.swing.MLabel;
import ru.yandex.fixcolor.tests.spc.loader.MainClass;
import ru.yandex.fixcolor.tests.spc.rs232.CommPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
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
    private int distance_adc;
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
    private MLabel distanceLabelCurrent;
    private MLabel distanceLabelPoint1;
    private JTextField distanceTextPoint1;
    private JButton distanceButtonPoint1;
    private MLabel distanceLabelPoint2;
    private JTextField distanceTextPoint2;
    private JButton distanceButtonPoint2;
    // ---------------
    protected Calibration(CallBack callBack, CommPort commPort) {
        Locale.setDefault(Locale.US);
        this.callBack = callBack;
        this.commPort = commPort;
        // ---------------
        readK_FromConfig();
        distance_point1 = renderPoint(100.0, distance_k, distance_offset);
        distance_point2 = renderPoint(1000.0, distance_k, distance_offset);
        weight_point1 = renderPoint(20.0, weight_k, weight_offset);
        weight_point2 = renderPoint(160.0, weight_k, weight_offset);
        // ---------------
        System.out.println("create calib");
        timerSend_On = false;
        // ---------------
        frame = CreateComponents.getFrame("Калибровка датчиков", 640, 480, false,
                null, new FrameClose());
        //
        initComponentsDistance(frame);
        frame.pack();
        frame.setVisible(true);
        // ---------------
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
    private void initComponentsDistance(Container parent) {
        CreateComponents.getLabel(parent, "Дистанция : ", new Font("Times New Roman", Font.PLAIN, 36),
                270, 5, true, true, MLabel.POS_RIGHT);
        distanceLabelCurrent = CreateComponents.getLabel(parent, "", new Font("Times New Roman", Font.PLAIN, 36),
                270, 5, true, true, MLabel.POS_LEFT);
        //
        CreateComponents.getLabel(parent, "точка 1 : АЦП ", new Font("Times New Roman", Font.PLAIN, 24),
                240, 60, true, true, MLabel.POS_RIGHT);
        distanceLabelPoint1 = CreateComponents.getLabel(parent, String.valueOf(distance_point1.adc), new Font("Times New Roman", Font.PLAIN, 24),
                250, 60, true, true, MLabel.POS_LEFT);
        distanceTextPoint1 = CreateComponents.getTextField(CreateComponents.TEXTFIELD,
                new Font("Times New Roman", Font.PLAIN, 24),
                320, 60, 120, 30,
                null, null,
                true, true);
        distanceTextPoint1.setText(String.format("%.3f", distance_point1.value));
        distanceButtonPoint1 = CreateComponents.getButton("Set", new Font("Times New Roman", Font.PLAIN, 16),
                500, 60, 80, 30,
                this::distanceSetPoint1, true, true);
        //
        parent.add(distanceTextPoint1);
        parent.add(distanceButtonPoint1);
        // ------------------
        CreateComponents.getLabel(parent, "точка 2 : АЦП ", new Font("Times New Roman", Font.PLAIN, 24),
                240, 100, true, true, MLabel.POS_RIGHT);
        distanceLabelPoint2 = CreateComponents.getLabel(parent, String.valueOf(distance_point2.adc), new Font("Times New Roman", Font.PLAIN, 24),
                250, 100, true, true, MLabel.POS_LEFT);
        distanceTextPoint2 = CreateComponents.getTextField(CreateComponents.TEXTFIELD,
                new Font("Times New Roman", Font.PLAIN, 24),
                320, 100, 120, 30,
                null, null,
                true, true);
        distanceTextPoint2.setText(String.format("%.3f", distance_point2.value));
        distanceButtonPoint2 = CreateComponents.getButton("Set", new Font("Times New Roman", Font.PLAIN, 16),
                500, 100, 80, 30,
                this::distanceSetPoint2, true, true);
        //
        parent.add(distanceTextPoint2);
        parent.add(distanceButtonPoint2);
    }

    private void distanceSetPoint1(ActionEvent actionEvent) {
        Point point = new Point();
        PointK pointK;
        try {
            point.value = Double.parseDouble(distanceTextPoint1.getText());
            point.adc = distance_adc;
            pointK = PointK.render(point, distance_point2);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "ошибка расчета калибровки", e);
            return;
        }
        distance_point1 = point;
        distance_k = pointK.k;
        distance_offset = pointK.offset;
        distanceLabelPoint1.setText(String.valueOf(distance_point1.adc));
        distanceCurrentShow();
    }

    private void distanceSetPoint2(ActionEvent actionEvent) {
        Point point = new Point();
        PointK pointK;
        try {
            point.value = Double.parseDouble(distanceTextPoint2.getText());
            point.adc = distance_adc;
            pointK = PointK.render(point, distance_point1);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "ошибка расчета калибровки", e);
            return;
        }
        distance_point2 = point;
        distance_k = pointK.k;
        distance_offset = pointK.offset;
        distanceLabelPoint2.setText(String.valueOf(distance_point2.adc));
        distanceCurrentShow();
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
    private void reciveRs(byte[] bytes, int lenght) {
        distance_adc = (int) ConvertDigit.bytes2int(bytes, 5, 2);
        int weight_adc = (int) ConvertDigit.bytes2int(bytes, 7, 2);
        distanceCurrentShow();
    }
    private void distanceCurrentShow() {
        double distance = Point.renderValue(distance_adc, distance_k, distance_offset);
        String str_distanceCur = String.valueOf(distance_adc) + " ==>  " + String.format("%.3f", distance);
        distanceLabelCurrent.setText(str_distanceCur);
    }
    // ===========
}
