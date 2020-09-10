package org.example.lib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
    public static Logger myLog;
    private final static String logFilePath = "./";
    private final static SimpleDateFormat formatLogFile = new SimpleDateFormat("yyyy-MM-dd");
    public MyLogger(Level consoleLevel, Level fileLevel) {
        myLog = Logger.getLogger("Logger spc");
        myLog.setLevel(Level.ALL);
        myLog.setUseParentHandlers(false);
        if (consoleLevel != Level.OFF) {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(consoleLevel);
            myLog.addHandler(handler);
        }
        if (fileLevel != Level.OFF) {
            FileHandler handler = null;
            try {
                handler = new FileHandler(
                        logFilePath
                        + "log_"
                        + formatLogFile.format(Calendar.getInstance().getTime())
                        + ".log"
                        , 0, 50
                );
                handler.setEncoding("cp1251");
            } catch (IOException e) {
                System.out.println("Ошибка создания файлово логера: \n" + e.getStackTrace().toString());
                e.printStackTrace();
            }
            handler.setLevel(fileLevel);;
            myLog.addHandler(handler);
        }
    }
}
