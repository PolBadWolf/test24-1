package org.example.test24.bd;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class ParametersSql implements BaseData.Parameters {
    final static String fileNameMySql = "my_sql.txt";
    final static String fileNameMsSql = "ms_sql.txt";
    // ------------------------------------------------
    private String fileName;
    private BaseData.TypeBaseDate typeBaseDate;
    private BaseData.Status stat;
    private String ipServer;
    private String portServer;
    private String dataBase;
    private String user;
    private String password;
    // ------------------------------------------------
    @Override
    public BaseData.Status getStat() {
        return stat;
    }
    @Override
    public BaseData.TypeBaseDate getTypeBaseDate() {
        return typeBaseDate;
    }
    @Override
    public String getIpServer() {
        return ipServer;
    }
    @Override
    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }
    @Override
    public String getPortServer() {
        return portServer;
    }
    @Override
    public void setPortServer(String portServer) {
        this.portServer = portServer;
    }
    @Override
    public String getDataBase() {
        return dataBase;
    }
    @Override
    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }
    @Override
    public String getUser() {
        return user;
    }
    @Override
    public void setUser(String user) {
        this.user = user;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    // ------------------------------------------------
    ParametersSql(BaseData.TypeBaseDate typeBaseDate) throws Exception {
        switch (typeBaseDate.codeTypeBaseData) {
            case BaseData.TYPEBD_MSSQL:
                fileName = fileNameMsSql;
                break;
            case BaseData.TYPEBD_MYSQL:
                fileName = fileNameMySql;
                break;
            case BaseData.TYPEBD_ERROR:
                this.stat = BaseData.Status.PARAMETERS_LOAD_ERROR;
                throw new Exception("ошибочный тип БД");
        }
        this.typeBaseDate = typeBaseDate;
        this.stat = BaseData.Status.OK;
    }
    // ------------------------------------------------
    @Override
    public BaseData.Status load() throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new FileReader(fileName)));
        } catch (IOException e) {
            stat = BaseData.Status.PARAMETERS_LOAD_ERROR;
            throw new Exception("ошибки загрузки параметров из файла конфигурации", e);
        }
        ipServer = properties.getProperty("Url_Server");
        portServer = properties.getProperty("Port_Server");
        dataBase = properties.getProperty("DataBase");
        user = properties.getProperty("User");
        //
        try {
            password = BaseData.Password.decoding(properties.getProperty("Password"));
        } catch (Exception e) {
            stat = BaseData.Status.PARAMETERS_PASSWORD_ERROR;
            password = null;
            myLog.log(Level.WARNING, "ошибка декодирования пароля", e);
        }
        //
        if (ipServer == null || portServer == null || dataBase == null || user == null) {
            myLog.log(Level.SEVERE, "один или несколько параметров в файле конфигурации отсутствуют");
            stat = BaseData.Status.PARAMETERS_ERROR;
        } else {
            if (password != null) stat = BaseData.Status.OK;
        }
        //
        return stat;
    }
    @Override
    public BaseData.Status save() {
        Properties properties = new Properties();
        properties.setProperty("Url_Server", ipServer);
        properties.setProperty("Port_Server", portServer);
        properties.setProperty("DataBase", dataBase);
        properties.setProperty("User", user);
        properties.setProperty("Password", BaseData.Password.encoding(password));
        try {
            properties.store(new BufferedWriter(new FileWriter(fileName)), "parameters sql");
            stat = BaseData.Status.OK;
        } catch (IOException e) {
            myLog.log(Level.SEVERE, "ошибка сохранения параметров соединения с БД", e);
            stat = BaseData.Status.PARAMETERS_SAVE_ERROR;
        }
        return stat;
    }
    @Override
    public void setDefault() {
        ipServer = "255.255.255.255";
        portServer = "3306";
        dataBase = "data_base";
        user = "login";
        password = "password";
    }
}
