package org.example.test24.loader.dialog;

class FrameParrent_Metods extends FrameParrent_Vars {
    // ================================================
    // ================================================
    // ================================================






    // начальная загрузка параметров соединения с БД
    protected boolean beginInitParametersSql() {
        /*// тип БД
        typeBaseData = callBack.getTypeBaseDataFromConfig(null);
        if (typeBaseData == BaseData2.TypeBaseData.ERROR) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        int result;
        // чтение параметров из конфига
        parametersSql = callBack.getParametersSql(typeBaseData);
        if (parametersSql.getStat() != ParametersSql2.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }*/
        return true;
    }
    // инициация тестового соединения c БД
    /*
    protected int initTestConnectBd(ParametersSql2 parametersSql) {
        // проверка паметров на валидность
        if (parametersSql.getStat() != ParametersSql2.Status.OK) {
            return BaseData2.ERROR;
        }
        // установка тестового соединения
        return callBack.createTestConnectBd(
                parametersSql.typeBaseData, new BaseData2.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );



         // установка тестового соединения
        return callBack.createTestConnectBd(typeBaseData,
                new BaseData2.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );



        // проверка структуры БД
        result = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (result != BaseData2.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // установка рабочего соединения
        result = callBack.createWorkConnect(typeBaseData,
                new BaseData2.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData2.OK) {
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
    */
    // инициация тестового соединения c БД список баз
    /*
    protected String[] initTestConnectBdGetListBse() {
        int result;
        // установка тестового соединения
        result = callBack.createTestConnectBd(typeBaseData,
                new BaseData2.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData2.OK) {
            return new String[0];
        }






        // проверка структуры БД
        result = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (result != BaseData2.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // установка рабочего соединения
        result = callBack.createWorkConnect(typeBaseData,
                new BaseData2.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData2.OK) {
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

        return new String[0];
    }
    */
    /*
    protected boolean testBaseAndInitWorkConnectBd() {
        int result;
        // проверка структуры БД
        result = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (result != BaseData2.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // установка рабочего соединения
        result = callBack.createWorkConnect(typeBaseData,
                new BaseData2.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData2.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        flCheckSql = true;
        return true;
    }
     */
    // получение списка пользователей
    /*
    protected boolean getListUsersFromBD() {
        listUsers = callBack.getListUsers(true);
        if (listUsers.length == 0) {
            return false;
        }
        return true;
    }
     */
    // получения списка пользователей параметры из конфига
    /*protected boolean getListUserFromConfig(Consumer<ParametersSql2> setParameters) {
        BaseData2.TypeBaseData typeBaseData;
        ParametersSql2 parametersSql;
        int resultInt;
        // чтение из конфига тип БД
        typeBaseData = callBack.getTypeBaseDataFromConfig();
        if (typeBaseData == BaseData2.TypeBaseData.ERROR) {
            listUsers = new UserClass[0];
            MySwingUtil.showMessage(frame, "чтение из конфига тип БД", "ошибка чтения типа БД из конфига", 5_000);
            return false;
        }
        // загрузка параметров соединения
        parametersSql = callBack.getParametersSql(typeBaseData);
        // проверка параметров соединения
        if (parametersSql.getStat() != ParametersSql2.OK) {
            listUsers = new UserClass[0];
            MySwingUtil.showMessage(frame, "загрузка параметров соединения", "ошибка получения параметров соединения с БД", 5_000);
            return false;
        }
        // попытка тестового соединения
        resultInt = initTestConnectBd(parametersSql);
        if (resultInt != BaseData2.OK) {
            listUsers = new UserClass[0];
            MySwingUtil.showMessage(frame, "попытка тестового соединения", "ошибка соединения с БД", 5_000);
            return false;
        }
        // проверка структуры БД
        resultInt = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (resultInt != BaseData2.OK) {
            listUsers = new UserClass[0];
            MySwingUtil.showMessage(frame, "проверка структуры БД", "структура БД нарушена", 5_000);
            return false;
        }
        // попытка рабочего соединения
        resultInt = callBack.createWorkConnect(parametersSql.typeBaseData,
                new BaseData2.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (resultInt != BaseData2.OK) {
            listUsers = new UserClass[0];
            MySwingUtil.showMessage(frame, "попытка рабочего соединения", "ошибка соединения с БД", 5_000);
            return false;
        }
        // загрузка списка пользователей
        listUsers = callBack.getListUsers(true);
        if (listUsers == null) {
            return false;
        }
        if (setParameters != null) {
            setParameters.accept(parametersSql);
        }
        return true;
    }*/
    /*protected boolean getListUserFromConfig() {
        return getListUserFromConfig(null);
    }*/
}
