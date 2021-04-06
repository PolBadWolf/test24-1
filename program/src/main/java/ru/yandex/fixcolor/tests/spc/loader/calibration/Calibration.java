package ru.yandex.fixcolor.tests.spc.loader.calibration;

import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.bd.BaseDataException;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Point;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.PointK;
import ru.yandex.fixcolor.tests.spc.lib.Converts;
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
    private int weight_adc;
    private Point distance_point1;
    private Point distance_point2;
    private PointK distance_pointK;
    private Point force_point1;
    private Point force_point2;
    private PointK force_pointK;
    // ---------------
    private JFrame frame;
    // ---------------
    private MLabel distanceLabelCurrent;
    private MLabel distanceLabelPoint1;
    private JTextField distanceTextPoint1;
    private JButton distanceButtonPoint1;
    private MLabel distanceLabelPoint2;
    private JTextField distanceTextPoint2;
    private JButton distanceButtonPoint2;
    // ---------------
    private MLabel weightLabelCurrent;
    private MLabel weightLabelPoint1;
    private JTextField weightTextPoint1;
    private JButton weightButtonPoint1;
    private MLabel weightLabelPoint2;
    private JTextField weightTextPoint2;
    private JButton weightButtonPoint2;
    // ---------------
    private JButton configButtonSave;
    private MLabel  labelFlashSave;
    // ---------------
    protected Calibration(CallBack callBack, CommPort commPort) {
        Locale.setDefault(Locale.US);
        this.callBack = callBack;
        this.commPort = commPort;
        // ---------------
        readK_FromConfig();
        distance_pointK = PointK.render(distance_point1, distance_point2);
        force_pointK = PointK.render(force_point1, force_point2);
        // ---------------
        //System.out.println("create calib");
        timerSend_On = false;
        // ---------------
        frame = CreateComponents.getFrame("Калибровка датчиков", 640, 480, false,
                null, new FrameClose());
        //
        initComponentsDistance(frame);
        initComponentsWeight(frame);
        configButtonSave = CreateComponents.getButton(frame, "Сохранить", new Font("Times New Roman", Font.PLAIN, 16),
                320, 370, 120, 30, this::configSave, true, true);
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
        //System.out.println("start com port");
        commPort.ReciveStart();
        // ---------------
        timerSend_Thread = new Thread(this::timerSend_run, "timer active calibration");
        //System.out.println("start timer");
        timerSend_Thread.start();
        // ---------------
    }

    private void initComponentsDistance(Container parent) {
        CreateComponents.getLabel(parent, "Дистанция : ", new Font("Times New Roman", Font.PLAIN, 36),
                320, 5, true, true, MLabel.POS_CENTER);
        CreateComponents.getLabel(parent, "Текущие данные : ", new Font("Times New Roman", Font.PLAIN, 24),
                270, 55, true, true, MLabel.POS_RIGHT);
        distanceLabelCurrent = CreateComponents.getLabel(parent, "", new Font("Times New Roman", Font.PLAIN, 24),
                270, 55, true, true, MLabel.POS_LEFT);
        //
        CreateComponents.getLabel(parent, "точка 1 : АЦП ", new Font("Times New Roman", Font.PLAIN, 24),
                240, 90, true, true, MLabel.POS_RIGHT);
        distanceLabelPoint1 = CreateComponents.getLabel(parent, String.valueOf(distance_point1.adc), new Font("Times New Roman", Font.PLAIN, 24),
                250, 90, true, true, MLabel.POS_LEFT);
        distanceTextPoint1 = CreateComponents.getTextField(CreateComponents.TEXTFIELD,
                new Font("Times New Roman", Font.PLAIN, 24),
                320, 90, 120, 30,
                null, null,
                true, true);
        distanceTextPoint1.setText(String.format("%.3f", distance_point1.value));
        distanceButtonPoint1 = CreateComponents.getButton(parent, "Задать", new Font("Times New Roman", Font.PLAIN, 16),
                500, 90, 80, 30,
                this::distanceSetPoint1, true, true);
        //
        parent.add(distanceTextPoint1);
//        parent.add(distanceButtonPoint1);
        // ------------------
        CreateComponents.getLabel(parent, "точка 2 : АЦП ", new Font("Times New Roman", Font.PLAIN, 24),
                240, 130, true, true, MLabel.POS_RIGHT);
        distanceLabelPoint2 = CreateComponents.getLabel(parent, String.valueOf(distance_point2.adc), new Font("Times New Roman", Font.PLAIN, 24),
                250, 130, true, true, MLabel.POS_LEFT);
        distanceTextPoint2 = CreateComponents.getTextField(CreateComponents.TEXTFIELD,
                new Font("Times New Roman", Font.PLAIN, 24),
                320, 130, 120, 30,
                null, null,
                true, true);
        distanceTextPoint2.setText(String.format("%.3f", distance_point2.value));
        distanceButtonPoint2 = CreateComponents.getButton(parent, "Задать", new Font("Times New Roman", Font.PLAIN, 16),
                500, 130, 80, 30,
                this::distanceSetPoint2, true, true);
        //
        parent.add(distanceTextPoint2);
//        parent.add(distanceButtonPoint2);
    }
    private void initComponentsWeight(Container parent) {
        CreateComponents.getLabel(parent, "Вес : ", new Font("Times New Roman", Font.PLAIN, 36),
                320, 190, true, true, MLabel.POS_CENTER);
        CreateComponents.getLabel(parent, "Текущие данные : ", new Font("Times New Roman", Font.PLAIN, 24),
                270, 240, true, true, MLabel.POS_RIGHT);
        weightLabelCurrent = CreateComponents.getLabel(parent, "", new Font("Times New Roman", Font.PLAIN, 24),
                270, 240, true, true, MLabel.POS_LEFT);
        //
        CreateComponents.getLabel(parent, "точка 1 : АЦП ", new Font("Times New Roman", Font.PLAIN, 24),
                240, 275, true, true, MLabel.POS_RIGHT);
        weightLabelPoint1 = CreateComponents.getLabel(parent, String.valueOf(force_point1.adc), new Font("Times New Roman", Font.PLAIN, 24),
                250, 275, true, true, MLabel.POS_LEFT);
        weightTextPoint1 = CreateComponents.getTextField(CreateComponents.TEXTFIELD,
                new Font("Times New Roman", Font.PLAIN, 24),
                320, 275, 120, 30,
                null, null,
                true, true);
        weightTextPoint1.setText(String.format("%.3f", force_point1.value));
        weightButtonPoint1 = CreateComponents.getButton(parent, "Задать", new Font("Times New Roman", Font.PLAIN, 16),
                500, 275, 80, 30,
                this::weightSetPoint1, true, true);
        //
        parent.add(weightTextPoint1);
//        parent.add(weightButtonPoint1);
        // ------------------
        CreateComponents.getLabel(parent, "точка 2 : АЦП ", new Font("Times New Roman", Font.PLAIN, 24),
                240, 315, true, true, MLabel.POS_RIGHT);
        weightLabelPoint2 = CreateComponents.getLabel(parent, String.valueOf(force_point2.adc), new Font("Times New Roman", Font.PLAIN, 24),
                250, 315, true, true, MLabel.POS_LEFT);
        weightTextPoint2 = CreateComponents.getTextField(CreateComponents.TEXTFIELD,
                new Font("Times New Roman", Font.PLAIN, 24),
                320, 315, 120, 30,
                null, null,
                true, true);
        weightTextPoint2.setText(String.format("%.3f", force_point2.value));
        weightButtonPoint2 = CreateComponents.getButton(parent, "Задать", new Font("Times New Roman", Font.PLAIN, 16),
                500, 315, 80, 30,
                this::weightSetPoint2, true, true);
        //
        parent.add(weightTextPoint2);
//        parent.add(weightButtonPoint2);
        // ------------------
        labelFlashSave = CreateComponents.getLabel(parent, "Параметры сохранены", new Font("Times New Roman", Font.PLAIN, 24),
                370, 370, false, true, MLabel.POS_CENTER);
    }

    private void weightSetPoint1(ActionEvent actionEvent) {
        Point point = new Point();
        PointK pointK;
        try {
            point.value = Double.parseDouble(weightTextPoint1.getText());
            point.adc = weight_adc;
            pointK = PointK.render(point, force_point2);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "ошибка расчета калибровки", e);
            return;
        }
        force_point1 = point;
        force_pointK = pointK;
        weightLabelPoint1.setText(String.valueOf(force_point1.adc));
        distanceCurrentShow();
    }

    private void weightSetPoint2(ActionEvent actionEvent) {
        Point point = new Point();
        PointK pointK;
        try {
            point.value = Double.parseDouble(weightTextPoint2.getText());
            point.adc = weight_adc;
            pointK = PointK.render(point, force_point1);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "ошибка расчета калибровки", e);
            return;
        }
        force_point2 = point;
        force_pointK = pointK;
//        weight_k = pointK.k;
//        weight_offset = pointK.offset;
        weightLabelPoint2.setText(String.valueOf(force_point2.adc));
        distanceCurrentShow();
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
        distance_pointK = pointK;
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
        distance_pointK = pointK;
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
    private void configSave(ActionEvent actionEvent) {
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        config.setDistanceCalib(distance_point1, distance_point2);
        config.setForceCalib(force_point1, force_point2);
        try {
            config.save();
        } catch (BaseDataException e) {
            myLog.log(Level.WARNING, "ошибка записи файла конфигурации", e);
        }
        // здесь выдать "сохранено
        new Thread(()->{
            try {
                configButtonSave.setVisible(false);
                labelFlashSave.setVisible(true);
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            } finally {
                labelFlashSave.setVisible(false);
                configButtonSave.setVisible(true);
            }
        }).start();
    }
    //
    private void readK_FromConfig() {
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        distance_point1 = new Point(
                config.getDistancePoint1_vol(),
                config.getDistancePoint1_adc()
        );
        distance_point2 = new Point(
                config.getDistancePoint2_vol(),
                config.getDistancePoint2_adc()
        );
        force_point1 = config.getForcePoint1();
        force_point2 = config.getForcePoint2();
    }
    //
    private void reciveRs(byte[] bytes, int lenght) {
        int codeSend = bytes[0] & 0x000000ff;
        if (codeSend != 17) {
            myLog.log(Level.WARNING, "Калибровка. прием ошибочного кода: " + codeSend);
//            System.out.println("calibr ups");
            return;
        }
        distance_adc = (int) Converts.bytesToInt(bytes, 2, 5);
        // ------------------------
        weight_adc = (int) Converts.bytesToInt(bytes, 2, 7);
        distanceCurrentShow();
        weightCurrentShow();
    }
    private void distanceCurrentShow() {
        double distance = Point.renderValue(distance_adc, distance_pointK);
        String str_distanceCur = String.valueOf(distance_adc) + " ==>  " + String.format("%.3f", distance);
        distanceLabelCurrent.setText(str_distanceCur);
    }
    private void weightCurrentShow() {
        double weight = Point.renderValue(weight_adc, force_pointK);
        String str_weightCur = String.valueOf(weight_adc) + " ==>  " + String.format("%.3f", weight);
        weightLabelCurrent.setText(str_weightCur);
    }
    // ===========
}
