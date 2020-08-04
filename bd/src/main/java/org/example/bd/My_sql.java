package org.example.bd;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;

class My_sql implements Sql_interface {
    private final String fileNameProperties = "my_sql.txt";
    private ParametersSql parametersSql = null;
    private Connection connection = null;

    public My_sql() {
        parametersSql = new ParametersSql(fileNameProperties);
    }

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) {

    }

    @Override
    public Connection getConnect() {
        return null;
    }

}
