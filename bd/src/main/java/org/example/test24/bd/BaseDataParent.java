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
        internalCheckConnect();
        internalAutoCommit(true);

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
                                " users.id_user, " +
                                " users.date_reg, " +
                                " users_logger.id_loggerUser, " +
                                " users_logger.date_upd, " +
                                " users_logger.id_loggerUserEdit, " +
                                " users_logger.surName, " +
                                " users_logger.userPassword, " +
                                " users_logger.rang, " +
                                " users.date_unreg " +
                                " FROM " +
                                " " + baseDat + ".users " +
                                " INNER JOIN " +
                                " " + baseDat + ".users_logger " +
                                " ON " +
                                " users.id_loggerUser = users_logger.id_loggerUser " +
                                " WHERE " +
                                " users.date_unreg IS NULL " +
                                " ORDER BY " +
                                " users_logger.surName ASC "
                ;
            } else {
                query =
                        "SELECT " +
                                " users.id_user, " +
                                " users.date_reg, " +
                                " users_logger.id_loggerUser, " +
                                " users_logger.date_upd, " +
                                " users_logger.id_loggerUserEdit, " +
                                " users_logger.surName, " +
                                " users_logger.userPassword, " +
                                " users_logger.rang, " +
                                " users.date_unreg " +
                                " FROM " +
                                " " + baseDat + ".users " +
                                " INNER JOIN " +
                                " " + baseDat + ".users_logger " +
                                " ON " +
                                " users.id_loggerUser = users_logger.id_loggerUser " +
                                " ORDER BY " +
                                " users_logger.surName ASC "
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
        internalCheckConnect();
        internalAutoCommit(true);

        boolean data, data_spec;
        boolean users, users_logger;
        boolean pushers, logger_pushers;
        data = checkStructureTable(
                base,
                "data",
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
        users = checkStructureTable(
                base,
                "users",
                new ArrayList(Arrays.asList(
                        "id_user",
                        "date_reg",
                        "id_loggerUser",
                        "date_unreg"
                ))
        );
        users_logger = checkStructureTable(
                base,
                "users_logger",
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
        pushers = checkStructureTable(
                base,
                "pushers",
                new ArrayList(Arrays.asList(
                        "id_pusher",
                        "date_reg",
                        "id_loggerPusher",
                        "date_unreg"
                ))
        );
        return data && users && pushers;
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
                    " SELECT " +
                            " pushers.id_pusher, " +
                            " pushers.date_reg AS date_reg_pushers, " +
                            " pushers_logger.id_loggerPusher,  " +
                            " pushers_logger.date_upd AS date_upd_pushers, " +
                            " pushers_logger.id_loggerUserEdit AS id_loggerUserEdit_pushers, " +
                            " pushers_logger.namePusher, " +
                            " pusherstype.id_typePusher, " +
                            " pusherstype.date_reg AS date_reg_typepushers, " +
                            " pusherstype_logger.id_loggerTypePusher, " +
                            " pusherstype_logger.date_upd AS date_upd_typepushers, " +
                            " pusherstype_logger.id_loggerUserEdit AS id_loggerUserEdittypepushers, " +
                            " pusherstype_logger.nameType, " +
                            " pusherstype_logger.forceNominal, " +
                            " pusherstype_logger.moveNominal, " +
                            " pusherstype_logger.unclenchingTime, " +
                            " pusherstype.date_unreg AS date_unreg_typepushers, " +
                            " pushers.date_unreg AS date_unreg_pushers " +
                            " FROM " +  baseDat + ".pushers " +
                            " INNER JOIN " + baseDat + ".pushers_logger ON pushers.id_loggerPusher = pushers_logger.id_loggerPusher " +
                            " INNER JOIN " + baseDat + ".pusherstype ON pushers_logger.id_typePusher = pusherstype.id_typePusher " +
                            " INNER JOIN " + baseDat + ".pusherstype_logger ON pusherstype.id_loggerTypePusher = pusherstype_logger.id_loggerTypePusher " +
                            " WHERE pushers.date_unreg IS NULL " +
                            " ORDER BY pushers_logger.namePusher "
            ;
        } else {
            query =
                    " SELECT " +
                            " pushers.id_pusher, " +
                            " pushers.date_reg AS date_reg_pushers, " +
                            " pushers_logger.id_loggerPusher,  " +
                            " pushers_logger.date_upd AS date_upd_pushers, " +
                            " pushers_logger.id_loggerUserEdit AS id_loggerUserEdit_pushers, " +
                            " pushers_logger.namePusher, " +
                            " pusherstype.id_typePusher, " +
                            " pusherstype.date_reg AS date_reg_typepushers, " +
                            " pusherstype_logger.id_loggerTypePusher, " +
                            " pusherstype_logger.date_upd AS date_upd_typepushers, " +
                            " pusherstype_logger.id_loggerUserEdit AS id_loggerUserEdittypepushers, " +
                            " pusherstype_logger.nameType, " +
                            " pusherstype_logger.forceNominal, " +
                            " pusherstype_logger.moveNominal, " +
                            " pusherstype_logger.unclenchingTime, " +
                            " pusherstype.date_unreg AS date_unreg_typepushers, " +
                            " pushers.date_unreg AS date_unreg_pushers " +
                            " FROM " +  baseDat + ".pushers " +
                            " INNER JOIN " + baseDat + ".pushers_logger ON pushers.id_loggerPusher = pushers_logger.id_loggerPusher " +
                            " INNER JOIN " + baseDat + ".pusherstype ON pushers_logger.id_typePusher = pusherstype.id_typePusher " +
                            " INNER JOIN " + baseDat + ".pusherstype_logger ON pusherstype.id_loggerTypePusher = pusherstype_logger.id_loggerTypePusher " +
                            " ORDER BY pushers_logger.namePusher "
            ;
        }
        result = statement.executeQuery(query);
        // создание списка
        ArrayList<Pusher> listPusher = new ArrayList<>();
        while (result.next()) {
            listPusher.add(new Pusher(
                    result.getLong("id_pusher"),
                    result.getTimestamp("date_reg_pushers"),
                    new LoggerPusher(
                            result.getLong("id_loggerPusher"),
                            result.getTimestamp("date_upd_pushers"),
                            result.getLong("id_loggerUserEdit_pushers"),
                            result.getLong("id_pusher"),
                            result.getString("namePusher"),
                            new TypePusher(
                                    result.getLong("id_typePusher"),
                                    result.getTimestamp("date_reg_typepushers"),
                                    new LoggerTypePusher(
                                            result.getLong("id_loggerTypePusher"),
                                            result.getTimestamp("date_upd_typepushers"),
                                            result.getLong("id_loggerUserEdit_pushers"),
                                            result.getLong("id_typePusher"),
                                            result.getString("nameType"),
                                            result.getInt("forceNominal"),
                                            result.getInt("moveNominal"),
                                            result.getInt("unclenchingTime")
                                    ),
                                    result.getTimestamp("date_unreg_typepushers")
                            )
                    ),
                    result.getTimestamp("date_unreg_pushers")
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
        internalCheckConnect();
        internalAutoCommit(false);

        PreparedStatement preStatementUser;
        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUserUpd;
        try {
            String pass = new String(java.util.Base64.getEncoder().encode(password.getBytes()));
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            //
            preStatementUser = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".users " +
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
                            " " + baseDat + ".users_logger " +
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
                            " " + baseDat + ".users " +
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
        internalCheckConnect();
        internalAutoCommit(false);
        PreparedStatement preparedStatementLogger;
        PreparedStatement preStatementUserUpdate;
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preparedStatementLogger = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".users_logger " +
                            " (date_upd, id_loggerUserEdit, id_user, surName, userPassword, rang) " +
                            " VALUES (?, ?, ?, ?, ?, ?) "
            );
            preparedStatementLogger.setTimestamp(1, timestamp);
            preparedStatementLogger.setLong(2, id_loggerUserEdit);
            preparedStatementLogger.setLong(3,  user.id_user);
            preparedStatementLogger.setString(4, user.surName);
            preparedStatementLogger.setString(5, BaseData.Password.encoding(user.userPassword));
            preparedStatementLogger.setInt(6, user.rang);
            preparedStatementLogger.executeUpdate();
            long id_loggerUser = ((ClientPreparedStatement)preparedStatementLogger).getLastInsertID();
            //
            preStatementUserUpdate = connection.prepareStatement(
                    "UPDATE " + baseDat + ".users " +
                            " SET " +
                            " date_unreg = ?, " +
                            " id_loggerUser = ? " +
                            " WHERE id_user = ? "
            );
            preStatementUserUpdate.setTimestamp(1, timestamp);
            preStatementUserUpdate.setLong(2, id_loggerUser);
            preStatementUserUpdate.setLong(3, user.id_user);
            preStatementUserUpdate.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            preparedStatementLogger.close();
            preStatementUserUpdate.close();
        } catch (SQLException throwables) { }
    }
    // ===================================================================================================
    // обновление данных о пользователе
    @Override
    public void updateDataUser(User user, long id_loggerUserEdit, String surName, String password, int rang) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUserUpd;

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " +
                            " " + baseDat + ".users_logger " +
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
                            " " + baseDat + ".users " +
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
        internalCheckConnect();
        internalAutoCommit(false);
        String query;
        //
        PreparedStatement preStatementPusherType;
        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        // время записи
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        //
        try {
            // создание записи индификатора толкателя
            query = "INSERT INTO " + baseDat + ".pusherstype " +
                    " (date_reg, id_loggerTypePusher) " +
                    " VALUES (?, ?) "
            ;
            preStatementPusherType = connection.prepareStatement(query);
            preStatementPusherType.setTimestamp(1, timestamp);
            preStatementPusherType.setLong(2, 0);
            preStatementPusherType.executeUpdate();
            long id_typePusher = ((ClientPreparedStatement) preStatementPusherType).getLastInsertID();
            // создание записи в журнале типа толкателя
            query = "INSERT INTO " + baseDat + ".pusherstype_logger " +
                    " (date_upd, id_loggerUserEdit, id_typePusher, nameType, forceNominal, moveNominal, unclenchingTime) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?) "
            ;
            preStatementLogger = connection.prepareStatement(query);
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUser);
            preStatementLogger.setLong(3, id_typePusher);
            preStatementLogger.setString(4, nameType);
            preStatementLogger.setInt(5, forceNominal);
            preStatementLogger.setInt(6, moveNominal);
            preStatementLogger.setInt(7, unclenchingTime);
            preStatementLogger.executeUpdate();
            long id_loggerTypePusher = ((ClientPreparedStatement) preStatementLogger).getLastInsertID();
            //
            preStatementUpdate = connection.prepareStatement(
                    "UPDATE " + baseDat + ".pusherstype " +
                            " SET " +
                            " id_loggerTypePusher = ? " +
                            " WHERE id_typePusher = ? "
            );
            preStatementUpdate.setLong(1, id_loggerTypePusher);
            preStatementUpdate.setLong(2, id_typePusher);
            preStatementUpdate.executeUpdate();
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
            preStatementLogger.close();
            preStatementPusherType.close();
            preStatementUpdate.close();
        } catch (SQLException throwables) { }
    }
    // обновление типа толкателя
    @Override
    public void updateTypePusher(TypePusher typePusher, long id_loggerUserEdit, String nameType, int forceNominal, int moveNominal, int unclenchingTime) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        long id_loggerTypePusher;

        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".pusherstype_logger " +
                            " (date_upd, id_loggerUserEdit, id_typePusher, nameType, forceNominal, moveNominal, unclenchingTime) " +
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
                    "UPDATE " + baseDat + ".pusherstype " +
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
        internalCheckConnect();
        internalAutoCommit(false);
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
                                " pusherstype.id_typePusher, " +
                                " pusherstype.date_reg, " +
                                " pusherstype_logger.id_loggerTypePusher, " +
                                " pusherstype_logger.date_upd, " +
                                " pusherstype_logger.id_loggerUserEdit, " +
                                " pusherstype_logger.nameType, " +
                                " pusherstype_logger.forceNominal, " +
                                " pusherstype_logger.moveNominal, " +
                                " pusherstype_logger.unclenchingTime, " +
                                " pusherstype.date_unreg " +
                                " FROM " + baseDat + ".pusherstype " +
                                " INNER JOIN " + baseDat + ".pusherstype_logger ON pusherstype.id_loggerTypePusher = pusherstype_logger.id_loggerTypePusher " +
                                " WHERE pusherstype.date_unreg IS NULL " +
                                " ORDER BY pusherstype_logger.nameType "
                ;
            } else {
                query =
                        "SELECT " +
                                " pusherstype.id_typePusher, " +
                                " pusherstype.date_reg, " +
                                " pusherstype_logger.id_loggerTypePusher, " +
                                " pusherstype_logger.date_upd, " +
                                " pusherstype_logger.id_loggerUserEdit, " +
                                " pusherstype_logger.nameType, " +
                                " pusherstype_logger.forceNominal, " +
                                " pusherstype_logger.moveNominal, " +
                                " pusherstype_logger.unclenchingTime, " +
                                " pusherstype.date_unreg " +
                                " FROM " + baseDat + ".pusherstype " +
                                " INNER JOIN " + baseDat + ".pusherstype_logger ON pusherstype.id_loggerTypePusher = pusherstype_logger.id_loggerTypePusher " +
                                " ORDER BY pusherstype_logger.nameType "
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
                                        result.getTimestamp("date_upd"),
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
        }
        //
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) { }
        return list.toArray(new TypePusher[0]);
    }

    @Override
    public BaseData cloneNewBase(String base) {
        BaseData baseData = new BaseDataParent(connection, base);
        return baseData;
    }
    protected BaseDataParent(Connection connection, String base) {
        this.connection = connection;
        this.baseDat = base;
    }
    protected BaseDataParent() {}
    // запись нового толкателя
    @Override
    public void writeNewPusher(long id_loggerUser, String regNumber, long id_typePusher) throws BaseDataException {
        if (connection == null) { throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION); }
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) { throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE); }
        //
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        String query;
        //
        PreparedStatement preparedStatementPusher;
        PreparedStatement preparedStatementLoggerPusher;
        PreparedStatement preparedStatementPusherUpdate;
        // время записи
        java.sql.Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        //
        try {
            // создание индификатора
            query = "INSERT INTO " +
                    " " + baseDat + ".table_pushers " +
                    " (date_reg, id_loggerPusher) " +
                    " VALUES (?, ?) "
            ;
            preparedStatementPusher = connection.prepareStatement(query);
            preparedStatementPusher.setTimestamp(1, timestamp);
            preparedStatementPusher.setLong(2, 0);
            preparedStatementPusher.executeUpdate();
            long id_pusher = ((ClientPreparedStatement) preparedStatementPusher).getLastInsertID();
            // запись в журнале
            query = "INSERT INTO " +
                    " " + baseDat + " .logger_pushers " +
                    " (date_upd, id_loggerUserEdit, id_pusher, namePusher, id_typePusher) " +
                    " VALUES (?, ?, ?, ?, ?) "
            ;
            preparedStatementLoggerPusher = connection.prepareStatement(query);
            preparedStatementLoggerPusher.setTimestamp(1, timestamp);
            preparedStatementLoggerPusher.setLong(2, id_loggerUser);
            preparedStatementLoggerPusher.setLong(3, id_pusher);
            preparedStatementLoggerPusher.setString(4, regNumber);
            preparedStatementLoggerPusher.setLong(5, id_typePusher);
            preparedStatementLoggerPusher.executeUpdate();
            long id_loggerPusher = ((ClientPreparedStatement) preparedStatementLoggerPusher).getLastInsertID();
            // обновление индификатора
            query = "UPDATE " +
                    " " + baseDat + ".table_pushers " +
                    " SET " +
                    " id_loggerPusher = ? " +
                    " WHERE id_pusher = ? "
            ;
            preparedStatementPusherUpdate = connection.prepareStatement(query);
            preparedStatementPusherUpdate.setLong(1, id_loggerPusher);
            preparedStatementPusherUpdate.setLong(2, id_pusher);
            preparedStatementPusherUpdate.executeUpdate();
            //
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException sqe) {
                e = new SQLException("ошибка отката транзакции: " + sqe.getMessage(), e);
            }
            throw new BaseDataException("ошибка транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        // close
        try {
            preparedStatementPusher.close();
            preparedStatementLoggerPusher.close();
            preparedStatementPusherUpdate.close();
        } catch (SQLException throwables) {
        }
    }
    // удаление толкателя
    @Override
    public void deactivatePusher(long id_loggerUser, long id_pusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);
        //
        PreparedStatement preparedStatement = null;
        java.sql.Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            preparedStatement = connection.prepareStatement(
                    "UPDATE " + baseDat + ".table_pushers SET " +
                            " date_unreg = ? " +
                            " WHERE id_pusher = ? "
            );
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setLong(2, id_pusher);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException sqe) {
                e = new SQLException("ошибка отмены транзакции: " + sqe.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException throwables) {
                }
            }
        }
    }
    protected void internalCheckConnect() throws BaseDataException {
        if (connection == null) { throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION); }
        boolean fl = false;
        try {
            fl = connection.isClosed();
        } catch (SQLException e) {
            throw new BaseDataException("соединение не установлено", e, Status.CONNECT_NO_CONNECTION);
        }
        if (fl) { throw new BaseDataException("соединение закрыто", Status.CONNECT_CLOSE); }
    }
    protected void internalAutoCommit(boolean enabled) throws BaseDataException {
        try {
            connection.setAutoCommit(enabled);
            if (!enabled) connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new BaseDataException("ошибка инициации транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
    }
}
