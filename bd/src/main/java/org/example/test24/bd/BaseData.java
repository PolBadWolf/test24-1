package org.example.test24.bd;

import java.util.Base64;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface BaseData {
    int MS_SQL = 0;
    int MY_SQL = 1;
    int ERROR = 99;
    enum TypeBaseData {
        MS_SQL  (BaseData.MS_SQL),
        MY_SQL  (BaseData.MY_SQL),
        ERROR   (BaseData.ERROR);
        private int typeBaseData;
        TypeBaseData(int typeBaseData) {
            this.typeBaseData = typeBaseData;
        }
        public int getCodeTypeBaseData() {
            return typeBaseData;
        }
        public String toString() {
            return BaseDataClass.typeBaseDataString(typeBaseData);
        }
    }
    // -------------------------
    int OK = 0;
    int UNEXPECTED_TYPE_BD = 1;
    int DRIVER_ERROR = 2;
    int CONNECT_PASS_ERROR = 3;
    int CONNECT_BASE_ERROR = 4;
    int CONNECT_ERROR = 5;
    int QUERY_ERROR = 6;
    int STRUCTURE_ERROR = 7;
    int UNKNOWN_ERROR = 99;
    enum Status {
        OK                  (BaseData.OK),
        UNEXPECTED_TYPE_BD  (BaseData.UNEXPECTED_TYPE_BD),
        DRIVER_ERROR        (BaseData.DRIVER_ERROR),
        CONNECT_PASS_ERROR  (BaseData.CONNECT_PASS_ERROR),
        CONNECT_BASE_ERROR  (BaseData.CONNECT_BASE_ERROR),
        CONNECT_ERROR       (BaseData.CONNECT_ERROR),
        QUERY_ERROR         (BaseData.QUERY_ERROR),
        STRUCTURE_ERROR     (BaseData.STRUCTURE_ERROR),
        UNKNOWN_ERROR       (BaseData.UNKNOWN_ERROR);
        private int codeStatus;
        Status(int codeStatus) {
            this.codeStatus = codeStatus;
        }
        public int getCodeStatus() {
            return codeStatus;
        }

        @Override
        public String toString() {
            String status = "";
            switch (codeStatus) {
                case BaseData.OK:
                    status = "OK";
                    break;
                case BaseData.UNEXPECTED_TYPE_BD:
                    status = "UNEXPECTED_TYPE_BD";
                    break;
                case BaseData.DRIVER_ERROR:
                    status = "DRIVER_ERROR";
                    break;
                case BaseData.CONNECT_PASS_ERROR:
                    status = "CONNECT_PASS_ERROR";
                    break;
                case BaseData.CONNECT_BASE_ERROR:
                    status = "CONNECT_BASE_ERROR";
                    break;
                case BaseData.QUERY_ERROR:
                    status = "QUERY_ERROR";
                    break;
                case BaseData.STRUCTURE_ERROR:
                    status = "STRUCTURE_ERROR";
                    break;
                default:
                    status = "UNKNOWN_ERROR";
            }
            return status;
        }
    }
    // -------------------------
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
    class Password {
        public static String encoding(String password) {
            return new String(java.util.Base64.getEncoder().encode(password.getBytes()));
        }
        public static String decoding(String password) throws Exception {
            try {
                return new String(Base64.getDecoder().decode(password));
            } catch (java.lang.Throwable e) {
                throw new Exception(e.getLocalizedMessage());
            }
        }
    }
    // -----------------------------------------------------------
    // создание тестового соединения
    Status createTestConnect(TypeBaseData typeBaseData, BaseData.Parameters parameters);
    // тестовое соединение проверка структуры БД
    Status checkCheckStructureBd(String base);
    // -----------------------------------------------------------
    // создание рабочего соединения
    Status createWorkConnect(TypeBaseData typeBaseData, BaseData.Parameters parameters);
    // чтение списка пользователей
    UserClass[] getListUsers(boolean actual) throws Exception;






    // список доступных БД из тестового соединения
    boolean requestListBdFromTestConnect(Consumer<String[]> list);


    // -----------------------------------------------------------
    // установка нового пароля пользователя
    boolean setUserNewPassword(UserClass user, String newPassword);




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
