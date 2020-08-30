package org.example.test24.bd;

public class BaseDataClass implements BaseData {
    static String typeBaseDataString(int codeTypeBaseData) {
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
    static TypeBaseData typeBaseDataCode(String nameTypeBaseData) {
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

    @Override
    public boolean checkConnect(TypeBaseData typeBaseData, Parameters parameters) {
        boolean stat = false;
        switch (typeBaseData) {
            case MS_SQL:
                stat = new BaseDataMsSql().checkConnect(parameters);
                break;
            case MY_SQL:
                stat = new BaseDataMySql().checkConnect(parameters);
                break;
        }
        return stat;
    }

    @Override
    public boolean checkConnectBase(TypeBaseData typeBaseData, Parameters parameters) {
        return false;
    }

    @Override
    public boolean checkStructBase(TypeBaseData typeBaseData, Parameters parameters) {
        return false;
    }

    @Override
    public String[] getListUsers(TypeBaseData typeBaseData, Parameters parameters) {
        return new String[0];
    }

    @Override
    public String[] getListBaseData(TypeBaseData typeBaseData, Parameters parameters) {
        return new String[0];
    }

    @Override
    public String[] getListUsers() {
        return new String[0];
    }
}
