package org.example.test24.loader;

import org.example.test24.bd.BaseData1;

public class BaseData {
    public interface CallBack {
        BaseData1.TypeBaseData loadTypeBaseData();
        String[] getFileNameSql();
    }
    private CallBack callBack;
    private BaseData1.TypeBaseData typeBaseData;
    private BaseData1 bd;
    private String[] fileNameSql;

    public BaseData(CallBack callBack) {
        this.callBack = callBack;
        this.fileNameSql = callBack.getFileNameSql();
        typeBaseData = callBack.loadTypeBaseData();
    }
    public void initBaseData(BaseData1.TypeBaseData typeBaseData) {
        bd = BaseData1.init(typeBaseData, fileNameSql);
        try {
            //bd.;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
