package org.example.bd;

import java.sql.*;
import java.util.Date;

public class BdWork implements SqlWork_interface {
    private SqlWork_interface sql_Work_interface = null;

    public static String BdSelectFileParam(String typeDb, String[] fileNameSql) {
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
    public BdWork(String typeDb, String[] fileNameSql) throws SQLException {
        switch (typeDb) {
            case "MS_SQL":
                sql_Work_interface = new Ms_sql(fileNameSql[0]);
                break;
            case  "MY_SQL":
                sql_Work_interface = new My_sql();
                break;
            default:
                throw new SQLException("неизвестный тип BD");
        }
    }

    @Override
    public Connection getConnect() throws Exception {
        return sql_Work_interface.getConnect();
    }

    @Override
    public String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        return sql_Work_interface.getConnectListBd(ip, portServer, login, password);
    }

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception {
        sql_Work_interface.pushDataDist(date, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, distance);
    }

    @Override
    public boolean testStuctBase(String ip, String portServer, String login, String password, String base) {
        return sql_Work_interface.testStuctBase(ip, portServer, login, password, base);
    }

    @Override
    public ParametersSql getParametrsSql() {
        return sql_Work_interface.getParametrsSql();
    }

    @Override
    public String getTypeBD() {
        return null;
    }
}
