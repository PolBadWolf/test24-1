package org.example.test24.bd;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;

public interface BaseData1 {
    int MS_SQL = 0;
    int MY_SQL = 1;
    int ERROR = 99;
    enum TypeBaseData {
        MS_SQL  (BaseData1.MS_SQL),
        MY_SQL  (BaseData1.MY_SQL),
        ERROR   (BaseData1.ERROR);
        private int typeBaseData;

        TypeBaseData(int typeBaseData) {
            this.typeBaseData = typeBaseData;
        }
        public int getTypeBaseData() {
            return typeBaseData;
        }
        public String getTypeBaseDataString() {
            return typeBaseDataString(typeBaseData);
        }
    }
    static BaseData2.TypeBaseData typeBaseDataCode(String nameTypeBaseData) {
        BaseData2.TypeBaseData typeBaseData;
        switch (nameTypeBaseData.toUpperCase()) {
            case "MS_SQL":
                typeBaseData = BaseData2.TypeBaseData.MS_SQL;
                break;
            case "MY_SQL":
                typeBaseData = BaseData2.TypeBaseData.MY_SQL;
                break;
            default:
                typeBaseData = BaseData2.TypeBaseData.ERROR;
        }
        return typeBaseData;
    }
    static String typeBaseDataString(TypeBaseData codeTypeBaseData) {
        String stroka;
        switch (codeTypeBaseData) {
            case MS_SQL:
                stroka = "MS_SQL";
                break;
            case MY_SQL:
                stroka = "MY_SQL";
                break;
            default:
                stroka = "ERROR";
        }
        return stroka;
    }
    static String typeBaseDataString(int codeTypeBaseData) {
        String stroka;
        switch (codeTypeBaseData) {
            case BaseData1.MS_SQL:
                stroka = "MS_SQL";
                break;
            case BaseData1.MY_SQL:
                stroka = "MY_SQL";
                break;
            default:
                stroka = "ERROR";
        }
        return stroka;
    }

    void connectBd() throws Exception;
    Connection getConnect() throws Exception;
    String getTypeBD();
    boolean testStuctBase(String ip, String portServer, String login, String password, String base);
    String[] getConnectListBd(String ip, String portServer, String login, String password) throws Exception;
    ParametersSql2 getParametrsSql();
    // запись
    void pushDataDist(Date date, long id_spec, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws Exception;
    // обновить пароль
    void updateUserPassword(User userClass, String newPassword) throws Exception;
    // список пользователей
    User[] getListUsers(boolean actual) throws Exception;
    // деактивация пользователя
    void deactiveUser(int id) throws Exception;
    // запись нового пользователя
    void writeNewUser(String name, String password) throws Exception;
    void setParametersSql(String[] fileNameSql);


    static BaseData1 init(String typeBase, String[] fileNameSql) {
        BaseData1 baseData1 = null;
        switch (typeBase) {
            case "MS_SQL" :
                baseData1 = new BaseData1ClassMsSql();
                break;
            case "MY_SQL" :
                baseData1 = new BaseData1ClassMySql();
                break;
            default:
                System.out.println("неизвестный тип BD");
                break;
        }
        if (baseData1 != null)   baseData1.setParametersSql(fileNameSql);
        return baseData1;
    }
    static BaseData1 init(TypeBaseData typeBaseData, String[] fileNameSql) {
        BaseData1 baseData1 = null;
        switch (typeBaseData) {
            case MS_SQL:
                baseData1 = new BaseData1ClassMsSql();
                break;
            case MY_SQL:
                baseData1 = new BaseData1ClassMySql();
                break;
            default:
                System.out.println("неизвестный тип BD");
                break;
        }
        if (baseData1 != null)   baseData1.setParametersSql(fileNameSql);
        return baseData1;
    }

    static String[] getConnectListBd(String typeBD, String ip, String portServer, String login, String password) throws Exception {
        String[] list;
        switch (typeBD) {
            case "MS_SQL" :
                list = BaseData1ClassMsSql.getConnectListBd1(ip, portServer, login, password);
                break;
            case "MY_SQL" :
                list = BaseData1ClassMySql.getConnectListBd1(ip, portServer, login, password);
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
                res = BaseData1ClassMsSql.testStuctBase1(ip, portServer, login, password, base);
                break;
            case "MY_SQL" :
                res = BaseData1ClassMySql.testStuctBase1(ip, portServer, login, password, base);
                break;
            default:
                res = false;
        }
        return res;
    }
}
