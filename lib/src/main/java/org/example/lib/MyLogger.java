package org.example.lib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.*;

public class MyLogger {
    public static Logger myLog;
    private final static String logFilePath = "./log/";
    private final static SimpleDateFormat formatForNameLogFile = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat formatLogFile = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                        + formatForNameLogFile.format(Calendar.getInstance().getTime())
                        + ".log"
                        , 0, 50
                );
                handler.setEncoding("cp1251");
                handler.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        StringBuilder string =
                                new StringBuilder(formatLogFile.format(record.getMillis())
                                        + " Class: \"" + record.getSourceClassName() + "\""
                                        + " Method: \"" + record.getSourceMethodName() + "\""
                                        + "\n"
                                        + record.getLevel().getName() + ": \"" + record.getMessage() + "\"");
                        Object[] parameters = record.getParameters();
                        if (parameters != null) {
                            for (Object parameter : parameters) {
                                StackTraceElement traceElement = (StackTraceElement) parameter;
                                string.append("\n   ").append(traceElement.toString());
                            }
                        }
                        string.append("\n---------------------------------------------------------\n");
                        return string.toString();
                    }
                });
            } catch (IOException e) {
                System.out.println("Ошибка создания файлово логера: \n" + Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
            handler.setLevel(fileLevel);
            myLog.addHandler(handler);
        }
    }
}
