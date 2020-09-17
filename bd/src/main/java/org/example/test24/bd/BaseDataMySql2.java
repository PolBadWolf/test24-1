package org.example.test24.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.function.Consumer;

import static org.example.test24.bd.BaseData2.*;

class BaseDataMySql2 extends BaseDataParent2 {
    public BaseDataMySql2() {
        super();
    }
    // тестовое соединение
    @Override
    public BaseData2.Status createTestConnect(BaseData2.Parameters parameters) {
        testConnection = null;
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return Status.DRIVER_ERROR;
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
            Status stat;
            switch (e.getErrorCode()) {
                case 1045:
                    stat = Status.CONNECT_PASS_ERROR;
                    break;
                case 1049:
                    stat = Status.CONNECT_BASE_ERROR;
                    break;
                default:
                    stat = Status.CONNECT_ERROR;
            }
            return stat;
        }
        testParameters = parameters;
        return Status.OK;
    }
    // тестовое соединение проверка структуры БД
    @Override
    public BaseData2.Status checkCheckStructureBd(String base) {
        if (testConnection == null) {
            return BaseData2.Status.CONNECT_ERROR;
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
                return BaseData2.Status.QUERY_ERROR;
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
                return BaseData2.Status.QUERY_ERROR;
            }
            table1 = countList == countSql;
            if (!table1)    return BaseData2.Status.STRUCTURE_ERROR;
        } // table_data
        return BaseData2.Status.OK;
    }
    // -----------------------------------------------------------
    // инициализация рабочего соединения
    @Override
    public BaseData2.Status createWorkConnect(Parameters parameters) {
        workConnection = null;
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return BaseData2.Status.DRIVER_ERROR;
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
            BaseData2.Status stat;
            switch (e.getErrorCode()) {
                case 1045:
                    stat = BaseData2.Status.CONNECT_PASS_ERROR;
                    break;
                case 1049:
                    stat = BaseData2.Status.CONNECT_BASE_ERROR;
                    break;
                default:
                    stat = BaseData2.Status.CONNECT_ERROR;
            }
            return stat;
        }
        workParameters = parameters;
        return BaseData2.Status.OK;
    }









    // тестовое соединение список доступных баз
    @Override
    public boolean requestListBdFrom(Consumer<String[]> list) {
        if (testConnection == null) {
            return false;
        }
        // запрос на список
        ResultSet resultSet;
        try {
            resultSet = testConnection.createStatement().executeQuery("SHOW DATABASES");
        } catch (SQLException throwables) {
            return false;
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
            return false;
        }
        if (list != null) {
            list.accept(listBd.toArray(new String[0]));
        }
        return true;
    }
    @Override
    public String[] getListBd() throws Exception {
        if (testConnection == null) { throw new Exception("Тестового соединение не установлено"); }
        try {
            if (testConnection.isClosed()) {
                throw new Exception("Тестового соединение не установлено");
            }
        } catch (SQLException e) {
            throw new Exception("Тестового соединение не установлено: " + e.getMessage());
        }
        // запрос на список
        ResultSet resultSet;
        try {
            resultSet = testConnection.createStatement().executeQuery("SHOW DATABASES");
        } catch (SQLException e) {
            throw new Exception("Ошибка выполнения запроса получения списка БД: " + e.getMessage());
        }
        ArrayList<String> list = new ArrayList<>();
        String s;
        while (resultSet.next()) {
            s = resultSet.getString(1);
            if (s.toLowerCase().equals("information_schema")) continue;
            if (s.toLowerCase().equals("mysql")) continue;
            if (s.toLowerCase().equals("performance_schema")) continue;
            if (s.toLowerCase().equals("sys")) continue;
            list.add(s);
        }
        return list.toArray(new String[0]);
    }
}