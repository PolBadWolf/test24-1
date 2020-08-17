package org.example.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBaseMsSql extends DataBase {

    @Override
    protected void setParametersSql(String[] fileNameSql) {
        parametersSql = new ParametersSql(fileNameSql[0], "MS_SQL");
    }

    static String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        // подключение драйвера
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        // установка параметров соединения
        String connectionUrl = "jdbc:sqlserver://%1$s:%2$s";
        String connString = String.format(connectionUrl
                , ip
                , portServer
        );
        // соединение и запрос на список
        try {
            connection = DriverManager.getConnection(connString, login, password);
            rs = connection.createStatement().executeQuery("SELECT name FROM sys.databases");
        } catch (SQLException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        // фильтр - отсеять системные bd
        ArrayList<String> listBd = new ArrayList<>();
        String s;
        while (rs.next()) {
            s = rs.getString(1);
            if (s.toLowerCase().equals("master"))   continue;
            if (s.toLowerCase().equals("tempdb"))   continue;
            if (s.toLowerCase().equals("model"))   continue;
            if (s.toLowerCase().equals("msdb"))   continue;
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
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        // установка параметров соединения
        String connectionUrl = "jdbc:sqlserver://%1$s:%2$s;databaseName=%3$s";
        String connString = String.format(connectionUrl
                , parametersSql.urlServer
                , parametersSql.portServer
                , parametersSql.dataBase
        );
        // соединение
        try {
            connection = DriverManager.getConnection(connString, parametersSql.user, parametersSql.password);
        } catch (SQLException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
    }
}
