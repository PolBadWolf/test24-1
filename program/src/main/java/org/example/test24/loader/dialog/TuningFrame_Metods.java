package org.example.test24.loader.dialog;

import org.example.test24.RS232.CommPort;
import org.example.test24.bd.BaseData;
import org.example.test24.bd.ParametersSql;
import org.example.test24.bd.UserClass;
import org.example.test24.loader.ParametersConfig;

class TuningFrame_Metods extends TuningFrame_Vars {
    // загрузка начальных параметров
    protected void loadBeginerParameters() {
        ParametersConfig config;
        ParametersSql parametersSql = null;
        int parametersSqlError;
        //
        // запрос конфигурации
        config = callBack.getParametersConfig();
        // тип БД
        if (config.getTypeBaseData() == BaseData.TypeBaseData.ERROR) {
            System.out.println("ошибка типа базы данных: " + config.getTypeBaseData().toString());
            config.setTypeBaseData(BaseData.TypeBaseData.MY_SQL);
        }
        // загрузка параметров соединения с БД
        try {
            parametersSql = callBack.requestParametersSql(config.getTypeBaseData());
            parametersSqlError = 0;
        } catch (Exception e) {
            System.out.println("Ошибка загрузки параметров соединения с БД" + e.getMessage());
            parametersSqlError = 1;
        }
        // чтение списка пользователей из нового соединения
        if (parametersSqlError == 0) {
            listUsers = getListUsersFromNewConnect(parametersSql, i -> {
                switch (i) {
                    case BaseData.CONNECT_ERROR:
                    case BaseData.STRUCTURE_ERROR:
                    case BaseData.QUERY_ERROR:
                        flCheckSql = false;
                        break;
                    case BaseData.OK:
                        flCheckSql = true;
                        break;
                }
            });
        } else {
            // нет соединения с БД - создать пустой список пользователей
            listUsers = new UserClass[0];
            this.flCheckSql = false;
        }
        this.parametersSql = parametersSql;
        // проверка ком порта
        try {
            flCheckCommPort = callBack.isCheckCommPort(statMainWork, config.getPortName());
            commPortName = config.getPortName();
            commPortNameList = CommPort.getListPortsName();
        } catch (Exception e) {
            System.out.println("Ошибка поверки ком порта: " + e.getMessage());
            flCheckCommPort = false;
            commPortName = "";
            commPortNameList = new String[0];
        }
        // ---
        try {
            listBaseData = callBack.getListBd();
        } catch (Exception exception) {
            exception.printStackTrace();
            listBaseData = new String[0];
        }
    }
}
