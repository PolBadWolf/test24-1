package ru.yandex.fixcolor.tests.spc.bd;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;

class ParametersSql implements BaseData.Parameters {
    final static String fileNameMySql = "my_sql.txt";
    final static String fileNameMsSql = "ms_sql.txt";
    // ------------------------------------------------
    private String fileName;
    private final TypeBaseDate typeBaseDate;
    private Status stat;
    private String ipServer;
    private String portServer;
    private String dataBase;
    private String user;
    private String password;
    // ------------------------------------------------
    @Override
    public Status getStat() {
        return stat;
    }
    @Override
    public TypeBaseDate getTypeBaseDate() {
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
    ParametersSql(TypeBaseDate typeBaseDate) throws ParametersSqlException {
        switch (typeBaseDate.codeTypeBaseData) {
            case TypeBaseDateConst.TYPEBD_MSSQL:
                fileName = fileNameMsSql;
                break;
            case TypeBaseDateConst.TYPEBD_MYSQL:
                fileName = fileNameMySql;
                break;
            case TypeBaseDateConst.TYPEBD_ERROR:
                this.stat = Status.PARAMETERS_LOAD_ERROR;
                throw new ParametersSqlException("ошибочный тип БД", Status.BASE_TYPE_ERROR, null);
        }
        this.typeBaseDate = typeBaseDate;
        this.stat = Status.OK;
    }
    // ------------------------------------------------
    @Override
    public Status load() throws ParametersSqlException {
        ParametersSql parameters = new ParametersSql(this.typeBaseDate);
        Properties properties = new Properties();

        try {
            properties.load(new BufferedReader(new FileReader(fileName)));
        } catch (IOException e) {
            parameters.stat = Status.PARAMETERS_LOAD_ERROR;
            throw new ParametersSqlException("ошибки загрузки параметров из файла конфигурации", e, Status.PARAMETERS_LOAD_ERROR, null);
        }
        parameters.ipServer = properties.getProperty("Url_Server");
        parameters.portServer = properties.getProperty("Port_Server");
        parameters.dataBase = properties.getProperty("DataBase");
        parameters.user = properties.getProperty("User");
        //
        try { parameters.password = BaseData.Password.decoding(properties.getProperty("Password"));
        } catch (Exception e) {
            parameters.password = null;
            parameters.stat = Status.PARAMETERS_PASSWORD_ERROR;
            myLog.log(Level.WARNING, "ошибка декодирования пароля", e);
        }
        //
        if (parameters.ipServer == null || parameters.portServer == null || parameters.dataBase == null || parameters.user == null || parameters.password == null) {
            parameters.stat = Status.PARAMETERS_ERROR;
            //myLog.log(Level.SEVERE, "один или несколько параметров в файле конфигурации отсутствуют");
            throw new ParametersSqlException("один или несколько параметров в файле конфигурации отсутствуют", parameters.stat, parameters);
        }
        //
        {
            this.ipServer = parameters.ipServer;
            this.portServer = parameters.portServer;
            this.dataBase = parameters.dataBase;
            this.user = parameters.user;
            this.password = parameters.password;
            this.stat = Status.OK;
        }
        return this.stat;
    }
    @Override
    public Status save() {
        Properties properties = new Properties();
        properties.setProperty("Url_Server", ipServer);
        properties.setProperty("Port_Server", portServer);
        properties.setProperty("DataBase", dataBase);
        properties.setProperty("User", user);
        properties.setProperty("Password", BaseData.Password.encoding(password));
        try {
            properties.store(new BufferedWriter(new FileWriter(fileName)), "parameters sql");
            stat = Status.OK;
        } catch (IOException e) {
            myLog.log(Level.SEVERE, "ошибка сохранения параметров соединения с БД", e);
            stat = Status.PARAMETERS_SAVE_ERROR;
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
