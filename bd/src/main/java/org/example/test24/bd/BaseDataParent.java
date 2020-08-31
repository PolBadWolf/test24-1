package org.example.test24.bd;

import java.sql.Connection;
import static org.example.test24.bd.BaseData.*;

class BaseDataParent implements BaseDataInterface {
    //private BaseData.Parameters parameters;
    protected BaseData.Parameters parametersTest;
    protected Connection testConnection;

    public BaseDataParent() {
    }
    // тестовое соединение
    @Override
    public int testConnectInit(BaseData.Parameters parameters) {
        return UNKNOWN_ERROR;
    }
    // тестовое соединение список доступных баз
    @Override
    public String[] testConnectListBd() throws Exception {
        return new String[0];
    }
    // тестовое соединение проверка структуры БД
    @Override
    public int testConnectCheckStructure(String base) {
        return UNKNOWN_ERROR;
    }
    // инициализация рабочего соединения
    @Override
    public int workConnectInit(Parameters parameters) {
        return UNKNOWN_ERROR;
    }
}
