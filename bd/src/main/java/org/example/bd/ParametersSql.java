package org.example.bd;

import java.io.*;
import java.util.Base64;
import java.util.Properties;

class ParametersSql {
    private String fileNameParameters;
    private Properties properties;
    public String typeBd;
    public String driver;
    public String urlServer;
    public String portServer;
    public String dataBase;
    public String user;
    public String password;

    public ParametersSql(String fileNameParameters) {
        this.fileNameParameters = fileNameParameters;
        properties = new Properties();
    }

    public void load() {
        try {
            properties.load(new BufferedReader(new FileReader(fileNameParameters)));
            typeBd = properties.getProperty("Type_Sql");
            driver = properties.getProperty("Driver");
            urlServer = properties.getProperty("Url_Server");
            portServer = properties.getProperty("Port_Server");
            dataBase = properties.getProperty("DataBase");
            user = properties.getProperty("User");
            password = new String(Base64.getDecoder().decode(properties.getProperty("Password")));
            if (typeBd == null || urlServer == null || portServer == null || dataBase == null || user == null || password == null) {
                System.out.println("один или несколько параметров в файле конфигурации отсутствуют");
                setDefault();
            }
        } catch (IOException e) {
            System.out.println("файл конфигурации sql не найден");
            setDefault();
        }
    }

    public void save() {
        properties.clear();
        properties.setProperty("Type_Sql", typeBd);
        properties.setProperty("Driver", driver);
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

    private void setDefault() {
        typeBd = "sqlserver";
        driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        urlServer = "127.0.0.1";
        portServer = "1433";
        dataBase = "spc1";
        user = "max";
        password = "1122";
        save();
    }
}
