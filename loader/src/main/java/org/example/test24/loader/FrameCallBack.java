package org.example.test24.loader;

import org.example.test24.bd.*;

import java.util.function.Consumer;

public interface FrameCallBack {
    // =================================
    // чтение параметров из конфига
    ParametersConfig getParametersConfig() throws Exception;
    // ================================== работа с БД ====================================
    // чтение типа БД из конфига
    BaseData.TypeBaseData getTypeBaseDataFromConfig();
    // чтение параметров
    ParametersSql getParametersSql(BaseData.TypeBaseData typeBaseData);
    // создание тестого соединения
    int createTestConnectBd(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters);
    // список доступных БД из тестового соединения
    boolean requestListBdFromTestConnect(Consumer<String[]> list);
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
    boolean requestCommPortNameFromConfig(Consumer<String> portName);
    // проверка Comm Port на валидность
    boolean checkCommPort(String portName);
    // загрузка списка ком портов в системе
    String[] getComPortNameList();
    //---------------------------
    void closeFrame();
    // ---------------
}
