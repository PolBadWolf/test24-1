package org.example.test24.bd;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class BaseDataMySql extends BaseDataParent {
    public BaseDataMySql() {
        super();
    }
    // открытие соединение с БД
    @Override
    public void openConnect(Parameters parameters) throws BaseDataException {
        // подключение драйвера
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new BaseDataException("ошибка подключения драйвера", e, Status.CONNECT_DRIVER_ERROR);
        }
        // установка параметров соединения
        String connectionUrl = "jdbc:mysql://%1$s:%2$s";
        String connString = String.format(connectionUrl
                , parameters.getIpServer()
                , parameters.getPortServer()
        ) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();
        // соединение
        try {
            connection = DriverManager.getConnection(
                    connString,
                    parameters.getUser(),
                    parameters.getPassword()
            );
        } catch (SQLException e) {
            Status status;
            switch (e.getErrorCode()) {
                case 1045:
                    status = Status.CONNECT_PASS_ERROR;
                    break;
                case 1049:
                    status = Status.CONNECT_BASE_ERROR;
                    break;
                default:
                    status = Status.CONNECT_ERROR;
            }
            throw new BaseDataException("установка соединения", e, status);
        }
        this.baseDat = parameters.getDataBase();
    }
    // чтение списка БД
    @Override
    public String[] getListBase() throws BaseDataException {
        if (connection == null) {
            throw new BaseDataException("отсутствует соединение (connection == null)", Status.CONNECT_NO_CONNECTION);
        }
        boolean flClosed;
        try {
            flClosed = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("отсутствует соединение (error)", e, Status.CONNECT_NO_CONNECTION);
        }
        if (flClosed) {
            throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);
        }
        // запрос на список
        ResultSet resultSet = null;
        ArrayList<String> list = new ArrayList<>();
        try {
            resultSet = connection.createStatement().executeQuery("SHOW DATABASES");
            // отсев системных БД
            String s;
            while (resultSet.next()) {
                s = resultSet.getString(1);
                if (s.toLowerCase().equals("information_schema")) continue;
                if (s.toLowerCase().equals("mysql")) continue;
                if (s.toLowerCase().equals("performance_schema")) continue;
                if (s.toLowerCase().equals("sys")) continue;
                list.add(s);
            }
        } catch (SQLException e) {
            if (list.size() == 0) {
                throw new BaseDataException(e, Status.CONNECT_ERROR);
            }
            myLog.log(Level.SEVERE, "ошибка парсинга", e);
        }
        try {
            resultSet.close();
        } catch (SQLException e) {
            myLog.log(Level.WARNING, "чтение списка БД", e);
        }
        return list.toArray(new String[0]);
    }
    // проверка структуры таблицы
    protected boolean checkCheckStructureTable(String base, String table, ArrayList<String> listColumns) {
        PreparedStatement statement;
        ResultSet resultSet;
        String sample;
        int countColumns = 0, sizeColumns = listColumns.size(), len;
        try {
            statement = connection.prepareStatement("SELECT\n" +
                    "COLUMN_NAME\n" +
                    "FROM information_schema.COLUMNS\n" +
                    "WHERE\tinformation_schema.COLUMNS.TABLE_SCHEMA = ?\n" +
                    "AND information_schema.COLUMNS.TABLE_NAME = ?\n" +
                    "ORDER BY information_schema.COLUMNS.ORDINAL_POSITION ASC"
            );
            statement.setString(1, base);
            statement.setString(2, table);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка проверки структуры таблицы", e);
            return false;
        }
        try {
            while (resultSet.next()) {
                countColumns++;
                sample = resultSet.getString(1);
                len = listColumns.size();
                if (len == 0) break;
                for (int i = 0; i < len; i++) {
                    if (listColumns.get(i).equals(sample)) {
                        listColumns.remove(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка проверки структуры таблицы", e);
            return false;
        }
        boolean stat = (countColumns == sizeColumns) && (listColumns.size() == 0);
        try {
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            myLog.log(Level.WARNING, "ошибка проверки структуры таблицы", e);
        }
        return stat;
    }
}
