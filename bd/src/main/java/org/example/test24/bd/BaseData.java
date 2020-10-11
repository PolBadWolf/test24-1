package org.example.test24.bd;

import org.example.test24.bd.usertypes.Pusher;
import org.example.test24.bd.usertypes.TypePusher;
import org.example.test24.bd.usertypes.User;

import java.sql.Blob;
import java.util.Base64;
import java.util.Date;

public interface BaseData {
    // ==================== PARAMETERS ====================
    interface Parameters {
        Status getStat();
        TypeBaseDate getTypeBaseDate();
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
        static BaseData.Parameters create(TypeBaseDate typeBaseDate) throws BaseDataException {
            return new ParametersSql(typeBaseDate);
        }
        Status load() throws BaseDataException;
        Status save();
        void setDefault();
    }
    // ==================== CONFIG ====================
    interface Config {
        static Config create() { return new ParametersConfig(); }
        Status load1() throws Exception;
        Status save() throws BaseDataException;
        void setDefault();
        String getPortName();
        TypeBaseDate getTypeBaseData();
        void setPortName(String portName);
        void setTypeBaseData(TypeBaseDate typeBaseData);
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
    String getCurrentBase();
    // чтение списка БД
    String[] getListBase() throws BaseDataException;
    // проверка структуры БД
    boolean checkStructureBd(String base) throws BaseDataException;
    // чтение списка пользователей
    User[] getListUsers(boolean actual) throws BaseDataException;
    // запись нового пользователя
    void writeNewUser(long id_loggerUserEdit, String sunName, String password, int rang) throws BaseDataException;
    // установка нового пароля пользователю
    void setNewUserPassword(long id_loggerUserEdit, User user, String newPassword) throws BaseDataException;
    // обновление данных о пользователе
    void updateDataUser(User editUser, long id_loggerUserEdit, String surName, String password, int rang) throws BaseDataException;
    // деактивация пользователя
    void deleteUser(long id_loggerUserEdit, User user) throws BaseDataException;
    // запись нового типа толкателя
    void writeNewTypePusher(long id_loggerUser, String nameType, int forceNominal, int moveNominal, int unclenchingTime) throws BaseDataException;
    // чтение списока типов толкателей
    TypePusher[] getListTypePushers(boolean actual) throws BaseDataException;
    // обновление типа толкателя
    void updateTypePusher(TypePusher typePusher, long id_loggerUser, String nameType, int forceNominal, int moveNominal, int unclenchingTime) throws BaseDataException;
    // деактивация типа толкателя
    void deleteTypePusher(long id_loggerUser, TypePusher typePusher) throws BaseDataException;
    // чтение списка толкателей
    Pusher[] getListPushers(boolean actual) throws Exception;

    // запись замера
    void writeDataDist(Date date, int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop, Blob distance) throws BaseDataException;
    //
    BaseData cloneNewBase(String base);
    // запись нового толкателя
    void writeNewPusher(long id_loggerUser, String regNumber, long id_typePusher) throws BaseDataException;
    // удаление толкателя
    void deactivatePusher(long id_loggerUser, long id_pusher) throws BaseDataException;
}
