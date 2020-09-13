package org.example.test24.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class BaseDataParent implements BaseData {
    protected Connection connection;
    // открытие соединение с БД
    @Override
    public BaseData.Status createConnect(Parameters parameters) {
        return null;
    }
    // чтение списка БД
    @Override
    public String[] getListBase() throws Exception {
        return new String[0];
    }
    // чтение списка пользователей
    @Override
    public UserClass[] getListUsers(boolean actual) throws Exception {
        if (connection == null) {
            myLog.log(Level.SEVERE, "отсутствует соединение");
            throw new Exception("отсутствует соединение (connection == null)");
        }
        boolean flClosed;
        try {
            flClosed = connection.isClosed();
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка проверки соединения: " + e.getMessage());
            throw new Exception(e);
        }
        if (flClosed) {
            myLog.log(Level.SEVERE, "соединение закрыто");
            throw new Exception("соединение закрыто");
        }
        ArrayList<UserClass> listUsers = new ArrayList<>();
        Statement statement = null;
        ResultSet result = null;
        // запрос на список пользователей
        try {
            String tab = "table_users";
            statement = connection.createStatement();
            // запрос
            if (actual) {
                result = statement.executeQuery(
                        "SELECT id, date_reg, date_unreg, name, password, rang " +
                                "FROM " + tab + " " +
                                "WHERE (date_unreg IS NULL) " +
                                "ORDER BY id "
                );
            } else {
                result = statement.executeQuery(
                        "SELECT id, date_reg, date_unreg, name, password, rang " +
                                "FROM " + tab + " " +
                                "ORDER BY id "
                );
            }
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка выполнения запроса", e);
            throw new Exception(e);
        }
        // создание списка
        try {
            while (result.next()) {
                String pass;
                // пароль
                try {
                    pass = BaseData.Password.decoding(result.getString("password"));
                } catch (IllegalArgumentException e) {
                    myLog.log(Level.SEVERE, "ошибка декодирования пароля", e);
                    continue;
                } catch (SQLException e) {
                    myLog.log(Level.SEVERE, "ошибка парсинга", e);
                    continue;
                }
                try {
                    listUsers.add(
                            new UserClass(
                                    result.getInt("id"),
                                    result.getTimestamp("date_reg"),
                                    result.getTimestamp("date_unreg"),
                                    result.getString("name"),
                                    pass,
                                    result.getInt("rang") // user status
                            )
                    );
                } catch (SQLException e) {
                    myLog.log(Level.SEVERE, "ошибка парсинга", e);
                    continue;
                }
            }
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "ошибка парсинга запроса", e);
            throw new Exception(e);
        }
        // закрытие соединения
        try {
            result.close();
            statement.close();
        } catch (SQLException e) {
            myLog.log(Level.WARNING, "ошибка закрытие соединения", e);
        }
        return listUsers.toArray(new UserClass[0]);
    }
}
