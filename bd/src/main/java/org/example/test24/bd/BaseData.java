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
    // -------------------------
    int OK = 0;
    int DRIVER_ERROR = 1;
    int CONNECT_ERROR = 2;
    int CONNECT_BASE_ERROR = 3;
    int QUERY_ERROR = 4;
    int STRUCTURE_ERROR = 5;
    int UNKNOWN_ERROR = 99;
    // -------------------------
    interface CallBack {

    }
    class Parameters {
        String  ip;
        String  port;
        String  login;
        String  password;
        String  base;

        public Parameters(String ip, String port, String login, String password, String base) {
            this.ip = ip;
            this.port = port;
            this.login = login;
            this.password = password;
            this.base = base;
        }
    }
    // создание тестового соединения
    void createTestConnect(TypeBaseData typeBaseData) throws IllegalStateException;
    // инициализация тестового соединения
    int testConnectInit(BaseData.Parameters parameters);
    // тестовое соединение список доступных БД
    String[] testConnectListBd() throws Exception;
    // тестовое соединение проверка структуры БД
    int testConnectCheckStructure(String base);
    // создание рабочего соединения
    void createWorkConnect(TypeBaseData typeBaseData) throws IllegalStateException;
    // инициализация рабочего соединения
    int workConnectInit(BaseData.Parameters parameters);
    /*
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
    */
}
