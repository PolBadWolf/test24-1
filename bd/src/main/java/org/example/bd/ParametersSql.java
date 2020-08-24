package org.example.bd;

import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class ParametersSql {
    private String fileNameParameters;
    private Properties properties;
    private String type_DB;
    public String urlServer;
    public String portServer;
    public String dataBase;
    public String user;
    public String password;

    public ParametersSql(String fileNameParameters, String type_DB) {
        this.fileNameParameters = fileNameParameters;
        this.type_DB = type_DB;
        properties = new Properties();
    }

    public void load() throws Exception {
        try {
            properties.load(new BufferedReader(new FileReader(fileNameParameters)));
        } catch (IOException e) {
            throw new Exception("отсутствует файл конфигурации sql : " + fileNameParameters);
        }
        try {
            urlServer = properties.getProperty("Url_Server");
            portServer = properties.getProperty("Port_Server");
            dataBase = properties.getProperty("DataBase");
            user = properties.getProperty("User");
            password = new String(Base64.getDecoder().decode(properties.getProperty("Password")));
        } catch (java.lang.Throwable ie) {
            System.out.println(fileNameParameters + " : ошибка декодирования пароля");
        }
        if (urlServer == null || portServer == null || dataBase == null || user == null || password == null) {
            //throw new Exception(fileNameParameters + " : один или несколько параметров в файле конфигурации отсутствуют");
            System.out.println(fileNameParameters + " : один или несколько параметров в файле конфигурации отсутствуют");
        }
    }

    public void save() {
        properties.clear();
        properties.setProperty("Url_Server", urlServer);
        properties.setProperty("Port_Server", portServer);
        properties.setProperty("DataBase", dataBase);
        properties.setProperty("User", user);
        properties.setProperty("Password", new String(java.util.Base64.getEncoder().encode(password.getBytes())));
        try {
            properties.store(new BufferedWriter(new FileWriter(fileNameParameters)), "parameters sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
