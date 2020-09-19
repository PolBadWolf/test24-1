package org.example.test24.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.example.test24.bd.BaseData2.*;

class BaseDataParent2 implements BaseDataInterface2 {
    protected BaseData2.Parameters testParameters;
    protected BaseData2.Parameters workParameters;
    protected Connection testConnection;
    protected Connection workConnection;

    public BaseDataParent2() {
    }
    // тестовое соединение
    @Override
    public BaseData2.Status createTestConnect(BaseData2.Parameters parameters) {
        return BaseData2.Status.UNKNOWN_ERROR;
    }
    // тестовое соединение проверка структуры БД
    @Override
    public BaseData2.Status checkCheckStructureBd(String base) {
        return BaseData2.Status.UNKNOWN_ERROR;
    }
    // -----------------------------------------------------------
    // инициализация рабочего соединения
    @Override
    public BaseData2.Status createWorkConnect(Parameters parameters) {
        return BaseData2.Status.UNKNOWN_ERROR;
    }
    // чтение списка пользователей
    @Override
    public UserClass[] getListUsers(boolean actual) throws Exception {
        if (workConnection == null) {
            throw new Exception("Не инициировано рабочее соединение");
        }
        if (workConnection.isClosed()) {
            throw new Exception("Не активно рабочее соединение");
        }
        ArrayList<UserClass> listUsers = new ArrayList<>();
        Statement statement = null;
        ResultSet result = null;
        boolean saveAutoCommit = true;
        // save auto commit
        try {
            saveAutoCommit = workConnection.getAutoCommit();
        } catch (SQLException e) {
            throw new Exception("Ошибка начала транзакции: " + e.getMessage());
        }
        // запрос на список пользователей
        try {
            workConnection.setAutoCommit(false);
            workConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String tab = "table_users";
            statement = workConnection.createStatement();
            // запрос
            if (actual) {
                result = statement.executeQuery(
                        "SELECT id, date_reg, date_unreg, name, password, rang " +
                                "FROM " + tab + " " +
                                "WHERE (date_unreg IS NULL) " +
                                "ORDER BY id "
                );
            } else {
                result = statement.executeQuery(
                        "SELECT id, date_reg, date_unreg, name, password, rang " +
                                "FROM " + tab + " " +
                                "ORDER BY id "
                );
            }
            // завершение транзакции
            workConnection.commit();
            workConnection.setAutoCommit(saveAutoCommit);
        } catch (SQLException e) {
            try {
                // отмена транзакции
                workConnection.rollback();
                workConnection.setAutoCommit(saveAutoCommit);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new Exception("Ошибка выполнения транзакции: " + e.getMessage());
        }
        // создание списка
        try {
            while (result.next()) {
                String pass = null;
                try {
                    pass = Password.decoding(result.getString("password"));
                } catch (Exception e) {
                    e.printStackTrace();
                    pass = "";
                }
                /*listUsers.add(
                        new UserClass(
                                result.getInt("id"),
                                result.getTimestamp("date_reg"),
                                result.getTimestamp("date_unreg"),
                                result.getString("name"),
                                pass,
                                result.getInt("rang") // user status
                        )
                );*/
            }
        } catch (SQLException e) {
            throw new Exception("Ошибка выполнения парсинга: " + e.getMessage());
        }
        // закрытие соединения
        try {
            result.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listUsers.toArray(new UserClass[0]);
    }


    @Override
    public String[] getListBd() throws Exception {
        return new String[0];
    }

    // тестовое соединение список доступных баз
    @Override
    public boolean requestListBdFrom(Consumer<String[]> list) {
        return false;
    }
    // установка нового пароля пользователя
    @Override
    public boolean setUserNewPassword(UserClass user, String newPassword) {
        try {
        if (workConnection.isClosed()) return false;
        } catch (SQLException e) {
            return false;
        }
        boolean saveAutoCommit;
        PreparedStatement preparedStatement;
        ResultSet result;
        try {
            saveAutoCommit = workConnection.getAutoCommit();
            workConnection.setAutoCommit(true);
        } catch (SQLException throwables) {
            return false;
        }
        try {
            preparedStatement = workConnection.prepareStatement(
                    "UPDATE Table_users SET  password = ? WHERE id = ?"
            );
        } catch (SQLException throwables) {
            try {
                workConnection.setAutoCommit(saveAutoCommit);
            } catch (SQLException e) {
            }
            return false;
        }
        try {
            preparedStatement.setString(1, BaseData2.Password.encoding(newPassword));
            preparedStatement.setInt(2, user.id);
            int r  = preparedStatement.executeUpdate();
            System.out.println("pass upd res = " + r);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }
}
