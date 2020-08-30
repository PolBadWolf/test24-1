package org.example.test24.bd;

class BaseDataMySql implements BaseDataInterface {
    @Override
    public boolean checkConnect(BaseData.Parameters parameters) {
        return false;
    }
}
