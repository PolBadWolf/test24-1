package org.example.bd;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.util.TimeUtil;

import java.sql.*;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

class My_sql implements Sql_interface {
    private final String fileNameProperties = "my_sql.txt";
    private ParametersSql parametersSql = null;
    private Connection connection = null;

    public My_sql() {
        parametersSql = new ParametersSql(fileNameProperties, "MY_SQL");
    }

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) {
        if (getConnect() == null) {
            // нет связи
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(
                    "START TRANSACTION\n"
                    + "INSERT INTO Table_Data (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, dis)\n"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)\n"
                    + "COMMIT;"
            );
            statement.setTimestamp(1, new java.sql.Timestamp(date.getTime()) );
            statement.setLong(2, id_spec);
            statement.setInt(3, n_cicle);
            statement.setInt(4, ves);
            statement.setInt(5, tik_shelf);
            statement.setInt(6, tik_back);
            statement.setInt(7, tik_stop);
            statement.setBlob(8, distance);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public Connection getConnect() {
        if (connection == null) connectBd();
        else {
            try {
                if (connection.isClosed())  connectBd();
            } catch (SQLException e) {
                e.printStackTrace();
                connection = null;
            }
        }
        boolean flag = true;
        try {
            flag = connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            connection = null;
        }
        if (flag)   connection = null;
        return connection;
    }

    private void connectBd() {
        try {
            parametersSql.load();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String connectionUrl = "jdbc:mysql://%1$s:%2$s/%3$s";
        String connString = String.format(connectionUrl
                , parametersSql.urlServer
                , parametersSql.portServer
                , parametersSql.dataBase
        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            connection = DriverManager.getConnection(connString, parametersSql.user, parametersSql.password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
