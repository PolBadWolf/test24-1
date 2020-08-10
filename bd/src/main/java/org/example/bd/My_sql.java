package org.example.bd;

import java.sql.*;
import java.util.Date;
import java.util.TimeZone;

class My_sql implements Sql_interface {
    private final String fileNameProperties = "my_sql.txt";
    private ParametersSql parametersSql = null;
    private Connection connection = null;

    public My_sql() {
        parametersSql = new ParametersSql(fileNameProperties, "MY_SQL");
    }

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception {
        if (getConnect() == null) {
            // нет связи
            return;
        }
        PreparedStatement statement = null;
        Statement statementReadSpec = null;
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statementReadSpec = connection.createStatement();
            ResultSet resultSpec = statementReadSpec.executeQuery("SELECT table_spec.id FROM table_spec ORDER BY\n" +
                    "table_spec.id DESC LIMIT 1");
            if (!resultSpec.next()) {
                throw new SQLException("таблица table_spec пуста");
            }

            statement = connection.prepareStatement(
                    "INSERT INTO Table_Data (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, dis)\n"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
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
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public Connection getConnect() throws Exception {
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

    @Override
    public String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        return new String[0];
    }

    private void connectBd() throws Exception {
        try {
            parametersSql.load();
        } catch (Exception e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
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
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }

        try {
            connection = DriverManager.getConnection(connString, parametersSql.user, parametersSql.password);
        } catch (SQLException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
    }

}
