package org.example.test24.bd;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import org.example.test24.bd.usertypes.*;

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
        switch (parameters.getTypeBaseDate()) {
            case MY_SQL:
                baseData = new BaseDataMySql();
                break;
            case MS_SQL:
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
        boolean flClosed;
        try {
            flClosed = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("отсутствует соединение (connection == null)", e, Status.CONNECT_NO_CONNECTION);
        }
        if (flClosed) throw new BaseDataException("отсутствует соединение (connection == null)", Status.CONNECT_CLOSE);

        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

        ArrayList<User> listUsers = new ArrayList<>();
        Statement statement;
        ResultSet result;
        // запрос на список пользователей
        String query;
        // запрос
        try {
            statement = connection.createStatement();
            if (actual) {
                query =
                        "SELECT " +
                                " table_users.id_user, " +
                                " table_users.date_reg, " +
                                " logger_users.id_loggerUser, " +
                                " logger_users.date_upd, " +
                                " logger_users.id_loggerUserEdit, " +
                                " logger_users.surName, " +
                                " logger_users.userPassword, " +
                                " logger_users.rang, " +
                                " table_users.date_unreg " +
                                " FROM " +
                                " " + baseDat + ".logger_users " +
                                " INNER JOIN " +
                                " " + baseDat + ".table_users " +
                                " ON " +
                                " logger_users.id_loggerUser = table_users.id_loggerUser " +
                                " WHERE " +
                                " table_users.date_unreg IS NULL " +
                                " ORDER BY " +
                                " logger_users.surName ASC "
                ;
            } else {
                query =
                        "SELECT " +
                                " table_users.id_user, " +
                                " table_users.date_reg, " +
                                " logger_users.id_loggerUser, " +
                                " logger_users.date_upd, " +
                                " logger_users.id_loggerUserEdit, " +
                                " logger_users.surName, " +
                                " logger_users.userPassword, " +
                                " logger_users.rang, " +
                                " table_users.date_unreg " +
                                " FROM " +
                                " " + baseDat + ".logger_users " +
                                " INNER JOIN " +
                                " " + baseDat + ".table_users " +
                                " ON " +
                                " logger_users.id_loggerUser = table_users.id_loggerUser " +
                                " ORDER BY " +
                                " logger_users.surName ASC "
                ;
            }
            result = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new BaseDataException(e, Status.CONNECT_ERROR);
        }
        // создание списка
        try {
            while (result.next()) {
                String pass;
                // пароль
                try {
                    pass = BaseData.Password.decoding(result.getString("userPassword"));
                } catch (Exception e) {
                    myLog.log(Level.SEVERE, "ошибка декодирования пароля", e);
                    pass = null;
                }
                try {
                    listUsers.add(
                            new User(
                                    result.getLong("id_user"),
                                    result.getTimestamp("date_reg"),
                                    result.getLong("id_loggerUser"),
                                    result.getTimestamp("date_upd"),
                                    result.getLong("id_loggerUserEdit"),
                                    result.getString("surName"),
                                    pass,
                                    result.getInt("rang"),
                                    result.getTimestamp("date_unreg")
                            ));
                } catch (Exception e) {
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

        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

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
                        "id_loggerPusher",
                        "date_unreg"
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
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUserUpd;

        try {
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            String pass = BaseData.Password.encoding(newPassword);
            //
            String query =
                    "INSERT INTO " +
                            " " + baseDat + ".logger_users " +
                            " (date_upd, id_loggerUserEdit, id_user, surName, userPassword, rang) " +
                            " VALUES (?, ?, ?, ?, ?, ?)"
                    ;
            preStatementLogger = connection.prepareStatement(query);
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, user.id_loggerUser);
            preStatementLogger.setLong(3, user.id_user);
            preStatementLogger.setString(4, user.surName);
            preStatementLogger.setString(5, pass);
            preStatementLogger.setInt(6, user.rang);
            preStatementLogger.executeUpdate();
            long id_loggerUser = ((ClientPreparedStatement)preStatementLogger).getLastInsertID();
            //
            preStatementUserUpd = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".table_users " +
                            " SET " +
                            " id_loggerUser = ? " +
                            " WHERE id_user = ? "
            );
            preStatementUserUpd.setLong(1, id_loggerUser);
            preStatementUserUpd.setLong(2, user.id_user);
            preStatementUserUpd.executeUpdate();
            //
            connection.commit();
            user.id_loggerUser = id_loggerUser;
            user.userPassword = pass;
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }
        //
        try {
            preStatementUserUpd.close();
            preStatementLogger.close();
        } catch (SQLException se) { }
    }
    // ===================================================================================================
    // чтение списка толкателей ****
    @Override
    public Pusher[] getListPushers(boolean actual) throws Exception {
        //if (1==1) throw new Exception("НЕ РЕАЛИЗОВАНО !!!!!!!!!!!!!!!!!!");
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        Statement statement;
        ResultSet result;
        // запрос на список толкателей
        String tab = "table_pushers";
        statement = connection.createStatement();
        // запрос
        String query;
        if (actual) {
            query =
                    "SELECT " +
                            " table_pushers.id_pusher, " +
                            " table_pushers.date_reg, " +
                            " logger_pushers.id_loggerPusher, " +
                            " logger_pushers.date_upd as date_upd_pusher, " +
                            " logger_pushers.id_loggerUserEdit as id_loggerUserEditPusher, " +
                            " logger_pushers.namePusher, " +
                            " logger_type_pushers.id_loggerTypePusher, " +
                            " logger_type_pushers.data_upd as data_upd_type, " +
                            " logger_type_pushers.id_loggerUserEdit as id_loggerUserEditType, " +
                            " logger_type_pushers.id_typePusher, " +
                            " logger_type_pushers.nameType, " +
                            " logger_type_pushers.forceNominal, " +
                            " logger_type_pushers.moveNominal, " +
                            " logger_type_pushers.unclenchingTime, " +
                            " table_pushers.date_unreg " +
                            " FROM " +
                            " " + baseDat + ".table_pushers " +
                            " INNER JOIN " +
                            " " + baseDat + ".logger_pushers " +
                            " ON " +
                            " table_pushers.id_loggerPusher = logger_pushers.id_loggerPusher " +
                            " INNER JOIN " +
                            " " + baseDat + ".logger_type_pushers " +
                            " ON " +
                            " logger_pushers.id_loggerTypePusher = logger_type_pushers.id_loggerTypePusher " +
                            " WHERE " +
                            " table_pushers.date_unreg IS NULL " +
                            " ORDER BY " +
                            " logger_pushers.namePusher ASC "
            ;
        } else {
            query =
                    "SELECT " +
                            " table_pushers.id_pusher, " +
                            " table_pushers.date_reg, " +
                            " logger_pushers.id_loggerPusher, " +
                            " logger_pushers.date_upd as date_upd_pusher, " +
                            " logger_pushers.id_loggerUserEdit as id_loggerUserEditPusher, " +
                            " logger_pushers.namePusher, " +
                            " logger_type_pushers.id_loggerTypePusher, " +
                            " logger_type_pushers.data_upd as data_upd_type, " +
                            " logger_type_pushers.id_loggerUserEdit as id_loggerUserEditType, " +
                            " logger_type_pushers.id_typePusher, " +
                            " logger_type_pushers.nameType, " +
                            " logger_type_pushers.forceNominal, " +
                            " logger_type_pushers.moveNominal, " +
                            " logger_type_pushers.unclenchingTime, " +
                            " table_pushers.date_unreg " +
                            " FROM " +
                            " " + baseDat + ".table_pushers " +
                            " INNER JOIN " +
                            " " + baseDat + ".logger_pushers " +
                            " ON " +
                            " table_pushers.id_loggerPusher = logger_pushers.id_loggerPusher " +
                            " INNER JOIN " +
                            " " + baseDat + ".logger_type_pushers " +
                            " ON " +
                            " logger_pushers.id_loggerTypePusher = logger_type_pushers.id_loggerTypePusher " +
                            " ORDER BY " +
                            " logger_pushers.namePusher ASC "
            ;
        }
        result = statement.executeQuery(query);
        // создание списка
        ArrayList<Pusher> listPusher = new ArrayList<>();
        myLog.log(Level.SEVERE, "нужен рефакторинг запроса", new Exception());
        while (result.next()) {
            listPusher.add(new Pusher(
                    result.getLong("id_pusher"),
                    result.getTimestamp("date_reg"),
                    new LoggerPusher(
                            result.getLong("id_loggerPusher"),
                            result.getTimestamp("date_upd_pusher"),
                            result.getLong("id_loggerUserEditPusher"),
                            result.getLong("id_pusher"),
                            result.getString("namePusher"),
                            /*new LoggerTypePusher(
                                    result.getLong("id_loggerTypePusher"),
                                    result.getTimestamp("data_upd_type"),
                                    result.getLong("id_loggerUserEditType"),
                                    result.getLong("logger_type_pushers.id_typePusher"),
                                    result.getString("nameType"),
                                    result.getInt("forceNominal"),
                                    result.getInt("moveNominal"),
                                    result.getInt("unclenchingTime")
                            )*/
                            new TypePusher(
                                    0,
                                    null,
                                    new LoggerTypePusher(
                                            0,
                                            null,
                                            0,
                                            0,
                                            null,
                                            0,
                                            0,
                                            0
                                    ),
                                    null
                            )
                    ),
                    result.getTimestamp("date_unreg")
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
        boolean fl;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);

        boolean saveAutoCommit;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

        PreparedStatement preStatementUser;
        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUserUpd;
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
            long id_user = ((ClientPreparedStatement)preStatementUser).getLastInsertID();
            //
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".logger_users " +
                            " (date_upd, id_loggerUserEdit, id_user, surName, userPassword, rang) " +
                            " VALUES (?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3, id_user);
            preStatementLogger.setString(4, surName);
            preStatementLogger.setString(5, pass);
            preStatementLogger.setInt(6, rang);
            preStatementLogger.executeUpdate();
            long id_loggerUser = ((ClientPreparedStatement)preStatementLogger).getLastInsertID();
            //
            preStatementUserUpd = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".table_users " +
                            " SET " +
                            " id_loggerUser = ? " +
                            " WHERE id_user = ? "
            );
            preStatementUserUpd.setLong(1, id_loggerUser);
            preStatementUserUpd.setLong(2, id_user);
            preStatementUserUpd.executeUpdate();
            //
            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }
        //
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

        boolean saveAutoCommit;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

        PreparedStatement preStatementUserUpd;

        try {
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            //
            preStatementUserUpd = connection.prepareStatement(
                    "UPDATE " +
                            baseDat + ".table_users " +
                            "SET " +
                            "date_unreg = ? " +
                            "WHERE id_user = ? "
            );
            preStatementUserUpd.setTimestamp(1, timestamp);
            preStatementUserUpd.setLong(2, user.id_user);
            preStatementUserUpd.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }
        try {
            preStatementUserUpd.close();
        } catch (SQLException throwables) { }
    }
    // ===================================================================================================
    // обновление данных о пользователе
    @Override
    public void updateDataUser(User user, long id_loggerUserEdit, String surName, String password, int rang) throws BaseDataException {
        if (connection == null) throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION);
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);

        boolean saveAutoCommit = true;
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
                            " (date_upd, id_loggerUserEdit, id_user, surName, userPassword, rang) " +
                            "VALUES (?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3,  user.id_user);
            preStatementLogger.setString(4, surName);
            preStatementLogger.setString(5, BaseData.Password.encoding(password));
            preStatementLogger.setInt(6, rang);
            preStatementLogger.executeUpdate();
            long id_loggerUser = ((ClientPreparedStatement)preStatementLogger).getLastInsertID();
            //
            preStatementUserUpd = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".table_users " +
                            " SET " +
                            " id_loggerUser = ? " +
                            " WHERE id_user = ? "
            );
            preStatementUserUpd.setLong(1, id_loggerUser);
            preStatementUserUpd.setLong(2, user.id_user);
            preStatementUserUpd.executeUpdate();
            //
            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }
        try {
            preStatementUserUpd.close();
            preStatementLogger.close();
        } catch (SQLException e) { }
    }
    // запись измерений
    @Override
    public void writeDataDist(Date date, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws BaseDataException {
        if (connection == null) throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION);
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);
        //
        PreparedStatement statement = null;
        Statement statementReadSpec = null;
        boolean saveAutoCommit = false;
        //
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        //
        try {
            // чтение последнего id spec
            ResultSet resultSpec = statementReadSpec.executeQuery(
                    "SELECT table_spec.id " +
                            " FROM " + baseDat + ".table_spec " +
                            " ORDER BY table_spec.id DESC " +
                            " LIMIT 1 "
            );
            resultSpec.next();
            long id_spec = resultSpec.getLong(1);
            // запись
            statement = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".table_Data " +
                            " (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, dis) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
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
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }
        //
        try {
            statementReadSpec.close();
            statement.close();
        } catch (SQLException e) { }
    }
    // запись нового типа толкателя
    @Override
    public void writeNewTypePusher(long id_loggerUser, String nameType, int forceNominal, int moveNominal, int unclenchingTime) throws BaseDataException {
        if (connection == null) throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION);
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE);
        //
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        //
        PreparedStatement preStatementPusherType;
        PreparedStatement preStatementLoggerPusherType;
        PreparedStatement preStatementUpdatePusherType;
        // время записи
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        //
        try {
            // создание записи индификатора толкателя
            preStatementPusherType = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".type_pushers " +
                            " (date_reg, id_loggerTypePusher) " +
                            " VALUES (?, ?) "
            );
            preStatementPusherType.setTimestamp(1, timestamp);
            preStatementPusherType.setLong(2, 0);
            preStatementPusherType.executeUpdate();
            long id_typePusher = ((ClientPreparedStatement) preStatementPusherType).getLastInsertID();
            // создание записи в журнале типа толкателя
            preStatementLoggerPusherType = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".logger_type_pushers " +
                            " (data_upd, id_loggerUserEdit, id_typePusher, nameType, forceNominal, moveNominal, unclenchingTime) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?) "
            );
            preStatementLoggerPusherType.setTimestamp(1, timestamp);
            preStatementLoggerPusherType.setLong(2, id_loggerUser);
            preStatementLoggerPusherType.setLong(3, id_typePusher);
            preStatementLoggerPusherType.setString(4, nameType);
            preStatementLoggerPusherType.setInt(5, forceNominal);
            preStatementLoggerPusherType.setInt(6, moveNominal);
            preStatementLoggerPusherType.setInt(7, unclenchingTime);
            preStatementLoggerPusherType.executeUpdate();
            long id_loggerTypePusher = ((ClientPreparedStatement) preStatementLoggerPusherType).getLastInsertID();
            //
            preStatementUpdatePusherType = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".type_pushers " +
                            " SET " +
                            " id_loggerTypePusher = ? " +
                            " WHERE id_typePusher = ? "
            );
            preStatementUpdatePusherType.setLong(1, id_loggerTypePusher);
            preStatementUpdatePusherType.setLong(2, id_typePusher);
            preStatementUpdatePusherType.executeUpdate();
            //
            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) { e = new SQLException("ошибка отката транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException("ошибка транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        // close
        try {
            preStatementLoggerPusherType.close();
            preStatementPusherType.close();
            preStatementUpdatePusherType.close();
        } catch (SQLException throwables) { }
    }
    // обновление типа толкателя
    @Override
    public void updateTypePusher(TypePusher typePusher, long id_loggerUserEdit, String nameType, int forceNominal, int moveNominal, int unclenchingTime) throws BaseDataException {
        if (connection == null) { throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION); }
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) { throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) { throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE); }
        if (typePusher == null) { throw new BaseDataException("нет данных", Status.PARAMETERS_ERROR); }

        boolean saveAutoCommit = true;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        long id_loggerTypePusher;

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".logger_type_pushers " +
                            " (data_upd, id_loggerUserEdit, id_typePusher, nameType, forceNominal, moveNominal, unclenchingTime) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3, typePusher.id_typePusher);
            preStatementLogger.setString(4, nameType);
            preStatementLogger.setInt(5, forceNominal);
            preStatementLogger.setInt(6, moveNominal);
            preStatementLogger.setInt(7, unclenchingTime);
            preStatementLogger.executeUpdate();
            id_loggerTypePusher = ((ClientPreparedStatement) preStatementLogger).getLastInsertID();
            //
            preStatementUpdate = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".type_pushers " +
                            " SET " +
                            " id_loggerTypePusher = ? " +
                            " WHERE id_typePusher = ? "
            );
            preStatementUpdate.setLong(1, id_loggerTypePusher);
            preStatementUpdate.setLong(2, typePusher.id_typePusher);
            preStatementUpdate.executeUpdate();
            //
            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }

        typePusher.loggerTypePusher.id_loggerTypePusher = id_loggerTypePusher;
        typePusher.loggerTypePusher.data_upd = timestamp;
        typePusher.loggerTypePusher.id_loggerUserEdit = id_loggerUserEdit;
        typePusher.loggerTypePusher.nameType = nameType;
        typePusher.loggerTypePusher.forceNominal = forceNominal;
        typePusher.loggerTypePusher.moveNominal = moveNominal;
        typePusher.loggerTypePusher.unclenchingTime = unclenchingTime;

        //
        try {
            preStatementLogger.close();
            preStatementUpdate.close();
        } catch (SQLException throwables) { }
    }
    // деактивация типа толкателя
    @Override
    public void deativateTypePusher(long id_loggerUser, TypePusher typePusher) throws BaseDataException {
        if (connection == null) { throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION); }
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) { throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE); }
        if (typePusher == null) { throw new BaseDataException("нет данных", Status.PARAMETERS_ERROR); }

        boolean saveAutoCommit = true;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) { throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        //
        PreparedStatement preStatementUpdate;
        //
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preStatementUpdate = connection.prepareStatement(
                    "UPDATE " +
                            " " + baseDat + ".type_pushers " +
                            " SET " +
                            " date_unreg = ? " +
                            " WHERE id_typePusher = ? "
            );
            preStatementUpdate.setTimestamp(1, timestamp);
            preStatementUpdate.setLong(2, typePusher.id_typePusher);
            preStatementUpdate.executeUpdate();
            //
            connection.commit();
            /*typePusher.date_upd = timestamp;
            typePusher.date_unreg = timestamp;*/
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }
        try {
            preStatementUpdate.close();
        } catch (SQLException throwables) { }
    }
    // чтение списока типов толкателей
    @Override
    public TypePusher[] getListTypePushers(boolean actual) throws BaseDataException {
        if (connection == null) { throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION); }
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) { throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE); }

        boolean saveAutoCommit = true;
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) { throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        //
        ResultSet result;
        Statement statement;
        ArrayList<TypePusher> list;

        try {
            statement = connection.createStatement();
            String query;
            if (actual) {
                query =
                        "SELECT " +
                                " type_pushers.id_typePusher, " +
                                " type_pushers.date_reg, " +
                                " logger_type_pushers.id_loggerTypePusher, " +
                                " logger_type_pushers.data_upd, " +
                                " logger_type_pushers.id_loggerUserEdit, " +
                                " logger_type_pushers.nameType, " +
                                " logger_type_pushers.forceNominal, " +
                                " logger_type_pushers.moveNominal, " +
                                " logger_type_pushers.unclenchingTime, " +
                                " type_pushers.date_unreg " +
                                " FROM " +
                                " " + baseDat + ".type_pushers " +
                                " INNER JOIN " +
                                " " + baseDat + ".logger_type_pushers " +
                                " ON " +
                                " type_pushers.id_loggerTypePusher = logger_type_pushers.id_loggerTypePusher " +
                                " WHERE " +
                                " type_pushers.date_unreg IS NULL " +
                                " ORDER BY " +
                                " logger_type_pushers.nameType "
                ;
            } else {
                query =
                        "SELECT " +
                                " type_pushers.id_typePusher, " +
                                " type_pushers.date_reg, " +
                                " logger_type_pushers.id_loggerTypePusher, " +
                                " logger_type_pushers.data_upd, " +
                                " logger_type_pushers.id_loggerUserEdit, " +
                                " logger_type_pushers.nameType, " +
                                " logger_type_pushers.forceNominal, " +
                                " logger_type_pushers.moveNominal, " +
                                " logger_type_pushers.unclenchingTime, " +
                                " type_pushers.date_unreg " +
                                " FROM " +
                                " " + baseDat + ".type_pushers " +
                                " INNER JOIN " +
                                " " + baseDat + ".logger_type_pushers " +
                                " ON " +
                                " type_pushers.id_loggerTypePusher = logger_type_pushers.id_loggerTypePusher " +
                                " ORDER BY " +
                                " logger_type_pushers.nameType "
                ;
            }
            result = statement.executeQuery(query);
            // создание списка
            list = new ArrayList<>();
            //
            try {
                while (result.next()) {
                    try {
                        list.add(new TypePusher(
                                result.getLong("id_typePusher"),
                                result.getTimestamp("date_reg"),
                                new LoggerTypePusher(
                                        result.getLong("id_loggerTypePusher"),
                                        result.getTimestamp("data_upd"),
                                        result.getLong("id_loggerUserEdit"),
                                        result.getLong("id_typePusher"),
                                        result.getString("nameType"),
                                        result.getInt("forceNominal"),
                                        result.getInt("moveNominal"),
                                        result.getInt("unclenchingTime")
                                ),
                                result.getTimestamp("date_unreg")
                        ));
                    } catch (Exception e) {
                        myLog.log(Level.WARNING, "ошибка парсинга", e);
                    }
                }
            } catch (Exception e) { }
            //if (list.size() == 0) { throw new SQLException("ошибка получения списка типов толкателей"); }
            connection.commit();
        } catch (SQLException e) {
            throw new BaseDataException("ошибка транзакции", e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            try { connection.setAutoCommit(saveAutoCommit);
            } catch (SQLException throwables) { }
        }
        //
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) { }
        return list.toArray(new TypePusher[0]);
    }
}
