package ru.yandex.fixcolor.tests.spc.loader;

import javafx.application.Platform;
import ru.yandex.fixcolor.tests.spc.bd.BaseDataException;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.rs232.*;
import ru.yandex.fixcolor.tests.spc.runner.Runner;
import ru.yandex.fixcolor.tests.spc.screen.*;
import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.loader.archive.ViewArchive;
import ru.yandex.fixcolor.tests.spc.loader.dialog.StartFrame;

import javax.swing.*;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;


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
        // чтение конфигурации
        BaseData.Config config = BaseData.Config.create();
        try {
            config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        commPort = CommPort.init();
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
                        public void stopSystem() {
                            if (commPort != null) commPort.close();
                            if (runner != null) runner.Close();
                            if (screenFx != null) screenFx.exitApp();
                            if (commPort != null) commPort = null;
                            if (runner != null) runner = null;
                            if (screenFx != null) screenFx = null;
                        }

                    },
                    commPort);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(-100);
        }
    }
    private void startFx() {
        // создание основных объектов
        screenFx = ScreenFx.init(
                new ScreenFx.Closer() {
                    @Override
                    public void close() {
                        MainClass.this.close();
                    }

                    @Override
                    public int loadMaxNcycle() {
                        return loadParameterMaxNcycle();
                    }
                }
        );
        runner = Runner.main(new RunnerCallBack());
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
            // установлено новое значение nMax
            @Override
            public void send_nMax(String textNmax) {
                int nMax = 1;
                try {
                    nMax = Integer.parseInt(textNmax);
                } catch (Exception e) {
                    myLog.log(Level.WARNING, "ошибка чтения максимального числа итераций");
                    nMax = 1;
                } finally {
                    if (nMax < 1 || nMax > 32) nMax = 1;
                }
                try {
                    commPort.sendMessageStopNcycleMax(nMax);
                    saveParameterMaxNcycle(nMax);
                } catch (Exception exception) {
                    myLog.log(Level.SEVERE, "ошибка передачи максимального количества циклов");
                }
            }
        });
        // пуск регистрации
        runner.init(connBd, MainFrame.mainFrame);
        try {
            commPortOpen(commPort, commPortName, runner::reciveRsPush);
        } catch (Exception exception) {
            myLog.log(Level.SEVERE, "открытие comm port при старте fx");
            System.exit(-1000);
        }
        commPort.ReciveStart();
    }
    // вызов Start Frame из fx
    private void newTestPuser() {
        new Thread(()->{
            commPort.ReciveStop();
            commPort.close();
            // стар фрейм
            try {
                StartFrame.main(true, new StartFrame.CallBack() {
                            @Override
                            public void messageCloseStartFrame(BaseData conn, String commPortName) throws Exception {
                                runner.fillFields();
                                runner.loadConfigK();
                                commPortOpen(commPort, commPortName, runner::reciveRsPush);
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

                        },
                        commPort);
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
        public void sendStopNcycleMax(int nCycleMax) {
            try {
                commPort.sendMessageStopNcycleMax(nCycleMax);
            } catch (Exception exception) {
                myLog.log(Level.SEVERE, "команда стоп авто режим", exception);
            }
        }

        @Override
        public void sendMessageStop() {
            try {
                commPort.sendMessageStop();
            } catch (Exception exception) {
                myLog.log(Level.SEVERE, "команда стоп авто режим", exception);
            }
        }
    }
    // ===============================================
    // чтение имени порта
    public static String getPortNameFromConfig() {
        // чтение конфигурации
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        return config.getPortName();
    }
    public static CommPort.PortStat commPortOpen(CommPort commPort, String commPortName, CommPort.CallBack callBack) throws Exception {
        if (commPort == null) throw new Exception("Ошибка открытия comm port (объект не создан)");
        return commPort.open(callBack, commPortName, BAUD.baud57600);
    }

    // ============
    private void saveParameterMaxNcycle(int maxNcycle) {
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        if (config.getMaxNcycle() != maxNcycle) {
            config.setMaxNcycle(maxNcycle);
            try {
                config.save();
            } catch (BaseDataException e) {
                myLog.log(Level.WARNING, "ошибка записи файла конфигурации", e);
            }
        }
    }
    public int loadParameterMaxNcycle() {
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException be) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", be);
            config.setDefault();
        }
        return config.getMaxNcycle();
    }
}
