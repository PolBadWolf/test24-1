package org.example.test24.loader;

import org.example.test24.bd.*;

public interface FrameCallBack {
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
    // прочитать список пользователей
    UserClass[] getListUsers(boolean actual);
    // чтение comm port из конфига
    String getCommPortNameFromConfig();
    // проверка Comm Port на валидность
    boolean checkCommPort(String portName);
    // загрузка списка ком портов в системе
    String[] getComPortNameList();

    // ----------------
    // установка нового пароля пользователя
    boolean setUserNewPassword(UserClass user, String newPassword);



    //---------------------------
    // подключение к БД и структуры БД (параметры из файла конфигурации)
    boolean checkSqlFile();
    void closeFrame();
    // ---------------
    TuningFrame getTuningFrame();
    //String[] getParameters();
    String[] getFilesNameSql();
    String getFileNameSql(String typeBd) throws Exception;
    String loadConfigCommPort();
    BaseData.TypeBaseData loadConfigTypeBaseData();
}
