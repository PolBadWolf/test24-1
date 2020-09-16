package org.example.test24.loader;

import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;
import org.example.test24.bd.*;
import org.example.test24.loader.dialog.StartFrame;
import org.example.test24.runner.Runner;
import org.example.test24.screen.ScreenFx;

class MainClassRequest {
    // глобальные переменные и объекты
    final public String fileNameMsSql = "ms_sql.txt";
    final public String fileNameMySql = "my_sql.txt";
    final public String[] fileNameSql = {fileNameMsSql, fileNameMySql};
    // ===============================================
    // модули
    protected ScreenFx screenFx;
    protected Runner runner;
    protected CommPort commPort;
    protected StartFrame startFrame;
    protected BaseData1 bdSql;
    protected BaseData2 connBd;
    // =============== недоступные переменные ==============
    // имя файла конфигурации
    final private String fileNameConfig = "config.txt";
    // параметры конфигурации
    private volatile ParametersConfig parametersConfig = null;
    // =============== запросы ==================
    // загрузка начальной конфигурации
    protected ParametersConfig getParametersConfig() {
        if (parametersConfig != null) return parametersConfig;
        parametersConfig = new ParametersConfig(fileNameConfig);
        if (parametersConfig.load() != ParametersConfig.Diagnostic.OK) {
                parametersConfig.setDefault();
        }
        return parametersConfig;
    }
    // создание объекта параметров соединения с БД
    protected ParametersSql2 createParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception {
        String fileNameParameters;
        switch (typeBaseData) {
            case MY_SQL:
                fileNameParameters = fileNameMySql;
                break;
            case MS_SQL:
                fileNameParameters = fileNameMsSql;
                break;
            default:
                throw new Exception("Неизвестный тип БД: " + typeBaseData.toString());
        }
        return new ParametersSql2(fileNameParameters, typeBaseData);
    }
    // запрос параметров соединения с БД
    protected ParametersSql2 requestParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception {
        String fileNameParameters;
        switch (typeBaseData) {
            case MY_SQL:
                fileNameParameters = fileNameMySql;
                break;
            case MS_SQL:
                fileNameParameters = fileNameMsSql;
                break;
            default:
                throw new Exception("Неизвестный тип БД: " + typeBaseData.toString());
        }
        ParametersSql2 parametersSql = new ParametersSql2(fileNameParameters, typeBaseData);
        ParametersSql2.Status status = parametersSql.load();
        if (status != ParametersSql2.Status.OK ) {
            throw new Exception("ошибка приема параметов соединения с БД: " + status.toString());
        }
        return parametersSql;
    }
    // -----------------------------------------------------------
    // создание тестого соединения
    protected BaseData2.Status createTestConnectBd(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
        return connBd.createTestConnect(typeBaseData, parameters);
    }
    // тестовое соединение проверка структуры БД
    protected BaseData2.Status checkCheckStructureBd(String base) {
        return connBd.checkCheckStructureBd(base);
    }
    // -----------------------------------------------------------
    // создание рабочего соединения
    protected BaseData2.Status createWorkConnect(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
        return connBd.createWorkConnect(typeBaseData, parameters);
    }
    // чтение списка пользователей
    protected UserClass[] getListUsers(boolean actual) throws Exception {
        return connBd.getListUsers(actual);
    }
    // ************************************************
    // проверка ком порта
    protected boolean isCheckCommPort(boolean statMainWork, String portName) throws Exception {
        String portNameConfig;
        CommPort port;
        if (statMainWork) {
            portNameConfig = parametersConfig.getPortName();
            if (portName == portNameConfig) return true;
        }
        port = CommPort.main();
        CommPort.PortStat portStat = port.open(null, portName, BAUD.baud57600);
        port.close();
        if (portStat == CommPort.PortStat.INITCODE_OK)  return true;
        return false;
    }
}
