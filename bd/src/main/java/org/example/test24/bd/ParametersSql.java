package org.example.test24.bd;

import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class ParametersSql {
    final public static int OK = 0;
    final public static int FILE_NOT_FOUND = 1;
    final public static int ERROR_LOAD = 2;
    final public static int ERROR_PARAMETERS = 3;
    final public static int ERROR_PASSWORD = 4;
    final public static int ERROR_SAVE = 9;
    final public static int UNKNOWN_ERROR = 99;
    // --------------------
    private String fileNameParameters;
    private Properties properties;
    private int stat;
    public String urlServer;
    public String portServer;
    public String dataBase;
    public String user;
    public String password;

    public ParametersSql(String fileNameParameters) {
        this.fileNameParameters = fileNameParameters;
        properties = new Properties();
    }

    public int load() {
        stat = OK;
        try {
            properties.load(new BufferedReader(new FileReader(fileNameParameters)));
        } catch (IOException e) {
            return ERROR_LOAD;
        }
        try {
            urlServer = properties.getProperty("Url_Server");
            portServer = properties.getProperty("Port_Server");
            dataBase = properties.getProperty("DataBase");
            user = properties.getProperty("User");
            password = BaseData.Password.decoding(properties.getProperty("Password"));
        } catch (java.lang.Throwable ie) {
            //System.out.println(fileNameParameters + " : ошибка декодирования пароля");
            stat = ERROR_PASSWORD;
        }
        if (urlServer == null || portServer == null || dataBase == null || user == null) {
            //throw new Exception(fileNameParameters + " : один или несколько параметров в файле конфигурации отсутствуют");
            //System.out.println(fileNameParameters + " : один или несколько параметров в файле конфигурации отсутствуют");
            stat = ERROR_PARAMETERS;
        }
        return stat;
    }

    public void save() {
        properties.clear();
        properties.setProperty("Url_Server", urlServer);
        properties.setProperty("Port_Server", portServer);
        properties.setProperty("DataBase", dataBase);
        properties.setProperty("User", user);
        properties.setProperty("Password", BaseData.Password.encoding(password));
        try {
            properties.store(new BufferedWriter(new FileWriter(fileNameParameters)), "parameters sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getStat() {
        return stat;
    }
}
