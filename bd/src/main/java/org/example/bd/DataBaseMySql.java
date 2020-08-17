package org.example.bd;

import java.sql.*;
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

    static boolean testStuctBase1(String ip, String portServer, String login, String password, String base) {
        // init vars
        ArrayList<String> listColmn = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        int len, countList, countSql;
        boolean table1 = true;
        boolean table2 = true;
        boolean table3 = true;
        String sample;
        // check connect
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://%1$s:%2$s/%3$s";
            String connString = String.format(connectionUrl
                    , ip
                    , portServer
                    , base
            )  + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();
            connection = DriverManager.getConnection(connString, login, password);
            statement = connection.createStatement();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            return false;
        }
        // check table data
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
            resultSet = statement.executeQuery("SELECT\n" +
                    "COLUMN_NAME\n" +
                    "FROM information_schema.COLUMNS\n" +
                    "WHERE\tinformation_schema.COLUMNS.TABLE_SCHEMA = \"spc1\"\n" +
                    "AND information_schema.COLUMNS.TABLE_NAME = \"table_data\"\n" +
                    "ORDER BY information_schema.COLUMNS.ORDINAL_POSITION ASC");
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
