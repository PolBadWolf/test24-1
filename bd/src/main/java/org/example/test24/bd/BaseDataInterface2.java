package org.example.test24.bd;

import java.util.function.Consumer;

interface BaseDataInterface2 {
    // создание тестового соединения
    BaseData2.Status createTestConnect(BaseData2.Parameters parameters);
    // тестовое соединение проверка структуры БД
    BaseData2.Status checkCheckStructureBd(String base);
    // -----------------------------------------------------------
    // инициализация рабочего соединения
    BaseData2.Status createWorkConnect(BaseData2.Parameters parameters);
    // чтение списка пользователей
    User[] getListUsers(boolean actual) throws Exception;

    String[] getListBd() throws Exception;





    // тестовое соединение список доступных баз
    boolean requestListBdFrom(Consumer<String[]> list);
    // установка нового пароля пользователя
    boolean setUserNewPassword(User user, String newPassword);
}
