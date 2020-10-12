package org.example.test24.loader;

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
    // =============== недоступные переменные ==============
    // имя файла конфигурации
    final private String fileNameConfig = "config.txt";
    // параметры конфигурации
    private volatile ParametersConfig parametersConfig = null;
    // =============== запросы ==================
    // загрузка начальной конфигурации
    /*protected ParametersConfig getParametersConfig() {
        if (parametersConfig != null) return parametersConfig;
        parametersConfig = new ParametersConfig();
        if (parametersConfig.load1() != ParametersConfig.Diagnostic.OK) {
                parametersConfig.setDefault();
        }
        return parametersConfig;
    }
    */
    // ************************************************
}
