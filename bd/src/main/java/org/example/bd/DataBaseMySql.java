package org.example.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimeZone;

public class DataBaseMySql extends DataBase {

    @Override
    protected void setParametersSql(String[] fileNameSql) {
        parametersSql = new ParametersSql(fileNameSql[1], "MY_SQL");
    }

    static String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        // установка параметров соединения
        String connectionUrl = "jdbc:mysql://%1$s:%2$s";
        String connString = String.format(connectionUrl
                , ip
                , portServer
        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();
        // соединение и запрос на список
        try {
            connection = DriverManager.getConnection(connString, login, password);
            rs = connection.createStatement().executeQuery("SHOW DATABASES");
        } catch (SQLException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        // фильтр - отсеять системные bd
        ArrayList<String> listBd = new ArrayList<>();
        String s;
        while (rs.next()) {
            s = rs.getString(1);
            if (s.toLowerCase().equals("information_schema"))   continue;
            if (s.toLowerCase().equals("mysql"))   continue;
            if (s.toLowerCase().equals("performance_schema"))   continue;
            if (s.toLowerCase().equals("sys"))   continue;
            listBd.add(s);
        }
        rs.close();
        connection.close();
        return listBd.toArray(new String[listBd.size()]);
    }

    @Override
    protected void connectBd() throws Exception {
        // загрузка параметров
        try {
            parametersSql.load();
        } catch (Exception e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        // установка параметров соединения
        String connectionUrl = "jdbc:mysql://%1$s:%2$s/%3$s";
        String connString = String.format(connectionUrl
                , parametersSql.urlServer
                , parametersSql.portServer
                , parametersSql.dataBase
        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();
        // соединение
        try {
            connection = DriverManager.getConnection(connString, parametersSql.user, parametersSql.password);
        } catch (SQLException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
    }
}
