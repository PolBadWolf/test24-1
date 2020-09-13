package org.example.test24.bd;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class BaseDataMySql extends BaseDataParent {
    public BaseDataMySql() {
        super();
    }
    // открытие соединение с БД
    @Override
    public BaseData.Status createConnect(Parameters parameters) {
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            myLog.log(Level.SEVERE, "ошибка подключения драйвера", e);
            return Status.CONNECT_DRIVER_ERROR;
        }
        // установка параметров соединения
        // подключение без БД
        String connectionUrl = "jdbc:mysql://%1$s:%2$s";
        String connString = String.format(connectionUrl
                , parameters.getIpServer()
                , parameters.getPortServer()
        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();
        // соединение
        try {
            connection = DriverManager.getConnection(
                    connString,
                    parameters.getUser(),
                    parameters.getPassword()
            );
        } catch (SQLException e) {
            BaseData.Status stat;
            switch (e.getErrorCode()) {
                case 1045:
                    myLog.log(Level.SEVERE, "ошибка пароля при подключении к БД", e);
                    stat = BaseData.Status.CONNECT_PASS_ERROR;
                    break;
                case 1049:
                    myLog.log(Level.SEVERE, "ошибка подключения к БД", e);
                    stat = BaseData.Status.CONNECT_BASE_ERROR;
                    break;
                default:
                    myLog.log(Level.SEVERE, "ошибка подключения к БД", e);
                    stat = BaseData.Status.CONNECT_ERROR;
            }
            return stat;
        }
        return Status.OK;
    }
    // чтение списка БД
    @Override
    public String[] getListBase() throws Exception {
        if (connection == null) {
            myLog.log(Level.SEVERE, "отсутствует соединение");
            throw new Exception("отсутствует соединение (connection == null)");
        }
        boolean flClosed;
        try {
            flClosed = connection.isClosed();
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка проверки соединения: " + e.getMessage());
            throw new Exception(e);
        }
        if (flClosed) {
            myLog.log(Level.SEVERE, "соединение закрыто");
            throw new Exception("соединение закрыто");
        }
        // запрос на список
        ResultSet resultSet;
        try {
            resultSet = connection.createStatement().executeQuery("SHOW DATABASES");
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка запроса", e);
            throw new Exception(e);
        }
        // отсев системных БД
        ArrayList<String> list = new ArrayList<>();
        String s;
        try {
            while (resultSet.next()) {
                s = resultSet.getString(1);
                if (s.toLowerCase().equals("information_schema")) continue;
                if (s.toLowerCase().equals("mysql")) continue;
                if (s.toLowerCase().equals("performance_schema")) continue;
                if (s.toLowerCase().equals("sys")) continue;
                list.add(s);
            }
            resultSet.close();
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка парсинга", e);
            throw new Exception(e);
        }
        return list.toArray(new String[0]);
    }
}
