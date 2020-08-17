package org.example.bd;

import org.example.test24.allinterface.bd.UserClass;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;

public interface SqlWork_interface {
    Connection getConnect() throws Exception;
    String getTypeBD();
    boolean testStuctBase(String ip, String portServer, String login, String password, String base);
    String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception;
    ParametersSql getParametrsSql();
    void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception;
    UserClass[] getListUsers(boolean actual) throws Exception;
    void updateUserPassword(UserClass userClass, String newPassword) throws Exception;
}
