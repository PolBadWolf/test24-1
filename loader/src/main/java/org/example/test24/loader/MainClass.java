package org.example.test24.loader;

import org.example.bd.BdWork;
import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.Closer;
import org.example.test24.RS232.BAUD;
import org.example.test24.runner.Running;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.ScreenClass;

import javax.swing.*;
import java.io.*;
import java.util.Properties;


public class MainClass {
    private ScreenClass mainFx = null;
    private Running runner = null;
    public CommPort commPort = null;
    private BdWork bdWork = null;
    private StartFrame startFrame = null;

    public static void main(String[] args) {
        new MainClass().start(args);
    }

    private void start(String[] args) {
        String[] param = getConfig();
        String namePort = param[1];

        mainFx= new ScreenClass();
        runner = new Running();
        commPort = new CommPort();

        Closer.getCloser().init(() -> commPort.Close(), () -> runner.Close(), mainFx);

        startFrame = new StartFrame(this);
        startFrame.frameConfig(param);

        int checkComm = commPort.Open((bytes, lenght) -> runner.reciveRsPush(bytes, lenght), namePort, BAUD.baud57600);
        if (checkComm != CommPort.INITCODE_OK) {
            errorCommMessage(checkComm, commPort);
            System.exit(0);
        }

        try {
            bdWork = new BdWork(param[0]);
            bdWork.getConnect();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }


        (new Thread(mainFx)).start();
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }

        runner.init(bdWork, commPort, MainFrame.mainFrame);

        commPort.ReciveStart();
    }



    private void errorCommMessage(int checkComm, CommPort commPort) {
        switch (checkComm) {
            case CommPort.INITCODE_ERROROPEN:
                System.out.println("ошибка открытия порта");
                break;
            case CommPort.INITCODE_NOTEXIST:
                System.out.println("указанный порт отсутствует");
                System.out.println("имеющиеся порты в системе:");
                for (String name : commPort.getListPortsName()) {
                    System.out.println(name);
                }
                break;
        }
    }

    private String[] getConfig() {
        Properties properties = new Properties();
        boolean flagReload = false;
        String[] strings = new String[2];

        try {
            properties.load(new FileReader("config.txt"));
            strings[0] = properties.getProperty("DataBase");
            strings[1] = properties.getProperty("CommPort");

            if (strings[0] == null || strings[1] == null)   flagReload = true;

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("файл config.txt не найден");
            flagReload = true;
        }

        if (flagReload) {
            strings[0] = "MY_SQL";
            strings[1] = "com2";
            try {
                properties.setProperty("DataBase", strings[0]);
                properties.setProperty("CommPort", strings[1]);
                properties.store(new FileWriter("config.txt"), "config.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return strings;
    }
}
