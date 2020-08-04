package org.example.bd;

import java.sql.*;
import java.util.Date;

class Ms_sql implements Sql_interface {
    private final String fileNameProperties = "ms_sql.txt";
    private ParametersSql parametersSql = null;
    private Connection connection = null;

    public Ms_sql() {
        parametersSql = new ParametersSql(fileNameProperties, "MS_SQL");
    }

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) {
        if (getConnect() == null) {
            // нет связи
            return;
        }
        PreparedStatement statement = null;
        try {
            boolean saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.prepareStatement(
                    "INSERT INTO Table_Data (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, dis)\n"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
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
            connection.commit();
            connection.setAutoCommit(saveAutoCommit);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        String connectionUrl = "jdbc:sqlserver://%1$s:%2$s;databaseName=%3$s";
        String connString = String.format(connectionUrl
                , parametersSql.urlServer
                , parametersSql.portServer
                , parametersSql.dataBase
        );
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(connString, parametersSql.user, parametersSql.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
