package org.example.bd;

import org.example.test24.allinterface.bd.UserClass;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;

import static org.example.bd.DataBaseClass.dataBaseClass;

public interface DataBase {

    static DataBase init(String typeBase, String[] fileNameSql) {
        dataBaseClass = null;
        switch (typeBase) {
            case "MS_SQL" :
                dataBaseClass = new DataBaseClassMsSql();
                break;
            case "MY_SQL" :
                dataBaseClass = new DataBaseClassMySql();
                break;
            default:
                System.out.println("неизвестный тип BD");
                break;
        }
        if (dataBaseClass != null)   dataBaseClass.setParametersSql(fileNameSql);
        return dataBaseClass;
    }


    Connection getConnect() throws Exception;
    String getTypeBD();
    boolean testStuctBase(String ip, String portServer, String login, String password, String base);

    String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception;

    ParametersSql getParametrsSql();
    // запись
    void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception;
    // обновить пароль
    void updateUserPassword(UserClass userClass, String newPassword) throws Exception;
    // список пользователей
    UserClass[] getListUsers(boolean actual) throws Exception;
    // деактивация пользователя
    void deactiveUser(int id) throws Exception;
    // запись нового пользователя
    void writeNewUser(String name, String password) throws Exception;


    static String[] getConnectListBd(String typeBD, String ip, String portServer, String login, String password) throws Exception {
        String[] list;
        switch (typeBD) {
            case "MS_SQL" :
                list = DataBaseClassMsSql.getConnectListBd1(ip, portServer, login, password);
                break;
            case "MY_SQL" :
                list = DataBaseClassMySql.getConnectListBd1(ip, portServer, login, password);
                break;
            default:
                throw new Exception("неизвестный тип BD");
        }
        return list;
    }
    static String getNameFileParametrsSql(String typeDb, String[] fileNameSql) {
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
    static boolean testStuctBase(String typeBD, String ip, String portServer, String login, String password, String base) {
        boolean res;
        switch (typeBD) {
            case "MS_SQL" :
                res = DataBaseClassMsSql.testStuctBase1(ip, portServer, login, password, base);
                break;
            case "MY_SQL" :
                res = DataBaseClassMySql.testStuctBase1(ip, portServer, login, password, base);
                break;
            default:
                res = false;
        }
        return res;
    }
}
