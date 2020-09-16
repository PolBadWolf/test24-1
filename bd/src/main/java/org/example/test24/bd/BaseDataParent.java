package org.example.test24.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class BaseDataParent implements BaseData {
    protected Connection connection;
    protected String baseDat;
    // открытие соединение с БД
    @Override
    public void openConnect(Parameters parameters) throws Exception { }
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
                                "FROM " + baseDat + "." + tab + " " +
                                "WHERE (date_unreg IS NULL) " +
                                "ORDER BY id "
                );
            } else {
                result = statement.executeQuery(
                        "SELECT id, date_reg, date_unreg, name, password, rang " +
                                "FROM " + baseDat + "." + tab + " " +
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
    // проверка структуры БД
    @Override
    public boolean checkCheckStructureBd(String base) throws Exception {
        if (connection == null) {
            throw new Exception("соединение не установлено");
        }
        boolean fl = connection.isClosed();
        if (fl) {
            throw new Exception("соединение закрыто");
        }
        boolean table_data = true, table_users = true, table_pushers = true;

        table_data = checkCheckStructureTable(
                base,
                "table_data",
                new ArrayList(Arrays.asList(
                        "id",
                        "dateTime",
                        "id_spec",
                        "n_cicle",
                        "ves",
                        "tik_shelf",
                        "tik_back",
                        "tik_stop",
                        "dis"
                ))
        );
        table_users = checkCheckStructureTable(
                base,
                "table_users",
                new ArrayList(Arrays.asList(
                        "id",
                        "date_reg",
                        "date_unreg",
                        "name",
                        "password",
                        "rang"
                ))
        );
        table_pushers = checkCheckStructureTable(
                base,
                "table_pushers",
                new ArrayList(Arrays.asList(
                        "id_pusher",
                        "date_reg",
                        "date_unreg",
                        "name"
                ))
        );
        return table_data && table_users && table_pushers;
    }
    // проверка структуры таблицы
    protected boolean checkCheckStructureTable(String base, String table, ArrayList<String> listColumns) {
        myLog.log(Level.SEVERE, "ошибка проверки таблицы");
        System.exit(-2);
        return false;
    }
    // установка нового пароля пользователю
    @Override
    public void setNewUserPassword(UserClass user, String newPassword) throws Exception {
        if (connection == null) { throw new Exception("соединение не установлено"); }
        boolean fl = connection.isClosed();
        if (fl) { throw new Exception("соединение закрыто"); }
        if (user == null) { throw new Exception("пользователь null"); }
        PreparedStatement preparedStatement;
        int result;
        preparedStatement = connection.prepareStatement(
                "UPDATE " + baseDat + ".Table_users SET  password = ? WHERE id = ?"
        );
        preparedStatement.setString(1, BaseData2.Password.encoding(newPassword));
        preparedStatement.setInt(2, user.id);
        result  = preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
