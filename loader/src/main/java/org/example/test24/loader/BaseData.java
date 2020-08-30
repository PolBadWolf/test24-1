package org.example.test24.loader;

public class BaseData {
    public interface CallBack {
        org.example.test24.bd.BaseData.TypeBaseData loadTypeBaseData();
        String[] getFileNameSql();
    }
    private CallBack callBack;
    private org.example.test24.bd.BaseData.TypeBaseData typeBaseData;
    private org.example.test24.bd.BaseData bd;
    private String[] fileNameSql;

    public BaseData(CallBack callBack) {
        this.callBack = callBack;
        this.fileNameSql = callBack.getFileNameSql();
        typeBaseData = callBack.loadTypeBaseData();
    }
    public void initBaseData(org.example.test24.bd.BaseData.TypeBaseData typeBaseData) {
        bd = org.example.test24.bd.BaseData.init(typeBaseData, fileNameSql);
        try {
            //bd.;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
