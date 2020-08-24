package org.example.test24.loader;

import org.example.test24.allinterface.bd.UserClass;

public interface MainClassCallBackStartFrame {
    // проверка Comm Port
    boolean checkCommPort();
    // подключение к БД и структуры БД (параметры из файла конфигурации)
    boolean checkSqlFile();
    void closeFrame();
    // ---------------
    TuningFrame getTuningFrame();
    String[] getParameters();
    String[] getFilesNameSql();
    String getFileNameSql(String typeBd) throws Exception;
}
