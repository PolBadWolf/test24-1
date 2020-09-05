package org.example.test24.loader.dialog;

import org.example.test24.bd.*;
import org.example.test24.loader.ParametersConfig;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface FrameCallBack {
    // =================================
    // чтение параметров из конфига
    ParametersConfig getParametersConfig();
    // запрос параметров соединения с БД
    ParametersSql requestParametersSql(BaseData.TypeBaseData typeBaseData, BiConsumer<ParametersSql, ParametersSql.Status> exception) throws Exception;
    // создание тестого соединения
    BaseData.Status createTestConnectBd(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters);
    // тестовое соединение проверка структуры БД
    BaseData.Status checkCheckStructureBd(String base);
    // чтение списка пользователей
    UserClass[] getListUsers(boolean actual, BiConsumer<UserClass[], BaseData.Status> exception);





/*
    // ================================== работа с БД ====================================
    // список доступных БД из тестового соединения
    boolean requestListBdFromTestConnect(Consumer<String[]> list);
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
*/
}
