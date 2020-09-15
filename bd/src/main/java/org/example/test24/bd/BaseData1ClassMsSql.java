package org.example.test24.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class BaseData1ClassMsSql extends BaseData1Class {

    @Override
    public void setParametersSql(String[] fileNameSql) {
        parametersSql = new ParametersSql2(fileNameSql[0], BaseData2.TypeBaseData.MS_SQL);
    }

    @Override
    public String getTypeBD() {
        return "MS_SQL";
    }

    static String[] getConnectListBd1(String ip, String portServer, String login, String password) throws Exception {
        Connection connection;
        ResultSet rs;
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
        return listBd.toArray(new String[0]);
    }

    static boolean testStuctBase1(String ip, String portServer, String login, String password, String base) {
        // init vars
        ArrayList<String> listColmn = new ArrayList<>();
        Connection connection;
        ResultSet resultSet;
        Statement statement;
        int len, countList, countSql;
        boolean table1 = true;
        boolean table2 = true;
        boolean table3 = true;
        String sample;
        // check connect
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
        } catch (java.lang.Throwable e) {
            System.out.println("test structure base: " + e.getLocalizedMessage());
            return false;
        }
        // check table data
        try {
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
            countSql = 0;
            countList = listColmn.size();
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
            table1 = countList == countSql;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            table1 = false;
        }
        // check table users
        try {
            {
                listColmn.clear();
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
            countSql = 0;
            countList = listColmn.size();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            table2 = false;
        }

        return table1 && table2 && table3;
    }

    @Override
    public void connectBd() throws Exception {
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

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception {
        // проверка связи
        if (getConnect() == null) {
            throw new Exception("нет связи");
        }
        PreparedStatement statement;
        Statement statementReadSpec;
        boolean saveAutoCommit = false;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            // чтение id спец
            statementReadSpec = connection.createStatement();
            ResultSet resultSpec = statementReadSpec.executeQuery("SELECT TOP 1 table_spec.id FROM table_spec ORDER BY table_spec.id DESC");
            if (!resultSpec.next()) {
                throw new SQLException("таблица table_spec пуста");
            }
            id_spec = resultSpec.getLong(1);
            // запись
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
            connection.rollback();
            connection.setAutoCommit(saveAutoCommit);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void updateUserPassword(UserClass userClass, String newPassword) throws Exception {
        // проверка связи
        if (getConnect() == null) {
            throw new Exception("нет связи");
        }
        String pass = new String(java.util.Base64.getEncoder().encode(newPassword.getBytes()));
        PreparedStatement statement;
        boolean saveAutoCommit = true;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(true);
            // запись
            statement = connection.prepareStatement(
                    "UPDATE Table_users SET  \"password\" = ? WHERE \"id\" = ?"
            );
            statement.setString(1, pass);
            statement.setInt(2, userClass.id);
            statement.executeUpdate();
            statement.close();
        } catch (java.lang.Throwable ex) {
            ex.printStackTrace();
        }
        try {
            connection.setAutoCommit(saveAutoCommit);
        } catch (java.lang.Throwable ex) {
            ex.printStackTrace();
        }
    }
}
