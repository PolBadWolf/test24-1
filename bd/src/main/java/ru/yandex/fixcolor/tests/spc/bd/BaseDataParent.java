package ru.yandex.fixcolor.tests.spc.bd;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;

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
    @Override
    public String getCurrentBase() { return baseDat; }
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
                query = "SELECT " +
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
        boolean pusherstype, pusherstype_logger;
        data = checkStructureTable(
                base,
                "datas",
                new ArrayList<>(Arrays.asList(
                        "id_data",
                        "dateTime",
                        "id_spec",
                        "n_cicle",
                        "ves",
                        "tik_shelf",
                        "tik_back",
                        "tik_stop",
                        "forceNominal",
                        "moveNominal",
                        "unclenchingTime",
                        "dataMeasured"
                ))
        );
        data_spec = checkStructureTable(
                base,
                "data_spec",
                new ArrayList<>(Arrays.asList(
                        "id_dataSpec",
                        "date_upd",
                        "id_user",
                        "id_pusher"
                ))
        );
        users = checkStructureTable(
                base,
                "users",
                new ArrayList<>(Arrays.asList(
                        "id_user",
                        "date_reg",
                        "id_loggerUser",
                        "date_unreg"
                ))
        );
        users_logger = checkStructureTable(
                base,
                "users_logger",
                new ArrayList<>(Arrays.asList(
                        "id_loggerUser",
                        "date_upd",
                        "id_loggerUserEdit",
                        "id_user",
                        "surName",
                        "userPassword",
                        "rang"
                ))
        );
        pushers = checkStructureTable(
                base,
                "pushers",
                new ArrayList<>(Arrays.asList(
                        "id_pusher",
                        "date_reg",
                        "id_loggerPusher",
                        "date_unreg"
                ))
        );
        logger_pushers = checkStructureTable(
                base,
                "pushers_logger",
                new ArrayList<>(Arrays.asList(
                        "id_loggerPusher",
                        "date_upd",
                        "id_loggerUserEdit",
                        "id_pusher",
                        "namePusher",
                        "id_typePusher"
                ))
        );
        pusherstype = checkStructureTable(
                base,
                "pusherstype",
                new ArrayList<>(Arrays.asList(
                        "id_typePusher",
                        "date_reg",
                        "id_loggerTypePusher",
                        "date_unreg"
                ))
        );
        pusherstype_logger = checkStructureTable(
                base,
                "pusherstype_logger",
                new ArrayList<>(Arrays.asList(
                       "id_loggerTypePusher",
                       "date_upd",
                        "id_loggerUserEdit",
                        "id_typePusher",
                        "nameType",
                        "forceNominal",
                        "moveNominal",
                        "unclenchingTime"
                ))
        );
        return data && data_spec && users && users_logger && pushers && logger_pushers && pusherstype && pusherstype_logger;
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
    public void setNewUserPassword(long id_loggerUserEdit, User user, String newPassword) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        String query;
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            String pass = BaseData.Password.encoding(newPassword);
            //
            query =
                    "INSERT INTO " + baseDat + ".users_logger " +
                            " (date_upd, id_loggerUserEdit, id_user, surName, userPassword, rang) " +
                            " VALUES (?, ?, ?, ?, ?, ?)"
                    ;
            preStatementLogger = connection.prepareStatement(query);
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3, user.id_user);
            preStatementLogger.setString(4, user.surName);
            preStatementLogger.setString(5, pass);
            preStatementLogger.setInt(6, user.rang);
            preStatementLogger.executeUpdate();
            long id_loggerUser = ((ClientPreparedStatement)preStatementLogger).getLastInsertID();
            //
            preStatementUpdate = connection.prepareStatement(
                    "UPDATE " + baseDat + ".users " +
                            " SET " +
                            " id_loggerUser = ? " +
                            " WHERE id_user = ? "
            );
            preStatementUpdate.setLong(1, id_loggerUser);
            preStatementUpdate.setLong(2, user.id_user);
            preStatementUpdate.executeUpdate();
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
        }
        //
        try {
            preStatementUpdate.close();
            preStatementLogger.close();
        } catch (SQLException se) { }
    }
    // ===================================================================================================
    // чтение списка толкателей ****
    @Override
    public Pusher[] getListPushers(boolean actual) throws Exception {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        // запрос на список толкателей
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
    public void deleteUser(long id_loggerUserEdit, User user) throws BaseDataException {
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
    public void writeDataDist(int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop,
                              int forceNominal, int moveNominal, int unclenchingTime, Blob dataMeasured) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);
        //
        Statement statementReadSpec;
        PreparedStatement statement;
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        //
        try {
            // чтение последнего id spec
            statementReadSpec = connection.createStatement();
            ResultSet resultSpec = statementReadSpec.executeQuery(
                    "SELECT data_spec.id_dataSpec " +
                            " FROM " + baseDat + ".data_spec " +
                            " ORDER BY data_spec.id_dataSpec DESC " +
                            " LIMIT 1 "
            );
            resultSpec.next();
            long id_spec = resultSpec.getLong(1);
            // запись
            statement = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".datas " +
                            " (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, forceNominal, moveNominal, unclenchingTime, dataMeasured) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            );
            statement.setTimestamp(1, timestamp);
            statement.setLong(2, id_spec);
            statement.setInt(3, n_cicle);
            statement.setInt(4, ves);
            statement.setInt(5, tik_shelf);
            statement.setInt(6, tik_back);
            statement.setInt(7, tik_stop);
            statement.setInt(8, forceNominal);
            statement.setInt(9, moveNominal);
            statement.setInt(10, unclenchingTime);
            statement.setBlob(11, dataMeasured);
            statement.executeUpdate();
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
            statementReadSpec.close();
            statement.close();
        } catch (SQLException e) { }
    }
    // запись нового типа толкателя
    @Override
    public long writeNewTypePusher(long id_loggerUser, String nameType, int forceNominal, int moveNominal, int unclenchingTime) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);
        String query;
        //
        PreparedStatement preStatementPusherType;
        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        long id_typePusher;
        long id_loggerTypePusher;
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
            id_typePusher = ((ClientPreparedStatement) preStatementPusherType).getLastInsertID();
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
            id_loggerTypePusher = ((ClientPreparedStatement) preStatementLogger).getLastInsertID();
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
        return id_typePusher;
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
    public void deleteTypePusher(long id_loggerUser, TypePusher typePusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);
        //
        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        //
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".pusherstype_logger " +
                            " (date_upd, id_loggerUserEdit, id_typePusher, nameType, forceNominal, moveNominal, unclenchingTime) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUser);
            preStatementLogger.setLong(3, typePusher.id_typePusher);
            preStatementLogger.setString(4, typePusher.loggerTypePusher.nameType);
            preStatementLogger.setInt(5, typePusher.loggerTypePusher.forceNominal);
            preStatementLogger.setInt(6, typePusher.loggerTypePusher.moveNominal);
            preStatementLogger.setInt(7, typePusher.loggerTypePusher.unclenchingTime);
            preStatementLogger.executeUpdate();
            long id_loggerTypePusher = ((ClientPreparedStatement) preStatementLogger).getLastInsertID();
            //
            preStatementUpdate = connection.prepareStatement(
                    "UPDATE " + baseDat + ".pusherstype " +
                            " SET " +
                            " date_unreg = ?, " +
                            " id_loggerTypePusher = ? " +
                            " WHERE id_typePusher = ? "
            );
            preStatementUpdate.setTimestamp(1, timestamp);
            preStatementUpdate.setLong(2, id_loggerTypePusher);
            preStatementUpdate.setLong(3, typePusher.id_typePusher);
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
        }
        try {
            preStatementLogger.close();
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
        return new BaseDataParent(connection, base);
    }
    protected BaseDataParent(Connection connection, String base) {
        this.connection = connection;
        this.baseDat = base;
    }
    protected BaseDataParent() {}
    // запись нового толкателя
    @Override
    public long writeNewPusher(long id_loggerUser, String regNumber, long id_typePusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);
        //

        String query;
        //
        PreparedStatement preparedStatementPusher;
        PreparedStatement preparedStatementLoggerPusher;
        PreparedStatement preparedStatementPusherUpdate;
        long id_pusher;
        long id_loggerPusher;
        // время записи
        java.sql.Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        //
        try {
            // создание индификатора
            preparedStatementPusher = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".pushers " +
                            " (date_reg, id_loggerPusher) " +
                            " VALUES (?, ?) "
            );
            preparedStatementPusher.setTimestamp(1, timestamp);
            preparedStatementPusher.setLong(2, 0);
            preparedStatementPusher.executeUpdate();
            id_pusher = ((ClientPreparedStatement) preparedStatementPusher).getLastInsertID();
            // запись в журнале
            query = "INSERT INTO " + baseDat + " .pushers_logger " +
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
            id_loggerPusher = ((ClientPreparedStatement) preparedStatementLoggerPusher).getLastInsertID();
            // обновление индификатора
            query = "UPDATE " + baseDat + ".pushers " +
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
        return id_pusher;
    }
    // удаление толкателя
    @Override
    public void deletePusher(long id_loggerUserEdit, Pusher pusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);
        //
        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        long id_loggerPusher;
        //
        java.sql.Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".pushers_logger " +
                            " (date_upd, id_loggerUserEdit, id_pusher, namePusher, id_typePusher) " +
                            " VALUES(?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3, pusher.id_pusher);
            preStatementLogger.setString(4, pusher.loggerPusher.namePusher);
            preStatementLogger.setLong(5, pusher.loggerPusher.typePusher.id_typePusher);
            preStatementLogger.executeUpdate();
            id_loggerPusher = ((ClientPreparedStatement) preStatementLogger).getLastInsertID();
            //
            preStatementUpdate = connection.prepareStatement(
                    "UPDATE " + baseDat + ".pushers " +
                            " SET " +
                            " id_loggerPusher = ?, " +
                            " date_unreg = ? " +
                            " WHERE id_pusher = ? "
            );
            preStatementUpdate.setLong(1, id_loggerPusher);
            preStatementUpdate.setTimestamp(2, timestamp);
            preStatementUpdate.setLong(3, pusher.id_pusher);
            preStatementUpdate.executeUpdate();
            //
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException se) {
                e = new SQLException("ошибка отмены транзакции: " + se.getMessage(), e);
            }
            throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            preStatementLogger.close();
            preStatementUpdate.close();
        } catch (SQLException throwables) { }
    }
    // обновление толкателя
    @Override
    public void updatePusher(Pusher pusher, long id_loggerUserEdit, String regNumber, long id_typePusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);

        PreparedStatement preStatementLogger;
        PreparedStatement preStatementUpdate;
        Statement statementTypePusher;
        long id_loggerPusher;

        java.sql.Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".pushers_logger" +
                            " (date_upd, id_loggerUserEdit, id_pusher, namePusher, id_typePusher) " +
                            " VALUES(?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setLong(2, id_loggerUserEdit);
            preStatementLogger.setLong(3, pusher.id_pusher);
            preStatementLogger.setString(4, regNumber);
            preStatementLogger.setLong(5, id_typePusher);
            preStatementLogger.executeUpdate();
            id_loggerPusher = ((ClientPreparedStatement) preStatementLogger).getLastInsertID();
            //
            preStatementUpdate = connection.prepareStatement(
                    "UPDATE " + baseDat + ".pushers " +
                            " SET " +
                            " id_loggerPusher = ? " +
                            " WHERE id_pusher = ? "
            );
            preStatementUpdate.setLong(1, id_loggerPusher);
            preStatementUpdate.setLong(2, pusher.id_pusher);
            preStatementUpdate.executeUpdate();
            //
            statementTypePusher = connection.createStatement();
            String query =
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
                            " INNER JOIN " + baseDat + ".pusherstype_logger " +
                            " ON pusherstype.id_loggerTypePusher = pusherstype_logger.id_loggerTypePusher " +
                            " WHERE " +
                            " pusherstype.id_typePusher = " + id_typePusher + " "
            ;
            ResultSet result = statementTypePusher.executeQuery(query);
            result.next();
            //
            pusher.loggerPusher.id_loggerPusher = id_loggerPusher;
            pusher.loggerPusher.date_upd = timestamp;
            pusher.loggerPusher.id_loggerUserEdit = id_loggerUserEdit;
            pusher.loggerPusher.namePusher = regNumber;
            pusher.loggerPusher.typePusher = new TypePusher(
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
            );
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
            preStatementLogger.close();
            statementTypePusher.close();
            preStatementUpdate.close();
        } catch (SQLException throwables) { }
    }
    // чтение данных о толкателе
    @Override
    public Pusher getPusher(long id_pusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);
        //
        Statement statement;
        ResultSet result;
        String query;
        Pusher pusher = null;
        //
        try {
            query = "SELECT " +
                    " pushers.id_pusher, " +
                    " pushers.date_reg AS pusher_dateReg, " +
                    " pushers_logger.id_loggerPusher, " +
                    " pushers_logger.date_upd AS pusher_dateUpd, " +
                    " pushers_logger.id_loggerUserEdit AS pusher_IdLoggerUserEdit, " +
                    " pushers_logger.namePusher, " +
                    " pusherstype.id_typePusher, " +
                    " pusherstype.date_reg AS typePusher_dateReg, " +
                    " pusherstype_logger.id_loggerTypePusher, " +
                    " pusherstype_logger.date_upd AS typePusher_dateUpd, " +
                    " pusherstype_logger.id_loggerUserEdit AS typePusher_IdLoggerUserEdit, " +
                    " pusherstype_logger.nameType, " +
                    " pusherstype_logger.forceNominal, " +
                    " pusherstype_logger.moveNominal, " +
                    " pusherstype_logger.unclenchingTime, " +
                    " pusherstype.date_unreg AS typePusher_dateUnreg, " +
                    " pushers.date_unreg AS pusher_dateUnreg " +
                    " FROM " + baseDat + ".pushers " +
                    " INNER JOIN " + baseDat + ".pushers_logger ON pushers.id_loggerPusher = pushers_logger.id_loggerPusher " +
                    " INNER JOIN " + baseDat + ".pusherstype ON pushers_logger.id_typePusher = pusherstype.id_typePusher " +
                    " INNER JOIN " + baseDat + ".pusherstype_logger ON pusherstype.id_loggerTypePusher = pusherstype_logger.id_loggerTypePusher" +
                    " WHERE " +
                    " pushers.id_pusher = " + id_pusher;
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            result.next();
            pusher = new Pusher(
                    result.getLong("id_pusher"),
                    result.getTimestamp("pusher_dateReg"),
                    new LoggerPusher(
                            result.getLong("id_loggerPusher"),
                            result.getTimestamp("pusher_dateUpd"),
                            result.getLong("pusher_IdLoggerUserEdit"),
                            result.getLong("id_pusher"),
                            result.getString("namePusher"),
                            new TypePusher(
                                    result.getLong("id_typePusher"),
                                    result.getTimestamp("typePusher_dateReg"),
                                    new LoggerTypePusher(
                                            result.getLong("id_loggerTypePusher"),
                                            result.getTimestamp("typePusher_dateUpd"),
                                            result.getLong("typePusher_IdLoggerUserEdit"),
                                            result.getLong("id_typePusher"),
                                            result.getString("nameType"),
                                            result.getInt("forceNominal"),
                                            result.getInt("moveNominal"),
                                            result.getInt("unclenchingTime")
                                    ),
                                    result.getTimestamp("typePusher_dateUnreg")
                            )
                    ),
                    result.getTimestamp("pusher_dateUnreg")
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pusher;
    }

    @Override
    public long getIdTypePusherFromIdPusher(long id_pusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);
        //
        Statement statement;
        ResultSet result;
        String query;
        long id_typePusher;

        try {
            statement = connection.createStatement();
            query = "SELECT pushers_logger.id_typePusher " +
                    " FROM " + baseDat + ".pushers " +
                    " INNER JOIN " + baseDat + ".pushers_logger ON pushers.id_loggerPusher = pushers_logger.id_loggerPusher " +
                    " WHERE pushers.id_pusher = " + id_pusher;
            result = statement.executeQuery(query);
            result.next();
            id_typePusher = result.getLong("id_typePusher");
        } catch (SQLException e) {
            throw new BaseDataException("ошибка получения id в журнале типов толкателей", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) {
        }
        return id_typePusher;
    }

    // -----
    // количество толкателей заданого типа
    @Override
    public int getCountPushersFromType(long id_typePusher, String[] targetNamePusher) throws BaseDataException {
        if (targetNamePusher.length != 1) throw  new BaseDataException("ошибка указателя имени", Status.ERROR);
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        int count;

        try {
            statement = connection.createStatement();
            String query =
                    "SELECT " +
                            " COUNT(pushers.id_pusher) AS c, " +
                            " pushers_logger.namePusher " +
                            " FROM " + baseDat + ".pushers " +
                            " INNER JOIN " + baseDat + ".pushers_logger ON pushers.id_loggerPusher = pushers_logger.id_loggerPusher " +
                            " WHERE " +
                            " pushers_logger.id_typePusher = " + id_typePusher + " AND " +
                            " pushers.date_unreg IS NULL ";
            result = statement.executeQuery(query);
            result.next();
            count = result.getInt("c");
            targetNamePusher[0] = result.getString("namePusher");
        } catch (SQLException e) { throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException e) { }
        return count;
    }
    // тип толкателя
    @Override
    public TypePusher getTypePusher(long id_typePusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        TypePusher typePusher;
        String query = "SELECT "+
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
                " WHERE " +
                " pusherstype.id_typePusher = " + id_typePusher + " ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            result.next();
        } catch (SQLException e) { throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            typePusher = new TypePusher(
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
            );
            result.close();
            statement.close();
        } catch (SQLException e) { throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        }
        return typePusher;
    }
    // новые параметры spec
    @Override
    public void writeDataSpec(long id_user, long id_pusher) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(false);

        PreparedStatement preStatement;
        java.sql.Timestamp timestamp = new Timestamp(new Date().getTime());
        //
        try {
            preStatement = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".data_spec " +
                            " (date_upd, id_user, id_pusher) " +
                            " VALUES(?, ?, ?) "
            );
            preStatement.setTimestamp(1, timestamp);
            preStatement.setLong(2, id_user);
            preStatement.setLong(3, id_pusher);
            preStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) { try { connection.rollback();
        } catch (SQLException se) { e = new SQLException("ошибка отмены транзакции" + se.getMessage(), e);
        } throw new BaseDataException(e, Status.SQL_TRANSACTION_ERROR);
        }
        try { preStatement.close();
        } catch (SQLException e) {
        }
    }
    // последний spec
    @Override
    public long getIdLastDataSpec() throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        long id_dataSpec;
        String query = "SELECT data_spec.id_dataSpec " +
                " FROM " + baseDat + ".data_spec " +
                " ORDER BY data_spec.id_dataSpec DESC " +
                " LIMIT 1 ";

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            result.next();
            id_dataSpec = result.getLong(1);
        } catch (SQLException e) { throw new BaseDataException("ошибка транзакции", e, Status.SQL_TRANSACTION_ERROR);
        }
        return id_dataSpec;
    }
    public DataSpec getLastDataSpec() throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        DataSpec dataSpec = null;
        String query = "SELECT id_dataSpec, date_upd, id_user, id_pusher " +
                " FROM " + baseDat + ".data_spec " +
                " ORDER BY id_dataSpec DESC " +
                " LIMIT 1 ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            if (!((ResultSetImpl) result).getRows().isEmpty()) {
                result.next();
                dataSpec = new DataSpec(
                        result.getLong("id_dataSpec"),
                        result.getLong("id_user"),
                        result.getLong("id_pusher"),
                        result.getTimestamp("date_upd")
                );
            }
        } catch (SQLException e) { throw new BaseDataException("получение последней спецификации", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException e) { }
        return dataSpec;
    }
    // -----
    protected void internalCheckConnect() throws BaseDataException {
        if (connection == null) { throw new BaseDataException("соединение не установлено", Status.CONNECT_NO_CONNECTION); }
        boolean fl;
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
    // ==========

    @Override
    public ArrayList<String> getListFromYear() throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT DISTINCT " +
                " DATE_FORMAT(datas.dateTime, '%Y') AS lYYYY" +
                " FROM " + baseDat + ".datas " +
                " ORDER BY datas.id_data ASC ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            while (result.next()) {
                list.add(result.getString("lYYYY"));
            }
        } catch (SQLException e) {
            throw new BaseDataException("список годов", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException e) { }
        return list;
    }

    @Override
    public ArrayList<String> getListFromMounth(String year) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT DISTINCT " +
                " DATE_FORMAT(datas.dateTime, '%Y-%m') AS list" +
                " FROM " + baseDat + ".datas " +
                " WHERE DATE_FORMAT(datas.dateTime, '%Y') = '" + year + "' " +
                " ORDER BY datas.id_data ASC ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            while (result.next()) {
                list.add(result.getString("list"));
            }
        } catch (SQLException e) {
            throw new BaseDataException("список месяцев", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) { }
        return list;
    }

    @Override
    public ArrayList<String> getListFromDates(String mounth) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT DISTINCT " +
                " DATE_FORMAT(datas.dateTime, '%Y-%m-%d') AS list" +
                " FROM " + baseDat + ".datas " +
                " WHERE DATE_FORMAT(datas.dateTime, '%Y-%m') = '" + mounth + "' " +
                " ORDER BY datas.id_data ASC ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            while (result.next()) {
                list.add(result.getString("list"));
            }
        } catch (SQLException e) {
            throw new BaseDataException("список месяцев", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) { }
        return list;
    }

    @Override
    public ArrayList<DataUnit> getListFromPusherChecks(String date) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        ArrayList<DataUnit> list = new ArrayList<>();
        String query = "SELECT " +
                " datas.id_data, " +
                " datas.n_cicle, " +
                " pushers_logger.namePusher, " +
                " pusherstype_logger.nameType, " +
                " datas.dateTime " +
                " FROM " + baseDat + ".datas " +
                " INNER JOIN " + baseDat + ".data_spec ON datas.id_spec = data_spec.id_dataSpec " +
                " INNER JOIN " + baseDat + ".pushers ON data_spec.id_pusher = pushers.id_pusher " +
                " INNER JOIN " + baseDat + ".pushers_logger ON pushers.id_loggerPusher = pushers_logger.id_loggerPusher " +
                " INNER JOIN " + baseDat + ".pusherstype ON pushers_logger.id_typePusher = pusherstype.id_typePusher " +
                " INNER JOIN " + baseDat + ".pusherstype_logger ON pusherstype.id_loggerTypePusher = pusherstype_logger.id_loggerTypePusher " +
                " WHERE DATE_FORMAT(datas.dateTime, '%Y-%m-%d') = '" + date + "' " +
                " ORDER BY datas.id_data ASC ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            while (result.next()) {
                list.add(
                        new DataUnit(
                                result.getLong("id_data"),
                                result.getInt("n_cicle"),
                                result.getString("namePusher"),
                                result.getString("nameType"),
                                result.getTimestamp("dateTime")
                        )
                );
            }
        } catch (SQLException e) {
            throw new BaseDataException("список проверенных толкателей", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) {        }
        return list;
    }
// ==========

    @Override
    public DataUnitMeasured getDataMeasured(long id_data) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        DataUnitMeasured dataUnitMeasured;
        String query = "SELECT " +
                " datas.id_data, " +
                " datas.dateTime, " +
                " datas.id_spec, " +
                " datas.n_cicle, " +
                " datas.ves, " +
                " datas.tik_shelf, " +
                " datas.tik_back, " +
                " datas.tik_stop, " +
                " datas.forceNominal, " +
                " datas.moveNominal, " +
                " datas.unclenchingTime, " +
                " datas.dataMeasured, " +
                " data_spec.id_user, " +
                " data_spec.id_pusher " +
                " FROM " + baseDat + ".datas " +
                " INNER JOIN " + baseDat + ".data_spec ON datas.id_spec = data_spec.id_dataSpec " +
                " WHERE datas.id_data = '" + id_data + "' ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            result.next();
            dataUnitMeasured = new DataUnitMeasured(
                    result.getLong("id_data"),
                    result.getTimestamp("dateTime"),
                    result.getLong("id_spec"),
                    result.getInt("n_cicle"),
                    result.getInt("ves"),
                    result.getInt("tik_shelf"),
                    result.getInt("tik_back"),
                    result.getInt("tik_stop"),
                    result.getInt("forceNominal"),
                    result.getInt("moveNominal"),
                    result.getInt("unclenchingTime"),
                    result.getBlob("dataMeasured"),
                    result.getLong("id_user"),
                    result.getLong("id_pusher")
            );
        } catch (SQLException e) {
            throw new BaseDataException("измеренные данные", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) { }
        return dataUnitMeasured;
    }
    // чтение данных о пользователе
    @Override
    public User getUser(long id_user) throws BaseDataException {
        internalCheckConnect();
        internalAutoCommit(true);

        Statement statement;
        ResultSet result;
        User user;
        String query = "SELECT " +
                " users.id_user, " +
                " users.date_reg, " +
                " users_logger.id_loggerUser, " +
                " users_logger.date_upd, " +
                " users_logger.id_loggerUserEdit, " +
                " users_logger.surName, " +
                " users_logger.userPassword, " +
                " users_logger.rang, " +
                " users.date_unreg " +
                " FROM " + baseDat + ".users " +
                " INNER JOIN " + baseDat + ".users_logger ON users.id_loggerUser = users_logger.id_loggerUser " +
                " WHERE users.id_user = '" + id_user + "' " +
                " LIMIT 1 ";
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            result.next();
            user = new User(
                    result.getLong("id_user"),
                    result.getTimestamp("date_reg"),
                    result.getLong("id_loggerUser"),
                    result.getTimestamp("date_upd"),
                    result.getLong("id_loggerUserEdit"),
                    result.getString("surName"),
                    result.getString("userPassword"),
                    result.getInt("rang"),
                    result.getTimestamp("date_unreg")
            );
        } catch (SQLException e) {
            throw new BaseDataException("данные пользователе", e, Status.SQL_TRANSACTION_ERROR);
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) { }
        return user;
    }
}
