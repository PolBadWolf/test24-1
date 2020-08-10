package org.example.bd;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;

interface Sql_interface {
    void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception;
    Connection getConnect() throws Exception;
    String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception;
    boolean testStuctBase();
}
