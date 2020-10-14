package org.example.test24.loader;

import org.example.test24.lib.MyLogger;
import org.example.test24.bd.*;
import org.example.test24.RS232.CommPort;
import org.example.test24.RS232.BAUD;
import org.example.test24.loader.dialog.StartFrame;
import org.example.test24.runner.Runner;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.ScreenFx;

import java.util.logging.Level;


public class MainClass {
    // модули
    protected ScreenFx screenFx;
    protected Runner runner;
    protected CommPort commPort;
    protected StartFrame startFrame;
    //
    private boolean statMainWork;
    BaseData connBd;
    public static void main(String[] args) {
        new MyLogger(Level.ALL, Level.OFF);
        Thread.currentThread().setName("Main class thread");
        new MainClass().start();
    }
    protected MainClass() {
        statMainWork = false;
    }
    private void start() {
        // создание основных объектов
        screenFx = ScreenFx.init(o->screenCloser());
        runner = Runner.main(o->runnerCloser());
        commPort = CommPort.main();

        // пуск
        puskStartFrame();
    }
    private void cont() {
        System.out.println("start ?");
        // тут вызов основной формы
        screenFx.main();
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }
        runner.init(connBd, commPort, MainFrame.mainFrame);
        //commPort.ReciveStart();
        // возврат в настройки
        //puskStartFrame();
    }
    private void puskStartFrame() {
        try {
            startFrame = StartFrame.main(statMainWork, new StartFrame.CallBack() {
                @Override
                public void messageCloseStartFrame(BaseData conn) {
                    statMainWork = true;
                    connBd = conn;
                    new Thread(()->cont()).start();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(-100);
        }
    }
    // ===============================================
    private void close() {
        if (screenFx != null) {
            screenFx.exitApp();
            screenFx = null;
        }
        if (commPort != null) {
            commPort.close();
            commPort = null;
        }
        if (runner != null) {
            runner.Close();
            runner = null;
        }
        System.exit(0);
    }
    private void screenCloser() {
        close();
    }
    private void runnerCloser() {
        close();
    }
    private void commPortCloser() {
        close();
    }

    // ===============================================



    // загрузка конфигурации
    /*private ParametersConfig.Diagnostic requestParametersConfig(String fileNameConfig, Consumer<ParametersConfig> parametersConfig) {
        ParametersConfig parameters = new ParametersConfig(fileNameConfig);
        ParametersConfig.Diagnostic result = parameters.load();
        if (parametersConfig != null) parametersConfig.accept(parameters);
        return result;
    }*/

    private void errorCommMessage(int checkComm, CommPort commPort) {
        switch (checkComm) {
            case CommPort.INITCODE_ERROROPEN:
                System.out.println("ошибка открытия порта");
                break;
            case CommPort.INITCODE_NOTEXIST:
                System.out.println("указанный порт отсутствует");
                System.out.println("имеющиеся порты в системе:");
                for (String name : CommPort.getListPortsName()) {
                    System.out.println(name);
                }
                break;
        }
    }

    private boolean checkCommPort(String portName) {
        CommPort.PortStat ch =  commPort.open(null, portName, BAUD.baud57600);
        commPort.close();
        return ch == CommPort.PortStat.INITCODE_OK;
    }


}
