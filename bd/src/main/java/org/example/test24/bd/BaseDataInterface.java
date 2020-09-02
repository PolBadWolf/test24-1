package org.example.test24.bd;

interface BaseDataInterface {
    // тестовое соединение
    int testConnectInit(BaseData.Parameters parameters);
    // тестовое соединение список доступных баз
    String[] testConnectListBd();
    // тестовое соединение проверка структуры БД
    int testConnectCheckStructure(String base);
    // инициализация рабочего соединения
    int workConnectInit(BaseData.Parameters parameters);
    // чтение списка пользователей
    UserClass[] getListUsers(boolean actual) throws Exception;
    // установка нового пароля пользователя
    boolean setUserNewPassword(UserClass user, String newPassword);
}
