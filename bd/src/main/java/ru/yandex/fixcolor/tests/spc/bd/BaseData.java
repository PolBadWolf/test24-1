package ru.yandex.fixcolor.tests.spc.bd;

import ru.yandex.fixcolor.tests.spc.bd.usertypes.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;

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
        Status load() throws BaseDataException;
        Status save() throws BaseDataException;
        void setDefault();
        String getPortName();
        TypeBaseDate getTypeBaseData();
        void setPortName(String portName);
        void setTypeBaseData(TypeBaseDate typeBaseData);
        // **** distance ****
        // set calib distance
        void setDistanceCalib(Point point1, Point point2);
        // gets
        double getDistance_k();
        double getDistance_offset();
        // distance point 1
        double getDistancePoint1_vol();
        int getDistancePoint1_adc();
        // distance point 2
        double getDistancePoint2_vol();
        int getDistancePoint2_adc();
        // **** force ****
        // set calib force
        void setForceCalib(Point point1, Point point2);
        // gets
        double getForce_k();
        double getForce_offset();
        // force point 1
        Point getForcePoint1();
        // force point 2
        Point getForcePoint2();
        // ******************************
        // *** n cycle max ***
        void setMaxNcycle(int maxNcycle);
        int getMaxNcycle();
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
    long writeNewTypePusher(long id_loggerUser, String nameType, int forceNominal, int moveNominal, float unclenchingTime, float clenchingTime, int weightNominal) throws BaseDataException;
    // чтение списока типов толкателей
    TypePusher[] getListTypePushers(boolean actual) throws BaseDataException;
    // обновление типа толкателя
    void updateTypePusher(TypePusher typePusher, long id_loggerUser, String nameType, int forceNominal, int moveNominal, float unclenchingTime, float clenchingTime, int weightNominal) throws BaseDataException;
    // деактивация типа толкателя
    void deleteTypePusher(long id_loggerUser, TypePusher typePusher) throws BaseDataException;
    // чтение списка толкателей
    Pusher[] getListPushers(boolean actual) throws Exception;
    // запись нового толкателя
    long writeNewPusher(long id_loggerUser, String regNumber, long id_typePusher) throws BaseDataException;
    // обновление толкателя
    void updatePusher(Pusher pusher, long id_loggerUser, String regNumber, long id_typePusher) throws BaseDataException;
    // удаление толкателя
    void deletePusher(long id_loggerUser, Pusher pusher) throws BaseDataException;
    // чтение данных о толкателе
    Pusher getPusher(long id_pusher) throws BaseDataException;
    long getIdTypePusherFromIdPusher(long id_pusher) throws BaseDataException;
    // количество толкателей заданого типа
    int getCountPushersFromType(long id_typePusher, String[] namePusher) throws BaseDataException;
    // тип толкателя
    TypePusher getTypePusher(long id_typePusher) throws BaseDataException;
    // чтение данных о пользователе
    User getUser(long id_user) throws BaseDataException;
    // новые параметры spec
    void writeDataSpec(long id_user, long id_pusher) throws BaseDataException;
    // последний spec
    long getIdLastDataSpec() throws BaseDataException;
    DataSpec getLastDataSpec() throws BaseDataException;

    // запись замера
    void writeDataDist(int n_cicle, int ves, int tik_shelf, int tik_back, int tik_stop,
                       int forceNominal, int moveNominal, float unclenchingTime, float clenchingTime, Blob dataMeasured) throws BaseDataException;
    //
    BaseData cloneNewBase(String base);
    // чтение замера
    DataUnitMeasured getDataMeasured(long id_data) throws BaseDataException;
    // =========================
    ArrayList<String> getListFromYear() throws BaseDataException;
    ArrayList<String> getListFromMounth(String year) throws BaseDataException;
    ArrayList<String> getListFromDates(String mounth) throws BaseDataException;
    ArrayList<DataUnit> getListFromPusherChecks(String date) throws BaseDataException;
    // =========================
}
