package org.example.bd;

import java.sql.*;
import java.util.Date;

public class BdWork {
    private Sql_interface sql_interface = null;

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
                sql_interface = new Ms_sql(fileNameSql[0]);
                break;
            case  "MY_SQL":
                sql_interface = new My_sql();
                break;
            default:
                throw new SQLException("неизвестный тип BD");
        }
    }

    public Connection getConnect() throws Exception {
        return sql_interface.getConnect();
    }

    public String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception {
        return sql_interface.getConnectListBd(ip, portServer, login, password);
    }

    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception {
        sql_interface.pushDataDist(date, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, distance);
    }

    public boolean testStuctBase() {
        return sql_interface.testStuctBase();
    }
}
