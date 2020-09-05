package org.example.test24.loader;

import org.example.test24.RS232.CommPort;
import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseData1;
import org.example.test24.bd.ParametersSql;
import org.example.test24.bd.UserClass;
import org.example.test24.loader.dialog.StartFrame;
import org.example.test24.runner.Runner;
import org.example.test24.screen.ScreenFx;

import java.util.function.BiConsumer;

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
    protected BaseData connBd;
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
    // запрос параметров соединения с БД
    public ParametersSql requestParametersSql(BaseData.TypeBaseData typeBaseData, BiConsumer<ParametersSql, ParametersSql.Status> exception) throws Exception {
        String fileNameParameters;
        switch (typeBaseData) {
            case MY_SQL:
                fileNameParameters = fileNameMySql;
                break;
            case MS_SQL:
                fileNameParameters = fileNameMsSql;
                break;
            default:
                // неизвестный тип БД
                int a = 1 / 0;
                fileNameParameters = "";
        }
        ParametersSql parametersSql = new ParametersSql(fileNameParameters, typeBaseData);
        ParametersSql.Status status = parametersSql.load();
        if (status != ParametersSql.Status.OK ) {
            if (exception != null) exception.accept(parametersSql, status);
            else throw new Exception("ошибка приема параметов соединения с БД: " + status.toString());
        }
        return parametersSql;
    }
    // создание тестого соединения
    public BaseData.Status createTestConnectBd(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters) {
        return connBd.createTestConnect(typeBaseData, parameters);
    }
    // тестовое соединение проверка структуры БД
    public BaseData.Status checkCheckStructureBd(String base) {
        return connBd.checkCheckStructureBd(base);
    }
    // чтение списка пользователей
    public UserClass[] getListUsers(boolean actual, BiConsumer<UserClass[], BaseData.Status> exception) {
        return connBd.getListUsers(actual, exception);
    }
}
