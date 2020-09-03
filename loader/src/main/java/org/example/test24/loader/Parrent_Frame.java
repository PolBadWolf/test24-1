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
    // начальная инициация соединения c БД и получение списка пользователей
    protected boolean beginInitConnectBdGetListUsers() {
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
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
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
    }

}
