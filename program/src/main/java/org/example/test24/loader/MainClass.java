package org.example.test24.loader;

import org.example.test24.lib.MyLogger;
import org.example.test24.bd.*;
import org.example.test24.RS232.CommPort;
import org.example.test24.RS232.BAUD;
import org.example.test24.loader.dialog.StartFrame;
import org.example.test24.loader.archive.ViewArchive;
import org.example.test24.runner.Runner;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.MainFrame_interface;
import org.example.test24.screen.ScreenFx;

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
        runner = Runner.main(o->runnerCloser());
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
                new Thread(()-> {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ViewArchive v = new ViewArchive(connBd);
                                if (v != null) {
                                    MainFrame.mainFrame.buttonArchive.setDisable(true);
                                }
                            } catch (Exception e) {
                                MyLogger.myLog.log(Level.SEVERE, "запуск окна орхива", e);
                            }
                        }
                    });
                }, "start arhive").start();
            }
        });
        // пуск регистрации
        runner.init(connBd, commPort, MainFrame.mainFrame);
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
//                    connBd = conn;
//                    MainClass.this.commPortName = commPortName;
//                    new Thread(() -> MainClass.this.startFx()).start();
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

    private void cont() {
        System.out.println("start ?");
        if (MainFrame.mainFrame == null) {
            screenFx.main();
        }
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }
    }
    private void puskStartFrame() {
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
    private void runnerCloser() {
        close();
    }

    public static ScreenFx getScreenFx() {
        return screenFx;
    }
    // ===============================================

}
