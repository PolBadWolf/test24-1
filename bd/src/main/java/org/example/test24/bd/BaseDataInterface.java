package org.example.test24.bd;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

interface BaseDataInterface {
    // создание тестового соединения
    BaseData.Status createTestConnect(BaseData.Parameters parameters);
    // тестовое соединение проверка структуры БД
    BaseData.Status checkCheckStructureBd(String base);
    // -----------------------------------------------------------
    // инициализация рабочего соединения
    BaseData.Status workConnectInit(BaseData.Parameters parameters);
    // чтение списка пользователей
    UserClass[] getListUsers(boolean actual, BiConsumer<UserClass[], BaseData.Status> exception);







    // тестовое соединение список доступных баз
    boolean requestListBdFrom(Consumer<String[]> list);
    // установка нового пароля пользователя
    boolean setUserNewPassword(UserClass user, String newPassword);
}
