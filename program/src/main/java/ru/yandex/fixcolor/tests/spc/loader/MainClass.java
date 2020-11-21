package ru.yandex.fixcolor.tests.spc.loader;

import javafx.application.Platform;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.rs232.*;
import ru.yandex.fixcolor.tests.spc.runner.Runner;
import ru.yandex.fixcolor.tests.spc.screen.*;
import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.loader.archive.ViewArchive;
import ru.yandex.fixcolor.tests.spc.loader.dialog.StartFrame;

import javax.swing.*;
import java.util.logging.Level;


public class MainClass {
    // модули
    protected static ScreenFx screenFx;
    protected static Runner runner;
    protected static CommPort commPort;
    //
    private BaseData connBd;
    private String commPortName;
    //
    public static void main(String[] args) {
        new MyLogger(Level.ALL, Level.OFF);
        Thread.currentThread().setName("Main class thread");
        new MainClass().start();
    }
    protected MainClass() {
    }
    private void start() {
        // стар фрейм
        try {
            StartFrame.main(false, new StartFrame.CallBack() {
                @Override
                public void messageCloseStartFrame(BaseData conn, String commPortName) {
                    connBd = conn;
                    MainClass.this.commPortName = commPortName;
                    new Thread(MainClass.this::startFx, "start fx").start();
                }

                @Override
                public void messageSetNewData() {

                }

                @Override
                public void stopSystem() {
                    if (commPort != null) commPort.close();
                    if (runner != null) runner.Close();
                    if (screenFx != null) screenFx.exitApp();
                    if (commPort != null) commPort = null;
                    if (runner != null) runner = null;
                    if (screenFx != null) screenFx = null;
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(-100);
        }
    }
    private void startFx() {
        // создание основных объектов
        screenFx = ScreenFx.init(this::close);
        runner = Runner.main(new RunnerCallBack());
        commPort = CommPort.main();
        // вызов основной формы
        screenFx.main();
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }
        MainFrame.mainFrame.setCallBack(new MainFrame_interface.CallBack() {
            @Override
            public void buttonExit_onAction() {
                newTestPuser();
            }

            @Override
            public void startViewArchive() {
                new Thread(()-> SwingUtilities.invokeLater(() -> {
                    try {
                        ViewArchive v = new ViewArchive(new ViewArchive.CallBack() {
                            @Override
                            public void closeArchive() {
                                Platform.runLater(() -> {
                                    MainClass.getScreenFx().setRootFocus();
                                    MainFrame.mainFrame.buttonArchive.setDisable(false);
                                });
                            }
                        }, connBd);
                        if (v != null) {
                            MainFrame.mainFrame.buttonArchive.setDisable(true);
                        }
                    } catch (Exception e) {
                        MyLogger.myLog.log(Level.SEVERE, "запуск окна орхива", e);
                    }
                }), "start arhive").start();
            }
        });
        // пуск регистрации
        runner.init(connBd, MainFrame.mainFrame);
        commPort.open(runner::reciveRsPush, commPortName, BAUD.baud57600);
        commPort.ReciveStart();
    }

    private void newTestPuser() {
        new Thread(()->{
            commPort.ReciveStop();
            // стар фрейм
            try {
                StartFrame.main(true, new StartFrame.CallBack() {
                    @Override
                    public void messageCloseStartFrame(BaseData conn, String commPortName) {
                    }

                    @Override
                    public void messageSetNewData() {
                        runner.fillFields();
                        commPort.ReciveStart();
                    }

                    @Override
                    public void stopSystem() {
                        if (commPort != null) commPort.close();
                        if (runner != null) runner.Close();
                        if (screenFx != null) screenFx.exitApp();
                        if (commPort != null) commPort = null;
                        if (runner != null) runner = null;
                        if (screenFx != null) screenFx = null;
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
                System.exit(-100);
            }
        }, "st fr").start();
    }
    // ===============================================
    private void close() {
        new Thread(()->{
            if (commPort != null) {
                commPort.close();
                //commPort = null;
            }
            if (runner != null) {
                runner.Close();
                runner = null;
            }
            if (screenFx != null) {
                screenFx.exitApp();
                screenFx = null;
//                screenFx.setVisible(false);
            }
        }, "restart").start();
    }

    public static ScreenFx getScreenFx() {
        return screenFx;
    }
    // ===============================================
    class RunnerCallBack implements Runner.CallBack {
        @Override
        public void sendStopAutoMode() {
            commPort.sendMessageStopAuto();
        }
    }
    // ===============================================
}
