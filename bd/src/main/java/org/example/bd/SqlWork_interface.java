package org.example.bd;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;

interface SqlWork_interface {
    Connection getConnect() throws Exception;

    void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception;
    boolean testStuctBase(String ip, String portServer, String login, String password, String base);
    ParametersSql getParametrsSql();
}
