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
    // ==============================================
    //                 var
    private BaseDataInterface testConnect = null;
    private BaseDataInterface workConnect = null;
    // ----------------------------------------------
    // создание тестового соединения
    @Override
    public void createTestConnect(TypeBaseData typeBaseData) throws IllegalStateException {
        switch (typeBaseData) {
            case MS_SQL:
                testConnect = new BaseDataMsSql();
                break;
            case MY_SQL:
                testConnect = new BaseDataMySql();
                break;
            default:
                testConnect = null;
                throw new IllegalStateException("BaseDataClass.createTestConnect Unexpected type Base Data: " + typeBaseData);
        }
    }
    // тестовое соединение
    @Override
    public int testConnectInit(Parameters parameters) {
        return testConnect.testConnectInit(parameters);
    }
    // тестовое соединение список доступных баз
    @Override
    public String[] testConnectListBd() throws Exception {
        return testConnect.testConnectListBd();
    }
    // тестовое соединение проверка структуры БД
    @Override
    public int testConnectCheckStructure(String base) {
        return testConnect.testConnectCheckStructure(base);
    }
    // создание рабочего соединения
    @Override
    public void createWorkConnect(TypeBaseData typeBaseData) throws IllegalStateException {
        switch (typeBaseData) {
            case MS_SQL:
                workConnect = new BaseDataMsSql();
                break;
            case MY_SQL:
                workConnect = new BaseDataMySql();
                break;
            default:
                workConnect = null;
                throw new IllegalStateException("BaseDataClass.createWorkConnect Unexpected type Base Data: " + typeBaseData);
        }
    }
    // инициализация рабочего соединения
    @Override
    public int workConnectInit(Parameters parameters) {
        return 0;
    }
}
