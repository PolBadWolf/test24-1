package org.example.test24.loader;

import org.example.test24.bd.*;

public interface FrameCallBack {
    // ================================== работа с БД ====================================
    // чтение типа БД из конфига
    BaseData.TypeBaseData getTypeBaseDataFromConfig();
    // чтение параметров из конфига
    ParametersSql getParametersSqlFromConfig(BaseData.TypeBaseData typeBaseData);
    // создание тестого соединения
    int createTestConnectBd(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters);
    // проверка структуры БД
    int testConnectCheckStructure(String base);
    // создание рабочего соединения
    int createWorkConnect(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters);
    // загрузка пользователей
    UserClass[] getListUsers(boolean actual);
    // установка нового пароля пользователя
    boolean setUserNewPassword(UserClass user, String newPassword);
    // ==================================== работа к ком портом ====================================
    // чтение comm port из конфига
    String getCommPortNameFromConfig();
    // проверка Comm Port на валидность
    boolean checkCommPort(String portName);
    // загрузка списка ком портов в системе
    String[] getComPortNameList();
    //---------------------------
    void closeFrame();
    // ---------------
}
