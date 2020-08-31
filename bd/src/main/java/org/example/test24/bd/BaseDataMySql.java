package org.example.test24.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.TimeZone;

import static org.example.test24.bd.BaseData.*;

class BaseDataMySql extends BaseDataParent {
    public BaseDataMySql() {
        super();
    }
    // тестовое соединение
    @Override
    public int testConnectInit(BaseData.Parameters parameters) {
        testConnection = null;
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return DRIVER_ERROR;
        }
        // установка параметров соединения
        String connectionUrl = "jdbc:mysql://%1$s:%2$s";
        String connString = String.format(connectionUrl
                , parameters.ip
                , parameters.port
        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();
        // соединение
        try {
            Connection connection = DriverManager.getConnection(connString, parameters.login, parameters.password);
            testConnection = connection;
        } catch (SQLException e) {
            int stat;
            switch (e.getErrorCode()) {
                case 1045:
                    stat = CONNECT_PASS_ERROR;
                    break;
                case 1049:
                    stat = CONNECT_BASE_ERROR;
                    break;
                default:
                    stat = CONNECT_ERROR;
            }
            return stat;
        }
        testParameters = parameters;
        return OK;
    }
    // тестовое соединение список доступных баз
    @Override
    public String[] testConnectListBd() throws Exception {
        if (testConnection == null) {
            throw new Exception("BaseDataMySql.testConnectListBd: CONNECT_ERROR -> testConnection");
        }
        // запрос на список
        ResultSet resultSet;
        try {
            resultSet = testConnection.createStatement().executeQuery("SHOW DATABASES");
        } catch (SQLException throwables) {
            throw new Exception("BaseDataMySql.testConnectListBd: QUERY_ERROR -> " + throwables.getMessage());
        }
        // отсев системных БД
        ArrayList<String> listBd = new ArrayList<>();
        String s;
        try {
            while (resultSet.next()) {
                s = resultSet.getString(1);
                if (s.toLowerCase().equals("information_schema")) continue;
                if (s.toLowerCase().equals("mysql")) continue;
                if (s.toLowerCase().equals("performance_schema")) continue;
                if (s.toLowerCase().equals("sys")) continue;
                listBd.add(s);
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throw new Exception("BaseDataMySql.testConnectListBd: PARSING_ERROR -> " + throwables.getMessage());
        }
        return listBd.toArray(new String[0]);
    }
    // тестовое соединение проверка структуры БД
    @Override
    public int testConnectCheckStructure(String base) {
        if (testConnection == null) {
            //throw new Exception("BaseDataMySql.testConnectCheckStructure: CONNECT_ERROR -> testConnection");
        }
        boolean table1;
        String sample;
        PreparedStatement statement;
        ResultSet resultSet = null;
        ArrayList<String> listColumns = new ArrayList<>();
        int len, countList, countSql;
        {
            listColumns.clear();
            listColumns.add("id");
            listColumns.add("dateTime");
            listColumns.add("id_spec");
            listColumns.add("n_cicle");
            listColumns.add("ves");
            listColumns.add("tik_shelf");
            listColumns.add("tik_back");
            listColumns.add("tik_stop");
            listColumns.add("dis");
            // запрос
            countSql = 0;
            countList = listColumns.size();
            try {
                statement = testConnection.prepareStatement("SELECT\n" +
                        "COLUMN_NAME\n" +
                        "FROM information_schema.COLUMNS\n" +
                        "WHERE\tinformation_schema.COLUMNS.TABLE_SCHEMA = ?\n" +
                        "AND information_schema.COLUMNS.TABLE_NAME = ?\n" +
                        "ORDER BY information_schema.COLUMNS.ORDINAL_POSITION ASC"
                );
                statement.setString(1, base);
                statement.setString(2, "table_data");
                resultSet = statement.executeQuery();
            } catch (SQLException throwables) {
                return QUERY_ERROR;
            }
            try {
                while (resultSet.next()) {
                    sample = resultSet.getString(1);
                    countSql++;
                    len = listColumns.size();
                    if (len == 0) break;
                    for (int i = 0; i < len; i++) {
                        if (sample.equals(listColumns.get(i))) {
                            listColumns.remove(i);
                            break;
                        }
                    }
                }
            } catch (SQLException throwables) {
                return QUERY_ERROR;
            }
            table1 = countList == countSql;
            if (!table1)    return STRUCTURE_ERROR;
        } // table_data
        return OK;
    }
    // инициализация рабочего соединения
    @Override
    public int workConnectInit(Parameters parameters) {
        workConnection = null;
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return DRIVER_ERROR;
        }
        // установка параметров соединения
        String connectionUrl = "jdbc:mysql://%1$s:%2$s/%3$s";
        String connString = String.format(connectionUrl
                , parameters.ip
                , parameters.port
                , parameters.base
        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();
        // соединение
        try {
            Connection connection = DriverManager.getConnection(connString, parameters.login, parameters.password);
            workConnection = connection;
        } catch (SQLException e) {
            int stat;
            switch (e.getErrorCode()) {
                case 1045:
                    stat = CONNECT_PASS_ERROR;
                    break;
                case 1049:
                    stat = CONNECT_BASE_ERROR;
                    break;
                default:
                    stat = CONNECT_ERROR;
            }
            return stat;
        }
        workParameters = parameters;
        return OK;
    }
}
