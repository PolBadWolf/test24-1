package ru.yandex.fixcolor.tests.spc.lib;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
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
                        , 0, 100
                );
                handler.setEncoding("cp1251");
                handler.setFormatter(
                        new Formatter() {
                            @Override
                            public String format(LogRecord record) {
                                StringBuilder string =
                                        new StringBuilder(formatLogFile.format(record.getMillis())
                                                + " Class: \"" + record.getSourceClassName() + "\""
                                                + " Method: \"" + record.getSourceMethodName() + "\""
                                                + "\n"
                                                + record.getLevel().getName() + ": \"" + record.getMessage() + "\"\n");
                                Throwable throwable = record.getThrown();
                                if (throwable != null) {
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    record.getThrown().printStackTrace(pw);
                                    pw.close();
                                    string.append(sw.toString());
                                }
                                string.append("---------------------------------------------------------\n");
                                return string.toString();
                            }
                        }
                );
            } catch (IOException e ) {
                System.out.println("Ошибка создания файлово логера:");
                e.printStackTrace();
            }
            if (handler != null) {
                handler.setLevel(fileLevel);
                myLog.addHandler(handler);
            } else {
                System.out.println("Ошибка создания логера");
                System.exit(-10);
            }
        }
    }
}
