package org.example.test24.loader;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.ParametersSql;
import org.example.test24.bd.UserClass;

class Parrent_Frame {
    // ================================================
    // интерфейс обратного вызова
    protected FrameCallBack callBack;
    // тип БД
    protected BaseData.TypeBaseData typeBaseData;
    // парамеры подключения к БД
    protected ParametersSql parametersSql;
    // флаг структурной целостности БД
    protected boolean flCheckSql = false;
    // имя ком порта
    protected String commPortName;
    // флаг доступности ком портов
    protected boolean flCheckCommPort = false;


    protected UserClass[] listUsers = null;
    protected UserClass user = null;
    // ================================================
    // начальная загрузка параметров соединения с БД
    protected boolean beginInitParametersSql() {
        // тип БД
        typeBaseData = callBack.getTypeBaseDataFromConfig();
        if (typeBaseData == BaseData.TypeBaseData.ERROR) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        int result;
        // чтение параметров из конфига
        parametersSql = callBack.getParametersSqlFromConfig(typeBaseData);
        if (parametersSql.getStat() != ParametersSql.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        return true;
    }
    // инициация тестового соединения c БД
    protected int initTestConnectBd() {
        // установка тестового соединения
        return callBack.createTestConnectBd(typeBaseData,
                new BaseData.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        /*
        // проверка структуры БД
        result = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // установка рабочего соединения
        result = callBack.createWorkConnect(typeBaseData,
                new BaseData.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        flCheckSql = true;
        // загрузка списка пользователей
        listUsers = callBack.getListUsers(true);
        if (listUsers.length == 0) {
            return false;
        }
        return true;
        */
    }
    // инициация тестового соединения c БД список баз
    protected String[] initTestConnectBdGetListBse() {
        int result;
        // установка тестового соединения
        result = callBack.createTestConnectBd(typeBaseData,
                new BaseData.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData.OK) {
            return new String[0];
        }

        /*
        // проверка структуры БД
        result = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // установка рабочего соединения
        result = callBack.createWorkConnect(typeBaseData,
                new BaseData.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        flCheckSql = true;
        // загрузка списка пользователей
        listUsers = callBack.getListUsers(true);
        if (listUsers.length == 0) {
            return false;
        }
        return true;
        */
        return new String[0];
    }
    protected boolean testBaseAndInitWorkConnectBd() {
        int result;
        // проверка структуры БД
        result = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // установка рабочего соединения
        result = callBack.createWorkConnect(typeBaseData,
                new BaseData.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        flCheckSql = true;
        return true;
    }
    // получение списка пользователей
    protected boolean getListUsersFromBD() {
        listUsers = callBack.getListUsers(true);
        if (listUsers.length == 0) {
            return false;
        }
        return true;
    }
    //
    protected boolean initConnectGetListUsers() {
        // установка тестового соединения
        if (
            initTestConnectBd()
             != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // проверка структуры БД
        testBaseAndInitWorkConnectBd();
        return false;
    }
}
