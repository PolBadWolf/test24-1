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
    public String[] testConnectListBd() {
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
        Statement statement;
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
            String tab = "table_users";
            statement = workConnection.createStatement();
            if (actual) {
                result = statement.executeQuery(
                        "SELECT id, date_reg, date_unreg, name, password " +
                                "FROM " + tab + " " +
                                "WHERE (date_unreg IS NULL) " +
                                "ORDER BY id "
                );
            } else {
                result = statement.executeQuery(
                        "SELECT id, date_reg, date_unreg, name, password " +
                                "FROM " + tab + " " +
                                "ORDER BY id "
                );
            }
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
