package org.example.bd;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

class Ms_msql implements Sql_interface {
    private ParametersSql parametersSql = null;

    public Ms_msql(ParametersSql parametersSql) {
        this.parametersSql = parametersSql;
    }

    @Override
    public void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) {
        if (getConnect() == null) {
            // нет связи
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(
                    //"BEGIN TRANSACTION\n"
                    "INSERT INTO Table_1 (dateTime, id_spec, n_cicle, ves, tik_shelf, tik_back, tik_stop, dis)\n"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)\n"
                    //+ "COMMIT"
            );
            statement.setTimestamp(1, new java.sql.Timestamp(date.getTime()) );
            statement.setLong(2, id_spec);
            statement.setInt(3, n_cicle);
            statement.setInt(4, ves);
            statement.setInt(5, tik_shelf);
            statement.setInt(6, tik_back);
            statement.setInt(7, tik_stop);
            statement.setBlob(8, distance);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
