package org.example.bd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class BdWork {
    ParametersSql parametersSql = null;
    private final String fileNameProperties = "sql.txt";
    private Connection connection = null;

    public BdWork() {
        parametersSql = new ParametersSql(fileNameProperties);
    }

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

    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) {
        if (getConnect() == null) {
            // нет связи
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("BEGIN TRANSACTION\n" +
                    "INSERT INTO Table_1 (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, dis)\n" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)\n" +
                    "COMMIT");
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
        }
    }

    private void connectBd() {
        parametersSql.load();
        String connectionUrl = "jdbc:%1$s://%2$s:%3$s;databaseName=%4$s;user=%5$s;password=%6$s;";
        String connString = String.format(connectionUrl,
                parametersSql.typeBd,
                parametersSql.urlServer,
                parametersSql.portServer,
                parametersSql.dataBase,
                parametersSql.user,
                parametersSql.password);
        try {
            Class.forName(parametersSql.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(connString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
