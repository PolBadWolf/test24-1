package org.example.test24.bd;

import java.sql.*;
import java.util.ArrayList;

import static org.example.test24.bd.BaseData.*;

class BaseDataParent implements BaseDataInterface {
    protected BaseData.Parameters testParameters;
    protected BaseData.Parameters workParameters;
    protected Connection testConnection;
    protected Connection workConnection;

    public BaseDataParent() {
    }
    // тестовое соединение
    @Override
    public int testConnectInit(BaseData.Parameters parameters) {
        return UNKNOWN_ERROR;
    }
    // тестовое соединение список доступных баз
    @Override
    public String[] testConnectListBd() throws Exception {
        return new String[0];
    }
    // тестовое соединение проверка структуры БД
    @Override
    public int testConnectCheckStructure(String base) {
        return UNKNOWN_ERROR;
    }
    // инициализация рабочего соединения
    @Override
    public int workConnectInit(Parameters parameters) {
        return UNKNOWN_ERROR;
    }
    // чтение списка пользователей
    @Override
    public UserClass[] getListUsers(boolean actual) throws Exception {
        if (workConnection == null) {
            throw new Exception("BaseDataParent.getListUsers: CONNECT_ERROR -> workConnection");
        }
        ArrayList<UserClass> listUsers = new ArrayList<>();
        PreparedStatement statement;
        ResultSet result;
        boolean saveAutoCommit;
        try {
            saveAutoCommit = workConnection.getAutoCommit();
        } catch (SQLException throwables) {
            throw new Exception("BaseDataParent.getListUsers: getAutoCommit");
        }
        // запрос на список пользователей
        try {
            workConnection.setAutoCommit(false);
            workConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            if (actual) {
                statement = workConnection.prepareStatement(
                        "SELECT        id, date_reg, date_unreg, name, password\n" +
                                "FROM            ?\n" +
                                "WHERE        (date_unreg IS NULL)\n" +
                                "ORDER BY id"
                );
            } else {
                statement = workConnection.prepareStatement(
                        "SELECT        id, date_reg, date_unreg, name, password\n" +
                                "FROM            ?\n" +
                                "ORDER BY id"
                );
            }
            statement.setString(1, "table_users");
            result = statement.executeQuery();
            workConnection.commit();
            workConnection.setAutoCommit(saveAutoCommit);
        } catch (SQLException throwables) {
            try {
                workConnection.rollback();
                workConnection.setAutoCommit(saveAutoCommit);
            } catch (SQLException e) { }
            throw new Exception("BaseDataParent.getListUsers: executeQuery");
        }
        // создание списка
        try {
            while (result.next()) {
                String pass = null;
                try {
                    pass = Password.decoding(result.getString("password"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listUsers.add(
                        new UserClass(
                                result.getInt("id"),
                                result.getTimestamp("date_reg"),
                                result.getTimestamp("date_unreg"),
                                result.getString("name"),
                                pass
                        )
                );
            }
        } catch (SQLException throwables) {
            throw new Exception("BaseDataParent.getListUsers: parsing list");
        }
        try {
            result.close();
            statement.close();
        } catch (SQLException throwables) {
            throw new Exception("BaseDataParent.getListUsers: close");
        }
        //
        return listUsers.toArray(new UserClass[0]);
    }
}
