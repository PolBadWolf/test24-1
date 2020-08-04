package org.example.bd;

import java.sql.*;
import java.util.Date;

public class BdWork {
    ParametersSql parametersSql = null;
    private final String fileNameProperties = "sql1.txt";
    private Connection connection = null;
    private Sql_interface sql_interface = null;

    public BdWork(String typeDb) throws SQLException {
//        parametersSql = new ParametersSql(fileNameProperties);
        switch (typeDb) {
            case "MS_SQL":
                sql_interface = new Ms_sql();
                break;
            case  "MY_SQL":
                sql_interface = new My_sql();
                break;
            default:
                throw new SQLException("неизвестный тип BD");
        }
    }

    public Connection getConnect() {
        /*if (connection == null) connectBd();
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
        if (flag)   connection = null;*/
        return connection;
    }

    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) {
        sql_interface.pushDataDist(date, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, distance);
        /*if (getConnect() == null) {
            // нет связи
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(
                    //"BEGIN TRANSACTION\n"
                    "INSERT INTO Table_1 (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, dis)\n"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)\n"
                    //+ "COMMIT"
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
        }*/
    }

    private void connectBd() {
/*        parametersSql.load();
//        String connectionUrl = "jdbc:%1$s://%2$s:%3$s;databaseName=%4$s;user=%5$s;password=%6$s;";
        String connectionUrl = "jdbc:%1$s://%2$s:%3$s;databaseName=%4$s";
//        String connectionUrl = "jdbc:%1$s://%2$s:%3$s/%4$s";
        String connString = String.format(connectionUrl
                , parametersSql.typeBd
                , parametersSql.urlServer
                , parametersSql.portServer
                , parametersSql.dataBase
//                , parametersSql.user
//                , parametersSql.password
//        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=%2B8";
        );// + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            Class.forName(parametersSql.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
//            connection = DriverManager.getConnection(connString);
            connection = DriverManager.getConnection(connString, parametersSql.user, parametersSql.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

}
