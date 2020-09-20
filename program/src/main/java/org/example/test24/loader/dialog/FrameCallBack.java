package org.example.test24.loader.dialog;

import org.example.test24.bd.*;
import org.example.test24.bd.ParametersConfig;

public interface FrameCallBack {
    // =================================
    // чтение параметров из конфига
    ParametersConfig getParametersConfig();
    // создание объекта параметров соединения с БД
    ParametersSql2 createParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception;
    // запрос параметров соединения с БД
    ParametersSql2 requestParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception;
    // -----------------------------------------------------------
    // создание тестого соединения
    BaseData2.Status createTestConnectBd(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters);
    // тестовое соединение проверка структуры БД
    BaseData2.Status checkCheckStructureBd(String base);
    // -----------------------------------------------------------
    // создание рабочего соединения
    BaseData2.Status createWorkConnect(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters);
    // чтение списка пользователей
    User[] getListUsers(boolean actual) throws Exception;

    // -----------------------------------------------------------
    // проверка ком порта
    boolean isCheckCommPort(boolean statMainWork, String portName) throws Exception;



    String[] getListBd() throws Exception;


/*
    // ================================== работа с БД ====================================
    // список доступных БД из тестового соединения
    boolean requestListBdFromTestConnect(Consumer<String[]> list);
    // загрузка пользователей
    User[] getListUsers(boolean actual);
    // установка нового пароля пользователя
    boolean setUserNewPassword(User user, String newPassword);
    // ==================================== работа к ком портом ====================================
    // чтение comm port из конфига
    boolean requestCommPortNameFromConfig(Consumer<String> portName);
    // проверка Comm Port на валидность
    boolean checkCommPort(String portName);
    // загрузка списка ком портов в системе
    String[] getComPortNameList();
    //---------------------------
    void closeFrame();
    // ---------------
*/
}
