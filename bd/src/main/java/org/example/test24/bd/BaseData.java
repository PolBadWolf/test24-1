package org.example.test24.bd;

import java.sql.Blob;
import java.util.Base64;
import java.util.Date;
import java.util.function.Consumer;

public interface BaseData {
    // ==================== STATUS ====================
    int STATUS_OK = 0;
    int STATUS_PARAMETERS_LOAD_ERROR = 1;
    int STATUS_PARAMETERS_SAVE_ERROR = 2;
    int STATUS_PARAMETERS_ERROR = 3;
    int STATUS_PARAMETERS_PASSWORD_ERROR = 4;
    int STATUS_BASE_TYPE_ERROR = 5;
    int STATUS_BASE_TYPE_NO_SELECT = 6;
    int STATUS_CONNECT_BASE_TYPE_ERROR = 7;
    int STATUS_CONNECT_DRIVER_ERROR = 8;
    int STATUS_CONNECT_PASS_ERROR = 9;
    int STATUS_CONNECT_BASE_ERROR = 10;
    int STATUS_CONNECT_ERROR = 11;
    int STATUS_CONNECT_NO_CONNECTION = 12;
    int STATUS_CONNECT_CLOSE = 13;
    int STATUS_SQL_TRANSACTION_ERROR = 14;
    enum Status {
        OK                          (STATUS_OK),
        PARAMETERS_LOAD_ERROR       (STATUS_PARAMETERS_LOAD_ERROR),
        PARAMETERS_SAVE_ERROR       (STATUS_PARAMETERS_SAVE_ERROR),
        PARAMETERS_ERROR            (STATUS_PARAMETERS_ERROR),
        PARAMETERS_PASSWORD_ERROR   (STATUS_PARAMETERS_PASSWORD_ERROR),
        BASE_TYPE_ERROR             (STATUS_BASE_TYPE_ERROR),
        BASE_TYPE_NO_SELECT         (STATUS_BASE_TYPE_NO_SELECT),
        CONNECT_BASE_TYPE_ERROR     (STATUS_CONNECT_BASE_TYPE_ERROR),
        CONNECT_DRIVER_ERROR        (STATUS_CONNECT_DRIVER_ERROR),
        CONNECT_PASS_ERROR          (STATUS_CONNECT_PASS_ERROR),
        CONNECT_BASE_ERROR          (STATUS_CONNECT_BASE_ERROR),
        CONNECT_ERROR               (STATUS_CONNECT_ERROR),
        CONNECT_NO_CONNECTION       (STATUS_CONNECT_NO_CONNECTION),
        CONNECT_CLOSE               (STATUS_CONNECT_CLOSE),
        SQL_TRANSACTION_ERROR       (STATUS_SQL_TRANSACTION_ERROR);

        int codeStatus;
        int getCodeStatus() {
            return codeStatus;
        }
        Status(int codeStatus) {
            this.codeStatus = codeStatus;
        }

        @Override
        public String toString() {
            String text;
            switch (codeStatus) {
                case STATUS_OK:
                    text = "ок";
                    break;
                case STATUS_PARAMETERS_LOAD_ERROR:
                    text = "ошибка загрузки";
                    break;
                case STATUS_PARAMETERS_SAVE_ERROR:
                    text = "ошибка сохранения";
                    break;
                case STATUS_PARAMETERS_ERROR:
                    text = "ошибка параметров";
                    break;
                case STATUS_PARAMETERS_PASSWORD_ERROR:
                    text = "ошибка пароля в параметрах";
                    break;
                case STATUS_BASE_TYPE_ERROR:
                    text = "ошибка типа БД";
                    break;
                case STATUS_CONNECT_DRIVER_ERROR:
                    text = "ошибка драйвера";
                    break;
                case STATUS_CONNECT_PASS_ERROR:
                    text = "ошибка пароля при соединении";
                    break;
                case STATUS_CONNECT_BASE_ERROR:
                    text = "ошибка соединения с базой БД";
                    break;
                case STATUS_CONNECT_ERROR:
                    text = "ошибка соединения с БД";
                    break;
                default:
                    text = "неизвестный код статуса";
            }
            return text;
        }
    }
    // ==================== TYPE BD ====================
    int TYPEBD_MYSQL = 0;
    int TYPEBD_MSSQL = 1;
    int TYPEBD_ERROR = -1;
    enum TypeBaseDate {
        MY_SQL      (TYPEBD_MYSQL),
        MS_SQL      (TYPEBD_MSSQL),
        ERROR       (TYPEBD_ERROR);
        int codeTypeBaseData;
        int getCodeTypeBaseData() {
            return codeTypeBaseData;
        }
        TypeBaseDate(int codeTypeBaseData) {
            this.codeTypeBaseData = codeTypeBaseData;
        }
        static void create(String typeBaseData, Consumer<TypeBaseDate> tbd) throws Exception {
            if (typeBaseData == null) {
                tbd.accept(TypeBaseDate.ERROR);
                throw new BaseDataException("ошибка типа БД (typeBaseData = null)", Status.BASE_TYPE_ERROR);
            }
            switch (typeBaseData.toUpperCase()) {
                case "MY_SQL":
                    tbd.accept(TypeBaseDate.MY_SQL);
                    break;
                case "MS_SQL":
                    tbd.accept(TypeBaseDate.MS_SQL);
                    break;
                default:
                    tbd.accept(TypeBaseDate.ERROR);
                    throw new BaseDataException("ошибка типа БД (typeBaseData = " + typeBaseData + ")", Status.BASE_TYPE_ERROR);
            }
        }

        public String codeToString() throws BaseDataException {
            String text;
            switch (codeTypeBaseData) {
                case TYPEBD_MYSQL:
                    text = "MY_SQL";
                    break;
                case TYPEBD_MSSQL:
                    text = "MS_SQL";
                    break;
                default:
                    throw new BaseDataException("не известный код типа БД", Status.BASE_TYPE_NO_SELECT);
            }
            return text;
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
        static BaseData.Parameters create(BaseData.TypeBaseDate typeBaseDate) throws BaseDataException {
            return new ParametersSql(typeBaseDate);
        }
        BaseData.Status load() throws BaseDataException;
        BaseData.Status save();
        void setDefault();
    }
    // ==================== CONFIG ====================
    interface Config {
        static Config create() { return new ParametersConfig(); }
        Status load1() throws Exception;
        Status save() throws BaseDataException;
        void setDefault();
        String getPortName();
        BaseData.TypeBaseDate getTypeBaseData();
        void setPortName(String portName);
        void setTypeBaseData(BaseData.TypeBaseDate typeBaseData);
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
    static BaseData create(Parameters parameters) throws BaseDataException { return BaseDataParent.create(parameters); }
    // ===================================================
    // открытие соединение с БД
    void openConnect(Parameters parameters) throws BaseDataException;
    // чтение списка БД
    String[] getListBase() throws BaseDataException;
    // чтение списка пользователей
    User[] getListUsers(boolean actual) throws BaseDataException;
    // проверка структуры БД
    boolean checkStructureBd(String base) throws BaseDataException;
    // установка нового пароля пользователю
    void setNewUserPassword(User user, String newPassword) throws BaseDataException;
    // чтение списка толкателей
    Pusher[] getListPushers(boolean actual) throws Exception;
    // запись нового пользователя
    void writeNewUser(long id_loggerUserEdit, String sunName, String password, int rang) throws BaseDataException;
    // деактивация пользователя
    void deativateUser(long id_loggerUserEdit, User user) throws BaseDataException;
    // обновление данных о пользователе
    void updateDataUser(long id_loggerUserEdit, User editUser, String surName, String password, int rang) throws BaseDataException;
    // запись замера
    void writeDataDist(Date date, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws BaseDataException;
    // запись нового типа толкателя
    void writeNewTypePusher(PusherType pusherType) throws BaseDataException;
    // обновление типа толкателя
    void updateTypePusher(PusherType pusherType) throws BaseDataException;
    // деактивация типа толкателя
    void deativateTypePusher(long id_loggerUser, PusherType pusherType) throws BaseDataException;
}
