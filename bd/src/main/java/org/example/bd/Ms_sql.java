package org.example.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

class Ms_sql implements Sql_interface {
    private String fileNameProperties = null;
    private ParametersSql parametersSql = null;
    private Connection connection = null;

    public Ms_sql(String fileNameProperties) {
        this.fileNameProperties = fileNameProperties;
        parametersSql = new ParametersSql(fileNameProperties, "MS_SQL");
    }

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception {
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
        Connection connection = null;
        ResultSet rs = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
        String connectionUrl = "jdbc:sqlserver://%1$s:%2$s";
        String connString = String.format(connectionUrl
                , ip
                , portServer
        );
        try {
            connection = DriverManager.getConnection(connString, login, password);
            rs = connection.createStatement().executeQuery("SELECT name FROM sys.databases");
        } catch (SQLException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
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

    private void connectBd() throws Exception {
        try {
            parametersSql.load();
        } catch (Exception e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
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
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }

        try {
            connection = DriverManager.getConnection(connString, parametersSql.user, parametersSql.password);
        } catch (SQLException e) {
            throw new Exception(e.getLocalizedMessage().substring(0, e.getLocalizedMessage().lastIndexOf(".")));
        }
    }

    @Override
    public boolean testStuctBase(String ip, String portServer, String login, String password, String base) {
        ArrayList<String> listColmn = new ArrayList<>();
        {
            listColmn.add("id");
            listColmn.add("dateTime");
            listColmn.add("id_spec");
            listColmn.add("n_cicle");
            listColmn.add("ves");
            listColmn.add("tik_shelf");
            listColmn.add("tik_back");
            listColmn.add("tik_stop");
            listColmn.add("dis");
        }
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        int len, countList = listColmn.size(), countSql = 0;
        String sample;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionUrl = "jdbc:sqlserver://%1$s:%2$s;databaseName=%3$s";
            String connString = String.format(connectionUrl
                    , ip
                    , portServer
                    , base
            );
            connection = DriverManager.getConnection(connString, login, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = 'Table_Data'");
            while (resultSet.next()) {
                sample = resultSet.getString(1);
                countSql++;
                if ((len = listColmn.size()) == 0)  break;
                for(int i=0; i<len; i++) {
                    if (!sample.equals(listColmn.get(i)))  continue;
                    listColmn.remove(i);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return countList == countSql;
    }

    @Override
    public ParametersSql getParametrsSql() {
        return parametersSql;
    }
}
