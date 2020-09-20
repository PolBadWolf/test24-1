package org.example.test24.bd;

import com.mysql.cj.jdbc.ClientPreparedStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class BaseDataParent implements BaseData {
    protected Connection connection;
    protected String baseDat;
    // ===================================================================================================
    // открытие соединение с БД
    @Override
    public void openConnect(Parameters parameters) throws Exception { }
    // ===================================================================================================
    // чтение списка БД
    @Override
    public String[] getListBase() throws Exception { return new String[0]; }
    // ===================================================================================================
    // чтение списка пользователей
    @Override
    public User[] getListUsers(boolean actual) throws Exception {
        if (connection == null) throw new Exception("отсутствует соединение (connection == null)");
        boolean flClosed = connection.isClosed();
        if (flClosed) throw new Exception("соединение закрыто");

        ArrayList<User> listUsers = new ArrayList<>();
        Statement statement;
        ResultSet result;
        // запрос на список пользователей
        String tab = "table_users";
        statement = connection.createStatement();
        // запрос
        if (actual) {
            result = statement.executeQuery(
                    "SELECT" +
                            " table_users.id_user, " +
                            " logger_users.id_loggerUser, " +
                            " logger_users.date, " +
                            " logger_users.name, " +
                            " logger_users.password, " +
                            " logger_users.rang, " +
                            " table_users.date_unreg " +
                            " FROM " +
                            " " + baseDat + ".logger_users " +
                            " INNER JOIN " +
                            " " + baseDat + ".table_users" +
                            " ON  " +
                            " logger_users.id_loggerUser = table_users.id_loggerUser " +
                            " WHERE " +
                            " table_users.date_unreg IS NULL " +
                            " ORDER BY " +
                            " name ASC "
            );
        } else {
            result = statement.executeQuery(
                    "SELECT" +
                            " table_users.id_user, " +
                            " logger_users.id_loggerUser, " +
                            " logger_users.date, " +
                            " logger_users.name, " +
                            " logger_users.password, " +
                            " logger_users.rang, " +
                            " table_users.date_unreg " +
                            " FROM " +
                            " " + baseDat + ".logger_users " +
                            " INNER JOIN " +
                            " " + baseDat + ".table_users" +
                            " ON  " +
                            " logger_users.id_loggerUser = table_users.id_loggerUser " +
                            " ORDER BY " +
                            " name ASC "
            );
        }
        // создание списка
        while (result.next()) {
            String pass;
            // пароль
            try {
                pass = BaseData.Password.decoding(result.getString("password"));
            } catch (Exception e) {
                myLog.log(Level.SEVERE, "ошибка декодирования пароля", e);
                continue;
            }
            try {
                listUsers.add(
                        new User(
                                result.getInt("id_user"),
                                result.getTimestamp("date"),
                                result.getInt("id_loggerUser"),
                                result.getString("name"),
                                pass,
                                result.getInt("rang"),
                                result.getTimestamp("date_unreg")
                        )
                );
            } catch (SQLException e) {
                myLog.log(Level.SEVERE, "ошибка парсинга", e);
                continue;
            }
        }
        // закрытие соединения
        try {
            result.close();
            statement.close();
        } catch (SQLException e) {
            myLog.log(Level.WARNING, "ошибка закрытие соединения", e);
        }
        return listUsers.toArray(new User[0]);
    }
    // ===================================================================================================
    // проверка структуры БД
    @Override
    public boolean checkCheckStructureBd(String base) throws Exception {
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        boolean table_data, table_spec;
        boolean table_users, logger_users;
        boolean table_pushers, logger_pushers;
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
                        "id_user",
                        "date_reg",
                        "id_loggerUser",
                        "date_unreg"
                ))
        );
        logger_users = checkCheckStructureTable(
                base,
                "logger_users",
                new ArrayList(Arrays.asList(
                        "id_loggerUser",
                        "date",
                        "id_loggerUserEdit",
                        "id_user",
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
                        "name",
                        "id_unreg"
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
    // ===================================================================================================
    // установка нового пароля пользователю
    @Override
    public void setNewUserPassword(User user, String newPassword) throws Exception {
        if (connection == null) { throw new Exception("соединение не установлено"); }
        boolean fl = connection.isClosed();
        if (fl) { throw new Exception("соединение закрыто"); }
        if (user == null) { throw new Exception("пользователь null"); }

        boolean saveAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUserUpd;

        try {
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            String pass = BaseData.Password.encoding(newPassword);
            //
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".logger_users " +
                            " (date, id_loggerUserEdit, id_user, name, password, rang) " +
                            " VALUES (?, ?, ?, ?, ?, ?)"
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, user.id_loggerUser);
            preStatementLogger.setLong(3, user.id_user);
            preStatementLogger.setString(4, user.name);
            preStatementLogger.setString(5, pass);
            preStatementLogger.setInt(6, user.rang);
            preStatementLogger.executeUpdate();
            //
            preStatementUserUpd = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".table_users " +
                            " SET " +
                            " id_loggerUser = ? " +
                            " WHERE id_user = ? "
            );
            preStatementUserUpd.setLong(1, ((ClientPreparedStatement)preStatementLogger).getLastInsertID());
            preStatementUserUpd.setLong(2, user.id_user);
            preStatementUserUpd.executeUpdate();
            connection.commit();
            user.id_loggerUser = ((ClientPreparedStatement)preStatementLogger).getLastInsertID();
        } catch (SQLException e) {
            connection.rollback();
            try {
                connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException se) { }
            throw new Exception(e);
        }
        try {
            connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException se) { }
        preStatementUserUpd.close();
        preStatementLogger.close();
    }
    // ===================================================================================================
    // чтение списка толкателей
    @Override
    public Pusher[] getListPushers(boolean actual) throws Exception {
        if (1==1) throw new Exception("НЕ РЕАЛИЗОВАНО !!!!!!!!!!!!!!!!!!");
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        Statement statement;
        ResultSet result;
        // запрос на список толкателей
        String tab = "table_pushers";
        statement = connection.createStatement();
        // запрос
        if (actual) {
            result = statement.executeQuery(
                    "SELECT id_pusher, date_reg, date_unreg, name\n" +
                            "FROM " + baseDat + "." + tab + "\n" +
                            "WHERE (date_unreg IS NULL)\n" +
                            "ORDER BY id_pusher "
            );
        } else {
            result = statement.executeQuery(
                    "SELECT id_pusher, date_reg, date_unreg, name\n" +
                            "FROM " + baseDat + "." + tab + "\n" +
                            "ORDER BY id_pusher "
            );
        }
        // создание списка
        ArrayList<Pusher> listPusher = new ArrayList<>();
        while (result.next()) {
            listPusher.add(new Pusher(
                    result.getInt("id_pusher"),
                    result.getTimestamp("date_reg"),
                    result.getTimestamp("date_unreg"),
                    result.getString("name")
            ));
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException e) {
            myLog.log(Level.SEVERE, "чтение списка толкателей", e);
        }
        return listPusher.toArray(new Pusher[0]);
    }
    // ===================================================================================================
    // запись нового пользователя
    @Override
    public void writeNewUser(long id_loggerUserEdit, String surName, String password, int rang) throws Exception {
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        boolean saveAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement preStatementUser = null;
        PreparedStatement preStatementLogger = null;
        PreparedStatement preStatementUserUpd = null;
        try {
            String pass = new String(java.util.Base64.getEncoder().encode(password.getBytes()));
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            //
            preStatementUser = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".table_users " +
                            " (date_reg, id_loggerUser) " +
                            " VALUES (?, ?) "
            );
            preStatementUser.setTimestamp(1, timestamp);
            preStatementUser.setInt(2, 0);
            preStatementUser.executeUpdate();
            //
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".logger_users " +
                            " (date, id_loggerUserEdit, id_user, name, password, rang) " +
                            " VALUES (?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3, ((ClientPreparedStatement)preStatementUser).getLastInsertID());
            preStatementLogger.setString(4, surName);
            preStatementLogger.setString(5, pass);
            preStatementLogger.setInt(6, rang);
            preStatementLogger.executeUpdate();
            //
            preStatementUserUpd = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".table_users " +
                            " SET " +
                            " id_loggerUser = ? " +
                            " WHERE id_user = ? "
            );
            preStatementUserUpd.setLong(1, ((ClientPreparedStatement)preStatementLogger).getLastInsertID() );
            preStatementUserUpd.setLong(2, ((ClientPreparedStatement)preStatementUser).getLastInsertID() );
            preStatementUserUpd.executeUpdate();
            //
            connection.commit();
        } catch (SQLException throwables) {
            connection.rollback();
            try {
                connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException se) { }
            throw new Exception(throwables);
        }
        try {
            connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException se) { }
        preStatementUserUpd.close();
        preStatementUser.close();
        preStatementLogger.close();
    }
    // деактивация пользователя
    @Override
    public void deativateUser(long id_edit, User user) throws Exception {
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        boolean saveAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUser;

        try {
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            String pass = BaseData.Password.encoding(user.password);
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            baseDat + ".logger_users (date, id_userEdit, id_user, name, password, rang) "
                            + " VALUES (?, ?, ?, ?, ?, ?)"
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_edit);
            preStatementLogger.setLong(3, user.id_user);
            preStatementLogger.setString(4, user.name);
            preStatementLogger.setString(5, pass);
            preStatementLogger.setInt(6, user.rang);
            preStatementLogger.executeUpdate();
            //
            preStatementUser = connection.prepareStatement(
                    "UPDATE " +
                            baseDat + ".table_users " +
                            "SET " +
                            "id_loggerUser = ?, " +
                            "date_unreg = ? " +
                            "WHERE id_user = ? "
            );
            preStatementUser.setLong(1, ((ClientPreparedStatement)preStatementLogger).getLastInsertID());
            preStatementUser.setTimestamp(2, timestamp);
            preStatementUser.setLong(3, user.id_user);
            preStatementUser.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            try {
                connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException se) { }
            throw new Exception(e);
        }
        try {
            connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException se) { }
        preStatementUser.close();
        preStatementLogger.close();
    }
    // обновление данных о пользователе
    @Override
    public void updateDataUser(int sourceId, int targetId, String surName, String password, int rang, User editUser) {
        /*if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        boolean saveAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement preStatementUser = null;
        PreparedStatement preStatementLogger = null;

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".logger_users (date, idUser_edit, act, idUser, " +
                            "name_old, name_new, " +
                            "password_old, password_new, " +
                            "rang_old, rang_new) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setInt(2, sourceId);
            preStatementLogger.setInt(3, 3);
            preStatementLogger.setLong(4,  targetId); // ((ClientPreparedStatement)preStatementUser).getLastInsertID());
            preStatementLogger.setString(5, editUser.name);
            preStatementLogger.setString(6, surName);
            preStatementLogger.setString(7, BaseData.Password.encoding(editUser.password));
            preStatementLogger.setString(8, BaseData.Password.encoding(password));
            preStatementLogger.setInt(9, editUser.rang);
            preStatementLogger.setInt(10, rang);
            preStatementLogger.executeUpdate();
            //
            preStatementUser = connection.prepareStatement(
                    "UPDATE " + baseDat + ".Table_users SET " +
                            "date_unreg = ?, " +
                            "id_unreg = ? " +
                            "WHERE id = ? "
            );
            preStatementUser.setTimestamp(1, timestamp);
            //preStatementUser.setInt(2, source_id);
            //preStatementUser.setInt(3, target_id);
            preStatementUser.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

         */
    }
}
