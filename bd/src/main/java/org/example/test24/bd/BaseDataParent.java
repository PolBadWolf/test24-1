package org.example.test24.bd;

import com.mysql.cj.jdbc.ClientPreparedStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class BaseDataParent implements BaseData {
    protected Connection connection;
    protected String baseDat;
    // ===================================================================================================
    static BaseData create(Parameters parameters) throws BaseDataException {
        BaseData baseData;
        switch (parameters.getTypeBaseDate().getCodeTypeBaseData()) {
            case BaseData.TYPEBD_MYSQL:
                baseData = new BaseDataMySql();
                break;
            case BaseData.TYPEBD_MSSQL:
                baseData = new BaseDataMsSql();
                break;
            default:
                throw new BaseDataException("ошибка открытия БД - не верный тип БД", Status.CONNECT_BASE_TYPE_ERROR);
        }
        return baseData;
    }
    // ===================================================================================================
    // открытие соединение с БД
    @Override
    public void openConnect(Parameters parameters) throws BaseDataException { }
    // ===================================================================================================
    // чтение списка БД
    @Override
    public String[] getListBase() throws BaseDataException { return new String[0]; }
    // ===================================================================================================
    // чтение списка пользователей
    @Override
    public User[] getListUsers(boolean actual) throws BaseDataException {
        if (connection == null) throw new BaseDataException("отсутствует соединение (connection == null)", Status.CONNECT_NO_CONNECTION);
        boolean flClosed = false;
        try {
            flClosed = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("отсутствует соединение (connection == null)", e, Status.CONNECT_NO_CONNECTION);
        }
        if (flClosed) throw new BaseDataException("отсутствует соединение (connection == null)", Status.CONNECT_CLOSE);

        ArrayList<User> listUsers = new ArrayList<>();
        Statement statement;
        ResultSet result;
        // запрос на список пользователей
        String tab = "table_users";
        // запрос
        try {
            statement = connection.createStatement();
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
        } catch (SQLException e) {
            throw new BaseDataException(e, Status.CONNECT_ERROR);
        }
        // создание списка
        try {
            while (result.next()) {
                String pass;
                // пароль
                try {
                    pass = BaseData.Password.decoding(result.getString("password"));
                } catch (Exception e) {
                    myLog.log(Level.SEVERE, "ошибка декодирования пароля", e);
                    pass = null;
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
        } catch (SQLException e) {
            myLog.log(Level.WARNING, "ошибка парсинга", e);
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
    public boolean checkStructureBd(String base) throws BaseDataException {
        if (connection == null) throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION);
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);

        boolean table_data, table_spec;
        boolean table_users, logger_users;
        boolean table_pushers, logger_pushers;
        table_data = checkStructureTable(
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
        table_users = checkStructureTable(
                base,
                "table_users",
                new ArrayList(Arrays.asList(
                        "id_user",
                        "date_reg",
                        "id_loggerUser",
                        "date_unreg"
                ))
        );
        logger_users = checkStructureTable(
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
        table_pushers = checkStructureTable(
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
    protected boolean checkStructureTable(String base, String table, ArrayList<String> listColumns) {
        myLog.log(Level.SEVERE, "ошибка проверки таблицы");
        System.exit(-2);
        return false;
    }
    // ===================================================================================================
    // установка нового пароля пользователю
    @Override
    public void setNewUserPassword(User user, String newPassword) throws BaseDataException {
        if (connection == null) { throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION); }
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) { throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE); }
        if (user == null) { throw new BaseDataException("пользователь null", Status.PARAMETERS_ERROR); }

        boolean saveAutoCommit = true;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка соединения", e, Status.SQL_TRANSACTION_ERROR);
        }

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
            //
            connection.commit();
            user.id_loggerUser = ((ClientPreparedStatement)preStatementLogger).getLastInsertID();
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException se) {
                myLog.log(Level.SEVERE, "ошибка отмены транзакции", se);
                e = new SQLException("ошибка отмены транзакции: " + se.getStackTrace(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        }
        //
        try {
            connection.setAutoCommit(saveAutoCommit);
            preStatementUserUpd.close();
            preStatementLogger.close();
        } catch (SQLException se) { }
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
    public void writeNewUser(long id_loggerUserEdit, String surName, String password, int rang) throws BaseDataException {
        if (connection == null) throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION);
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);

        boolean saveAutoCommit = false;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

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
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException se) {
                e = new SQLException("ошибка закрытия транзакции", se);
            }
            throw new BaseDataException("ошибка транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        //
        try {
            connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException se) { }
        try {
            preStatementUserUpd.close();
            preStatementUser.close();
            preStatementLogger.close();
        } catch (SQLException throwables) { }
    }
    // ===================================================================================================
    // деактивация пользователя
    @Override
    public void deativateUser(long id_loggerUserEdit, User user) throws BaseDataException {
        if (connection == null) throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION);
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);

        boolean saveAutoCommit = false;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUserUpd;

        try {
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            String pass = BaseData.Password.encoding(user.password);
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".logger_users " +
                            " (date, id_loggerUserEdit, id_user, name, password, rang) " +
                            " VALUES (?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3, user.id_user);
            preStatementLogger.setString(4, user.name);
            preStatementLogger.setString(5, pass);
            preStatementLogger.setInt(6, user.rang);
            preStatementLogger.executeUpdate();
            //
            preStatementUserUpd = connection.prepareStatement(
                    "UPDATE " +
                            baseDat + ".table_users " +
                            "SET " +
                            "id_loggerUser = ?, " +
                            "date_unreg = ? " +
                            "WHERE id_user = ? "
            );
            preStatementUserUpd.setLong(1, ((ClientPreparedStatement)preStatementLogger).getLastInsertID());
            preStatementUserUpd.setTimestamp(2, timestamp);
            preStatementUserUpd.setLong(3, user.id_user);
            preStatementUserUpd.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException se) {
                e = new SQLException("ошибка закрытия транзакции", se);
            }
            throw new BaseDataException("ошибка транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        try { connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException se) { }
        try {
            preStatementUserUpd.close();
            preStatementLogger.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    // ===================================================================================================
    // обновление данных о пользователе
    @Override
    public void updateDataUser(long id_loggerUserEdit, User user, String surName, String password, int rang) throws BaseDataException {
        if (connection == null) throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION);
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);

        boolean saveAutoCommit = false;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

        PreparedStatement preStatementLogger = null;
        PreparedStatement preStatementUserUpd = null;

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".logger_users " +
                            " (date, id_loggerUserEdit, id_user, name, password, rang) " +
                            "VALUES (?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3,  user.id_user);
            preStatementLogger.setString(4, surName);
            preStatementLogger.setString(5, BaseData.Password.encoding(password));
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
            preStatementUserUpd.setLong(2, user.id_user );
            preStatementUserUpd.executeUpdate();
            //
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException se) {
                e = new SQLException("ошибка закрытия транзакции", se);
            }
            throw new BaseDataException("ошибка транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        //
        try { connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException se) { }
        try {
            preStatementUserUpd.close();
            preStatementLogger.close();
        } catch (SQLException e) { }
    }
    // запись измерений
    @Override
    public void writeDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws BaseDataException {

        myLog.log(Level.SEVERE, "сохранение данных замера", new Exception("СДЕЛАТЬ !!!!!!!!!!!"));
    }
}
