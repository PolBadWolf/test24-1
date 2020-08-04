package org.example.bd;

import java.io.*;
import java.util.Base64;
import java.util.Properties;

class ParametersSql {
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
            urlServer = properties.getProperty("Url_Server");
            portServer = properties.getProperty("Port_Server");
            dataBase = properties.getProperty("DataBase");
            user = properties.getProperty("User");
            password = new String(Base64.getDecoder().decode(properties.getProperty("Password")));
            if (urlServer == null || portServer == null || dataBase == null || user == null || password == null) {
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

    private void setDefault() throws Exception {
        switch (type_DB) {
            case "MS_SQL":
                urlServer = "127.0.0.1";
                portServer = "1433";
                dataBase = "spc1";
                user = "max";
                password = "1122";
                break;
            case "MY_SQL":
                urlServer = "127.0.0.1";
                portServer = "3306";
                dataBase = "spc1";
                user = "root";
                password = "My*22360";
                break;
            default:
                throw new Exception("ошибка выбора загрузки по умолчанию");
        }
        save();
    }
}
