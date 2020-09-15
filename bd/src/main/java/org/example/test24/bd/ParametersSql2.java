package org.example.test24.bd;

import java.io.*;
import java.util.Properties;

public class ParametersSql2 {
    final public static int OK = 0;
    final public static int FILE_NOT_FOUND = 1;
    final public static int ERROR_LOAD = 2;
    final public static int ERROR_PARAMETERS = 3;
    final public static int ERROR_PASSWORD = 4;
    final public static int ERROR_SAVE = 9;
    final public static int UNKNOWN_ERROR = 99;
    public enum Status {
        OK                  (ParametersSql2.OK),
        FILE_NOT_FOUND      (ParametersSql2.FILE_NOT_FOUND),
        ERROR_LOAD          (ParametersSql2.ERROR_LOAD),
        ERROR_PARAMETERS    (ParametersSql2.ERROR_PARAMETERS),
        ERROR_PASSWORD      (ParametersSql2.ERROR_PASSWORD),
        ERROR_SAVE          (ParametersSql2.ERROR_SAVE),
        UNKNOWN_ERROR       (ParametersSql2.UNKNOWN_ERROR);
        int codeStatus;
        Status(int codeStatus) {this.codeStatus = codeStatus;}
        public int getCodeStatus() {
            return codeStatus;
        }
        @Override
        public String toString() {
            String status = "UNKNOWN_ERROR";
            switch (codeStatus) {
                case ParametersSql2.OK:
                    status = "OK";
                    break;
                case ParametersSql2.FILE_NOT_FOUND:
                    status = "FILE NOT FOUND";
                    break;
                case ParametersSql2.ERROR_LOAD:
                    status = "ERROR LOAD";
                    break;
                case ParametersSql2.ERROR_PARAMETERS:
                    status = "ERROR PARAMETERS";
                    break;
                case ParametersSql2.ERROR_PASSWORD:
                    status = "ERROR PASSWORD";
                    break;
                case ParametersSql2.ERROR_SAVE:
                    status = "ERROR SAVE";
                    break;
                case ParametersSql2.UNKNOWN_ERROR:
                    status = "UNKNOWN ERROR";
                    break;
            }
            return status;
        }
    }
    // --------------------
    private String fileNameParameters;
    private Properties properties;
    private Status stat;
    final public BaseData2.TypeBaseData typeBaseData;
    public String urlServer;
    public String portServer;
    public String dataBase;
    public String user;
    public String password;

    public ParametersSql2(String fileNameParameters, BaseData2.TypeBaseData typeBaseData) {
        this.fileNameParameters = fileNameParameters;
        this.typeBaseData = typeBaseData;
        properties = new Properties();
    }

    public Status load() {
        stat = Status.OK;
        try {
            properties.load(new BufferedReader(new FileReader(fileNameParameters)));
        } catch (IOException e) {
            return Status.ERROR_LOAD;
        }
        try {
            urlServer = properties.getProperty("Url_Server");
            portServer = properties.getProperty("Port_Server");
            dataBase = properties.getProperty("DataBase");
            user = properties.getProperty("User");
            password = BaseData2.Password.decoding(properties.getProperty("Password"));
        } catch (java.lang.Throwable ie) {
            //System.out.println(fileNameParameters + " : ошибка декодирования пароля");
            stat = Status.ERROR_PASSWORD;
        }
        if (typeBaseData == BaseData2.TypeBaseData.ERROR || urlServer == null || portServer == null || dataBase == null || user == null) {
            //throw new Exception(fileNameParameters + " : один или несколько параметров в файле конфигурации отсутствуют");
            //System.out.println(fileNameParameters + " : один или несколько параметров в файле конфигурации отсутствуют");
            stat = Status.ERROR_PARAMETERS;
        }
        return stat;
    }

    public void save() {
        properties.clear();
        properties.setProperty("Url_Server", urlServer);
        properties.setProperty("Port_Server", portServer);
        properties.setProperty("DataBase", dataBase);
        properties.setProperty("User", user);
        properties.setProperty("Password", BaseData2.Password.encoding(password));
        try {
            properties.store(new BufferedWriter(new FileWriter(fileNameParameters)), "parameters sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefault() {
        urlServer = "255.255.255.255";
        portServer = "1111";
        dataBase = "Base";
        user = "Login";
        password = "Password";
    }

    public Status getStat() {
        return stat;
    }
}
