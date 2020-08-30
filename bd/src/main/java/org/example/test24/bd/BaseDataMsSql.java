package org.example.test24.bd;

class BaseDataMsSql implements BaseDataInterface {
    @Override
    public boolean checkConnect(BaseData.Parameters parameters) {
        return false;
    }
}
