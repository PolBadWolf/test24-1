package org.example.bd;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public abstract class DataBase implements Sql_interface {
    protected Connection connection = null;
    protected static DataBase dataBase = null;
    protected ParametersSql parametersSql = null;

    public static Sql_interface init(String typeBase, String[] fileNameSql) {
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
        return (Sql_interface) dataBase;
    }
    protected abstract void setParametersSql(String[] fileNameSql);

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

    protected abstract void connectBd() throws Exception;

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception {

    }

    @Override
    public String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        return new String[0];
    }

    @Override
    public boolean testStuctBase(String ip, String portServer, String login, String password, String base) {
        return false;
    }

    @Override
    public ParametersSql getParametrsSql() {
        return null;
    }
}
