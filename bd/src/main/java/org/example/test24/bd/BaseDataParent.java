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
    // открытие соединение с БД
    @Override
    public void openConnect(Parameters parameters) throws Exception { }
    // чтение списка БД
    @Override
    public String[] getListBase() throws Exception { return new String[0]; }
    // чтение списка пользователей
    @Override
    public UserClass[] getListUsers(boolean actual) throws Exception {
        if (connection == null) throw new Exception("отсутствует соединение (connection == null)");
        boolean flClosed = connection.isClosed();
        if (flClosed) throw new Exception("соединение закрыто");

        ArrayList<UserClass> listUsers = new ArrayList<>();
        Statement statement;
        ResultSet result;
        // запрос на список пользователей
        String tab = "table_users";
        statement = connection.createStatement();
        // запрос
        if (actual) {
            result = statement.executeQuery(
                    "SELECT id, date_reg, name, password, rang, id_unreg " +
                            "FROM " + baseDat + "." + tab + " " +
                            "WHERE (id_unreg IS NULL) OR (id_unreg < 0) " +
                            "ORDER BY id "
            );
        } else {
            result = statement.executeQuery(
                    "SELECT id, date_reg, name, password, rang, id_unreg " +
                            "FROM " + baseDat + "." + tab + " " +
                            "ORDER BY id "
            );
        }
        // создание списка
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
                                result.getString("name"),
                                pass,
                                result.getInt("rang"), // user status
                                result.getInt("id_unreg")
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
        return listUsers.toArray(new UserClass[0]);
    }
    // проверка структуры БД
    @Override
    public boolean checkCheckStructureBd(String base) throws Exception {
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        boolean table_data, table_users, table_pushers;
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
                        "rang",
                        "id_unreg"
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
    // чтение списка толкателей
    @Override
    public Pusher[] getListPushers(boolean actual) throws Exception {
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
    // запись нового пользователя
    @Override
    public void writeNewUser(int id_edit, String sunName, String password, int rang) throws Exception
    {
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        boolean saveAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement preStatementUser = null;
        PreparedStatement preStatementLogger = null;
        try {
            String pass = new String(java.util.Base64.getEncoder().encode(password.getBytes()));
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            preStatementUser = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".Table_users (name, password, rang, date_reg)\n"
                            + " VALUES (?, ?, ?, ?)"
            );
            preStatementUser.setString(1, sunName);
            preStatementUser.setString(2, pass);
            preStatementUser.setInt(3, rang);
            preStatementUser.setTimestamp(4, timestamp);
            preStatementUser.executeUpdate();
            preStatementLogger = connection.prepareStatement(
                    "INSERT INTO " + baseDat + ".logger_users (date, idUser_edit, act, idUser_new, name_new, password_new, rang_new) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) "
            );
            preStatementLogger.setTimestamp(1, timestamp);
            preStatementLogger.setInt(2, id_edit);
            preStatementLogger.setInt(3, 1);
            preStatementLogger.setLong(4, ((ClientPreparedStatement)preStatementUser).getLastInsertID());
            preStatementLogger.setString(5, sunName);
            preStatementLogger.setString(6, pass);
            preStatementLogger.setInt(7, rang);
            preStatementLogger.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            connection.rollback();
            throw new Exception(throwables);
        }
        connection.setAutoCommit(saveAutoCommit);
        preStatementUser.close();
        preStatementLogger.close();
    }
    // деактивация пользователя
    @Override
    public void deativateUser(int source_id, int target_id) throws Exception {
        if (connection == null) throw new Exception("соединение не установлено");
        boolean fl = connection.isClosed();
        if (fl) throw new Exception("соединение закрыто");

        PreparedStatement preStatement;
        java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        preStatement = connection.prepareStatement(
                "UPDATE " + baseDat + ".Table_users SET " +
                        "date_unreg = ?, " +
                        "id_unreg = ? " +
                        "WHERE id = ? "
        );
        preStatement.setTimestamp(1, timestamp);
        preStatement.setInt(2, source_id);
        preStatement.setInt(3, target_id);
        preStatement.executeUpdate();
        preStatement.close();
    }
}
