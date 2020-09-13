package org.example.test24.bd;

import java.util.Base64;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public interface BaseData {
    // ==================== STATUS ====================
    int STATUS_OK = 0;
    int STATUS_PARAMETERS_LOAD_ERROR = 1;
    int STATUS_PARAMETERS_SAVE_ERROR = 2;
    int STATUS_PARAMETERS_ERROR = 3;
    int STATUS_PARAMETERS_PASSWORD_ERROR = 4;
    int STATUS_BASE_TYPE_ERROR = 5;
    int STATUS_CONNECT_DRIVER_ERROR = 6;
    int STATUS_CONNECT_PASS_ERROR = 7;
    int STATUS_CONNECT_BASE_ERROR = 8;
    int STATUS_CONNECT_ERROR = 9;
    enum Status {
        OK                          (STATUS_OK),
        PARAMETERS_LOAD_ERROR       (STATUS_PARAMETERS_LOAD_ERROR),
        PARAMETERS_SAVE_ERROR       (STATUS_PARAMETERS_SAVE_ERROR),
        PARAMETERS_ERROR            (STATUS_PARAMETERS_ERROR),
        PARAMETERS_PASSWORD_ERROR   (STATUS_PARAMETERS_PASSWORD_ERROR),
        BASE_TYPE_ERROR             (STATUS_BASE_TYPE_ERROR),
        CONNECT_DRIVER_ERROR        (STATUS_CONNECT_DRIVER_ERROR),
        CONNECT_PASS_ERROR          (STATUS_CONNECT_PASS_ERROR),
        CONNECT_BASE_ERROR          (STATUS_CONNECT_BASE_ERROR),
        CONNECT_ERROR               (STATUS_CONNECT_ERROR);

        int codeStatus;
        int getCodeStatus() {
            return codeStatus;
        }
        Status(int codeStatus) {
            this.codeStatus = codeStatus;
        }
    }
    // ==================== TYPE BD ====================
    int TYPEBD_MYSQL = 0;
    int TYPEBD_MSSQL = 1;
    int TYPEBD_ERROR = -1;
    enum TypeBaseDate {
        MYSQL       (TYPEBD_MYSQL),
        MSSQL       (TYPEBD_MSSQL),
        ERROR       (TYPEBD_ERROR);
        int codeTypeBaseData;
        int getCodeTypeBaseData() {
            return codeTypeBaseData;
        }
        TypeBaseDate(int codeTypeBaseData) {
            this.codeTypeBaseData = codeTypeBaseData;
        }
    }
    // ==================== PARAMETERS ====================
    interface Parameters {
        BaseData.Status getStat();
        BaseData.TypeBaseDate getTypeBaseDate();
        String getIpServer();
        String getPortServer();
        String getDataBase();
        String getUser();
        String getPassword();
        void setPortServer(String portServer);
        void setIpServer(String ipServer);
        void setDataBase(String dataBase);
        void setUser(String user);
        void setPassword(String password);
        static BaseData.Parameters create(BaseData.TypeBaseDate typeBaseDate) {
            return ParametersSql.create(typeBaseDate);
        }
        BaseData.Status load();
        BaseData.Status save();
        void setDefault();
    }
    // ==================== PASSWORD ====================
    class Password {
        public static String encoding(String password) {
            return new String(java.util.Base64.getEncoder().encode(password.getBytes()));
        }
        public static String decoding(String password) throws IllegalArgumentException {
            return new String(Base64.getDecoder().decode(password));
        }
    }
    // ==================== SQL ====================
    static BaseData create(Parameters parameters) throws Exception {
        BaseData baseData;
        switch (parameters.getTypeBaseDate().getCodeTypeBaseData()) {
            case BaseData.TYPEBD_MYSQL:
                baseData = new BaseDataMySql();
                break;
            case BaseData.TYPEBD_MSSQL:
                baseData = new BaseDataMsSql();
                break;
            default:
                myLog.log(Level.SEVERE, "ошибка открытия БД - не верный тип БД");
                throw new Exception("ошибка открытия БД - не верный тип БД");
        }
        return baseData;
    }
    // ===================================================
    // открытие соединение с БД
    BaseData.Status createConnect(Parameters parameters);
    // чтение списка БД
    String[] getListBase() throws Exception;
    // чтение списка пользователей
    UserClass[] getListUsers(boolean actual) throws Exception;
}
