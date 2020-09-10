package org.example.test24.bd;

import java.util.Properties;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class ParametersSql implements BaseData.Parameters {
    final static String fileNameMySql = "my_sql.txt";
    final static String fileNameMsSql = "ms_sql.txt";
    // ------------------------------------------------
    final private String fileName;
    private BaseData.TypeBaseDate typeBaseDate;
    private Properties properties;
    private BaseData.Status stat;
    private String ipServer;
    private String portServer;
    private String dataBase;
    private String user;
    private String password;
    // ------------------------------------------------
    public BaseData.Status getStat() {
        return stat;
    }
    public String getIpServer() {
        return ipServer;
    }
    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }
    public String getPortServer() {
        return portServer;
    }
    public void setPortServer(String portServer) {
        this.portServer = portServer;
    }
    public String getDataBase() {
        return dataBase;
    }
    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public ParametersSql(BaseData.TypeBaseDate typeBaseDate) {
        myLog.log(Level.INFO, "создание объекта параметров БД");
        this.typeBaseDate = typeBaseDate;
        properties = new Properties();
        fileName = null;
    }
    // ------------------------------------------------
    public BaseData.Status load() {
        return null;
    }
}
