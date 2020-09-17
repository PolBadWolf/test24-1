package org.example.test24.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

abstract class BaseData1Class implements BaseData1 {
    protected Connection connection = null;
    protected ParametersSql2 parametersSql = null;

    @Override
    public abstract void setParametersSql(String[] fileNameSql);

    //protected abstract void connectBd() throws Exception;

    @Override
    public Connection getConnect() throws Exception {
        if (connection == null) connectBd();
        else {
            try {
                if (connection.isClosed())  connectBd();
            } catch (SQLException e) {
                e.printStackTrace();
                connection = null;
            }
        }
        boolean flag = true;
        try {
            flag = connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            connection = null;
        }
        if (flag)   connection = null;
        return connection;
    }

    @Override
    public abstract void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception;

    @Override
    public boolean testStuctBase(String ip, String portServer, String login, String password, String base) {
        return BaseData1.testStuctBase(getTypeBD(), ip, portServer, login, password, base);
    }

    @Override
    public String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        return BaseData1.getConnectListBd(getTypeBD(), ip, portServer, login, password);
    }

    @Override
    public ParametersSql2 getParametrsSql() {
        return parametersSql;
    }

    @Override
    public abstract String getTypeBD();

    @Override
    public UserClass[] getListUsers(boolean actual) throws Exception {
        ArrayList<UserClass> listUsers = new ArrayList<>();
        Statement statementReadSpec;
        ResultSet result;
        boolean saveAutoCommit;
        try {
            getConnect();
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            statementReadSpec = connection.createStatement();
            //
            if (actual) {
                result = statementReadSpec.executeQuery("SELECT        id, date_reg, date_unreg, name, password\n" +
                        "FROM            Table_users\n" +
                        "WHERE        (date_unreg IS NULL)\n" +
                        "ORDER BY id");
            } else {
                result = statementReadSpec.executeQuery("SELECT        id, date_reg, date_unreg, name, password\n" +
                        "FROM            Table_users\n" +
                        "ORDER BY id");
            }
            while (result.next()) {
                String pass = "";
                try {
                    pass = new String(Base64.getDecoder().decode(result.getString("password")));
                } catch (java.lang.Throwable throwable) {
                    System.out.println("ошибка расшифровки пароля для : " + result.getString("name"));
                }
                try {
                    listUsers.add(new UserClass(
                            result.getInt("id"),
                            result.getTimestamp("date_reg"),
                            result.getString("name"),
                            pass,
                            0, // это статус
                            result.getInt("date_unreg")
                    ));
                } catch (java.lang.Throwable throwable) {
                    throwable.printStackTrace();
                }
                connection.setAutoCommit(saveAutoCommit);
            }
        } catch (SQLException e) {
            //throw new Exception("ошибка чтения списка пользователей");
            System.out.println("ошибка чтения списка пользователей");
        }
        return listUsers.toArray(new UserClass[0]);
    }

    @Override
    public abstract void updateUserPassword(UserClass userClass, String newPassword) throws Exception;

    @Override
    public void deactiveUser(int id) throws Exception {
        try {
            // проверка связи
            if (getConnect() == null) {
                throw new Exception("DataBaseClass.deactiveUser: нет связи");
            }
        } catch (Exception e) {
            throw new Exception("DataBaseClass.deactiveUser: " + e.getMessage());
        }
        // инициализация переменных
            PreparedStatement statement;
            boolean saveAutoCommit = true;
            // настройка auto commit
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException throwables) {
            throw new Exception("DataBaseClass.deactiveUser: " + throwables.getMessage());
        }
        try {
            java.util.Date date = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
            statement = connection.prepareStatement(
                    "UPDATE Table_users SET  date_unreg = ? WHERE id = ?"
            );
            statement.setTimestamp(1, timestamp);
            statement.setInt(2, id);
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException throwables) {
            connection.rollback();
            connection.setAutoCommit(saveAutoCommit);
            throw new Exception("DataBaseClass.deactiveUser: " + throwables.getMessage());
        }
    }

    // запись нового пользователя
    @Override
    public void writeNewUser(String name, String password) throws Exception {
        try {
            // проверка связи
            if (getConnect() == null) {
                throw new Exception("DataBaseClass.writeNewUser: нет связи");
            }
        } catch (Exception e) {
            throw new Exception("DataBaseClass.writeNewUser: " + e.getMessage());
        }
        // инициализация переменных
        PreparedStatement statement;
        boolean saveAutoCommit = true;
        // настройка auto commit
        try {
            saveAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException throwables) {
            throw new Exception("DataBaseClass.writeNewUser: " + throwables.getMessage());
        }
        try {
            java.util.Date date = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
            String pass = new String(java.util.Base64.getEncoder().encode(password.getBytes()));
            statement = connection.prepareStatement(
                    "INSERT INTO Table_users (date_reg, name, password)\n"
                            + " VALUES (?, ?, ?)"
            );
            statement.setTimestamp(1, timestamp);
            statement.setString(2, name);
            statement.setString(3, pass);
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(saveAutoCommit);
        } catch (SQLException throwables) {
            connection.rollback();
            connection.setAutoCommit(saveAutoCommit);
            throw new Exception("DataBaseClass.writeNewUser: " + throwables.getMessage());
        }
    }
}
