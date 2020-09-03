package org.example.test24.bd;

public class BaseDataClass implements BaseData {
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
    private CallBack callBack;

    public BaseDataClass(CallBack callBack) {
        this.callBack = callBack;
    }
    // ==============================================
    //                 var
    private BaseDataInterface testConnect = null;
    private BaseDataInterface workConnect = null;
    // ----------------------------------------------
    // создание тестового соединения
    @Override
    public int createTestConnect(TypeBaseData typeBaseData, BaseData.Parameters parameters) {
        switch (typeBaseData) {
            case MS_SQL:
                testConnect = new BaseDataMsSql();
                break;
            case MY_SQL:
                testConnect = new BaseDataMySql();
                break;
            default:
                testConnect = null;
                return UNEXPECTED_TYPE_BD;
        }
        return testConnect.testConnectInit(parameters);
    }
    // тестовое соединение список доступных баз
    @Override
    public String[] getListBdFromTestConnect() {
        return testConnect.testConnectListBd();
    }
    // тестовое соединение проверка структуры БД
    @Override
    public int testConnectCheckStructure(String base) {
        return testConnect.testConnectCheckStructure(base);
    }
    // создание рабочего соединения
    @Override
    public int createWorkConnect(TypeBaseData typeBaseData, BaseData.Parameters parameters) {
        switch (typeBaseData) {
            case MS_SQL:
                workConnect = new BaseDataMsSql();
                break;
            case MY_SQL:
                workConnect = new BaseDataMySql();
                break;
            default:
                workConnect = null;
                return UNEXPECTED_TYPE_BD;
        }
        return workConnect.workConnectInit(parameters);
    }
    // чтение списка пользователей
    @Override
    public UserClass[] getListUsers(boolean actual) throws Exception {
        return workConnect.getListUsers(actual);
    }
    // установка нового пароля пользователя
    @Override
    public boolean setUserNewPassword(UserClass user, String newPassword) {
        if (workConnect == null) return false;
        return workConnect.setUserNewPassword(user, newPassword);
    }
}
