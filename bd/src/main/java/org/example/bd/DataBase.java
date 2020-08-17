package org.example.bd;

import org.example.test24.allinterface.bd.UserClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public abstract class DataBase implements SqlWork_interface {
    protected Connection connection = null;
    protected static DataBase dataBase = null;
    protected ParametersSql parametersSql = null;

    public static SqlWork_interface init(String typeBase, String[] fileNameSql) {
        dataBase = null;
        switch (typeBase) {
            case "MS_SQL" :
                dataBase = new DataBaseMsSql();
                break;
            case "MY_SQL" :
                dataBase = new DataBaseMySql();
                break;
            default:
                System.out.println("неизвестный тип BD");
                break;
        }
        if (dataBase != null)   dataBase.setParametersSql(fileNameSql);
        return (SqlWork_interface) dataBase;
    }

    protected abstract void setParametersSql(String[] fileNameSql);

    protected abstract void connectBd() throws Exception;

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
        return DataBase.testStuctBase(getTypeBD(), ip, portServer, login, password, base);
    }

    @Override
    public String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        return DataBase.getConnectListBd(getTypeBD(), ip, portServer, login, password);
    }

    @Override
    public ParametersSql getParametrsSql() {
        return parametersSql;
    }

    @Override
    public abstract String getTypeBD();

    public static String getNameFileParametrsSql(String typeDb, String[] fileNameSql) {
        String s = "";
        switch (typeDb) {
            case "MS_SQL":
                s = fileNameSql[0];
                break;
            case "MY_SQL":
                s = fileNameSql[1];
                break;
        }
        return s;
    }

    public static boolean testStuctBase(String typeBD, String ip, String portServer, String login, String password, String base) {
        boolean res = false;
        switch (typeBD) {
            case "MS_SQL" :
                res = DataBaseMsSql.testStuctBase1(ip, portServer, login, password, base);
                break;
            case "MY_SQL" :
                res = DataBaseMySql.testStuctBase1(ip, portServer, login, password, base);
                break;
            default:
                res = false;
        }
        return res;
    }

    public static String[] getConnectListBd(String typeBD, String ip, String portServer, String login, String password) throws Exception {
        String[] list;
        switch (typeBD) {
            case "MS_SQL" :
                list = DataBaseMsSql.getConnectListBd1(ip, portServer, login, password);
                break;
            case "MY_SQL" :
                list = DataBaseMySql.getConnectListBd1(ip, portServer, login, password);
                break;
            default:
                throw new Exception("неизвестный тип BD");
        }
        return list;
    }

    @Override
    public UserClass[] getListUsers(boolean actual) throws Exception {
        ArrayList<UserClass> listUsers = new ArrayList<>();
        PreparedStatement statement = null;
        Statement statementReadSpec = null;
        ResultSet result = null;
        boolean saveAutoCommit = false;
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
                            result.getTimestamp("date_unreg"),
                            result.getString("name"),
                            pass
                    ));
                } catch (java.lang.Throwable throwable) {
                    throwable.printStackTrace();
                }
                connection.setAutoCommit(saveAutoCommit);
            }
        } catch (SQLException e) {
            throw new Exception("ошибка чтения списка пользователей");
        }
        return listUsers.toArray(new UserClass[listUsers.size()]);
    }

    @Override
    public abstract void updateUserPassword(UserClass userClass, String newPassword) throws Exception;
}
