package org.example.test24.loader;

import org.example.bd.BdWork;
import org.example.bd.ParametersSql;
import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.Closer;
import org.example.test24.RS232.BAUD;
import org.example.test24.runner.Running;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.ScreenClass;

import java.io.*;
import java.sql.SQLException;
import java.util.Properties;


public class MainClass {
    final public String fileNameConfig = "config.txt";
    final public String fileNameMsSql = "ms_sql.txt";
    final public String fileNameMySql = "my_sql.txt";
    final public String[] fileNameSql = {fileNameMsSql, fileNameMySql};

    private ScreenClass mainFx;
    private Running runner = null;
    public CommPort commPort = null;
    private BdWork bdWork = null;
    private String[] parameters = null;
    private StartFrame startFrame = null;
    private TiningFrame tiningFrame = null;

    public static void main(String[] args) {
        new MainClass().start(args);
    }

    private void start(String[] args) {
        parameters = getConfig();
        String namePort = parameters[1];

        mainFx= new ScreenClass();
        runner = new Running();
        commPort = new CommPort();

        Closer.getCloser().init(() -> commPort.Close(), () -> runner.Close(), mainFx);

        startFrame = StartFrame.main(getMainClassCallBack());
        try {
            while (startFrame != null) {
                Thread.yield();
                Thread.sleep(500);
            }
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        tiningFrame = new TiningFrame(this);
        tiningFrame.frameConfig(parameters);

        int checkComm = commPort.Open((bytes, lenght) -> runner.reciveRsPush(bytes, lenght), namePort, BAUD.baud57600);
        if (checkComm != CommPort.INITCODE_OK) {
            errorCommMessage(checkComm, commPort);
            System.exit(0);
        }

        try {
            bdWork = new BdWork(parameters[0], fileNameSql);
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

    public String[] getConfig() {
        Properties properties = new Properties();
        boolean flagReload = false;
        String[] strings = new String[2];

        try {
            properties.load(new FileReader(fileNameConfig));
            strings[0] = properties.getProperty("DataBase").toUpperCase();
            strings[1] = properties.getProperty("CommPort").toUpperCase();

            if (strings[0] == null || strings[1] == null)   flagReload = true;

        } catch (IOException e) {
            System.out.println("файл config.txt не найден");
            flagReload = true;
        }

        if (flagReload) {
            strings[0] = "MY_SQL";
            strings[1] = "com2";
            saveConfig(strings);
        }

        return strings;
    }
    public void saveConfig(String[] parameters) {
        Properties properties = new Properties();
        try {
            properties.setProperty("DataBase", parameters[0].toUpperCase());
            properties.setProperty("CommPort", parameters[1].toUpperCase());
            properties.store(new FileWriter(fileNameConfig), "config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean  checkCommPort(String portName) {
        int ch =  commPort.Open(null, portName, BAUD.baud57600);
        commPort.Close();
        return ch == CommPort.INITCODE_OK;
    }

    private MainClassCallBack getMainClassCallBack() {
        return new MainClassCallBack() {

            @Override
            public boolean checkCommPort() {
                return MainClass.this.checkCommPort(parameters[1]);
            }

            @Override
            public boolean checkSql() {
                boolean stat;
                ParametersSql parametersSql;
                try {
                    bdWork = new BdWork(parameters[0], fileNameSql);
                    parametersSql = bdWork.getParametrsSql();
                    parametersSql.load();
                    stat = bdWork.testStuctBase(
                            parametersSql.urlServer,
                            parametersSql.portServer,
                            parametersSql.user,
                            parametersSql.password,
                            parametersSql.dataBase
                    );
                } catch (java.lang.Throwable e) {
                    e.printStackTrace();
                    stat = false;
                }
                return stat;
            }

            @Override
            public void closeFrame() {
                MainClass.this.startFrame = null;
            }
        };
    }
}
