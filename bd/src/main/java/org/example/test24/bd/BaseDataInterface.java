package org.example.test24.bd;

interface BaseDataInterface {
    // тестовое соединение
    int testConnectInit(BaseData.Parameters parameters);
    // тестовое соединение список доступных баз
    String[] testConnectListBd() throws Exception;
    // тестовое соединение проверка структуры БД
    int testConnectCheckStructure(String base);
}
