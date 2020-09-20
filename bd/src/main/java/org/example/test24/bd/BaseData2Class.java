package org.example.test24.bd;

import java.util.function.Consumer;

public class BaseData2Class implements BaseData2 {
    public static String typeBaseDataString(int codeTypeBaseData) {
        String stroka;
        switch (codeTypeBaseData) {
            case MS_SQL:
                stroka = "MS_SQL";
                break;
            case MY_SQL:
                stroka = "MY_SQL";
                break;
            default:
                stroka = "ERROR";
        }
        return stroka;
    }
    public static TypeBaseData typeBaseDataCode(String nameTypeBaseData) {
        TypeBaseData typeBaseData;
        if (nameTypeBaseData == null) {
            typeBaseData = TypeBaseData.ERROR;
        } else {
            switch (nameTypeBaseData.toUpperCase()) {
                case "MS_SQL":
                    typeBaseData = TypeBaseData.MS_SQL;
                    break;
                case "MY_SQL":
                    typeBaseData = TypeBaseData.MY_SQL;
                    break;
                default:
                    typeBaseData = TypeBaseData.ERROR;
            }
        }
        return typeBaseData;
    }
    static String typeBaseDataString(BaseData1.TypeBaseData codeTypeBaseData) {
        String stroka;
        switch (codeTypeBaseData) {
            case MS_SQL:
                stroka = "MS_SQL";
                break;
            case MY_SQL:
                stroka = "MY_SQL";
                break;
            default:
                stroka = "ERROR";
        }
        return stroka;
    }
    // ==============
    //private CallBack callBack;

    public BaseData2Class(/*CallBack callBack*/) {
        //this.callBack = callBack;
    }
    // ==============================================
    //                 var
    private BaseDataInterface2 testConnect = null;
    private BaseDataInterface2 workConnect = null;
    // ----------------------------------------------
    // создание тестового соединения
    @Override
    public Status createTestConnect(TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
        switch (typeBaseData) {
            case MS_SQL:
                testConnect = new BaseDataMsSql2();
                break;
            case MY_SQL:
                testConnect = new BaseDataMySql2();
                break;
            default:
                testConnect = null;
                return Status.UNEXPECTED_TYPE_BD;
        }
        return testConnect.createTestConnect(parameters);
    }
    // тестовое соединение список доступных баз
    @Override
    public boolean requestListBdFromTestConnect(Consumer<String[]> list) {
        return testConnect.requestListBdFrom(list);
    }
    // тестовое соединение проверка структуры БД
    @Override
    public Status checkCheckStructureBd(String base) {
        return testConnect.checkCheckStructureBd(base);
    }
    // создание рабочего соединения
    @Override
    public Status createWorkConnect(TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
        switch (typeBaseData) {
            case MS_SQL:
                workConnect = new BaseDataMsSql2();
                break;
            case MY_SQL:
                workConnect = new BaseDataMySql2();
                break;
            default:
                workConnect = null;
                return Status.UNEXPECTED_TYPE_BD;
        }
        return workConnect.createWorkConnect(parameters);
    }
    // чтение списка пользователей
    @Override
    public User[] getListUsers(boolean actual) throws Exception {
        if (workConnect == null) {
            throw new Exception("Не инициировано рабочее соединение");
        }
        return workConnect.getListUsers(actual);
    }


    @Override
    public String[] getListBd() throws Exception {
        return testConnect.getListBd();
    }

    // установка нового пароля пользователя
    @Override
    public boolean setUserNewPassword(User user, String newPassword) {
        if (workConnect == null) return false;
        return workConnect.setUserNewPassword(user, newPassword);
    }
}
