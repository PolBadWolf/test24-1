package org.example.test24.loader;

import javafx.application.Platform;
import org.example.test24.allinterface.screen.MainFrame_interface;
import org.example.test24.lib.MyLogger;
import org.example.test24.bd.*;
import org.example.test24.RS232.CommPort;
import org.example.test24.RS232.BAUD;
import org.example.test24.loader.dialog.StartFrame;
import org.example.test24.runner.Runner;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.ScreenClass;
import org.example.test24.screen.ScreenFx;

import java.util.logging.Level;


public class MainClass {
    // модули
    protected ScreenFx screenFx;
    protected Runner runner;
    protected CommPort commPort;
    //
    private boolean statMainWork;
    private BaseData connBd;
    private String commPortName;
    //
    public static void main(String[] args) {
        new MyLogger(Level.ALL, Level.OFF);
        Thread.currentThread().setName("Main class thread");
        new MainClass().start();
    }
    protected MainClass() {
        statMainWork = false;
    }
    private void start() {
        // стар фрейм
        try {
            StartFrame.main(statMainWork, new StartFrame.CallBack() {
                @Override
                public void messageCloseStartFrame(BaseData conn, String commPortName) {
                    statMainWork = true;
                    connBd = conn;
                    MainClass.this.commPortName = commPortName;
                    new Thread(() -> MainClass.this.startFx()).start();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(-100);
        }
    }
    private void startFx() {
        // создание основных объектов
        screenFx = ScreenFx.init(() -> close());
        runner = Runner.main(o->runnerCloser());
        commPort = CommPort.main();
        // вызов основной формы
        screenFx.main();
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }
        MainFrame.mainFrame.setCallBack(this::newTestPuser);
        // пуск регистрации
        runner.init(connBd, commPort, MainFrame.mainFrame);
        commPort.open(runner::reciveRsPush, commPortName, BAUD.baud57600);
        commPort.ReciveStart();
    }

    private void newTestPuser() {
        commPort.ReciveStop();
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
//                screenFx.exitApp();
//                screenFx = null;
//                screenFx.setVisible(false);
            }
        }, "restart").start();
    }
    private void runnerCloser() {
        close();
    }

    // ===============================================

}
