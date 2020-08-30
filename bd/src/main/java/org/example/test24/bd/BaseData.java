package org.example.test24.bd;

public interface BaseData {
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
        public String toString() {
            return BaseDataClass.typeBaseDataString(typeBaseData);
        }
    }
    //
    interface CallBack {

    }
    class Parameters {
        String  ip;
        int     port;
        String  login;
        String  password;
        String  base;
    }
    // проверка подключения (логин/пароль)
    boolean checkConnect(TypeBaseData typeBaseData, Parameters parameters);
    // проверка подключения (логин/пароль + база)
    boolean checkConnectBase(TypeBaseData typeBaseData, Parameters parameters);
    // проверка структуры базы
    boolean checkStructBase(TypeBaseData typeBaseData, Parameters parameters);
    // список пользователей в заданом подключении
    String[] getListUsers(TypeBaseData typeBaseData, Parameters parameters);
    // список пользовательских БД
    String[] getListBaseData(TypeBaseData typeBaseData, Parameters parameters);
    //-----------------
    // список пользователей в текущем подкдлючении
    String[] getListUsers();
}
