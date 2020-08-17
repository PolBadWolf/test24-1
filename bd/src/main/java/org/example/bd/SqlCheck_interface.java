package org.example.bd;

public interface SqlCheck_interface {
    static String[] getConnectListBd(String typeBD, String ip, String portServer, String login, String password) throws Exception {
        String[] list;
        switch (typeBD) {
            case "MS_SQL" :
                list = DataBaseMsSql.getConnectListBd(ip, portServer, login, password);
                break;
            case "MY_SQL" :
                list = DataBaseMySql.getConnectListBd(ip, portServer, login, password);
                break;
            default:
                throw new Exception("неизвестный тип BD");
        }
        return list;
    }

    static boolean testStuctBase(String typeBD, String ip, String portServer, String login, String password, String base) {
        boolean res = false;
        switch (typeBD) {
            case "MS_SQL" :
                res = DataBaseMsSql.testStuctBase1(ip, portServer, login, password, base);
                break;
            case "MY_SQL" :
                res = DataBaseMySql.testStuctBase1(ip, portServer, login, password, base);
                break;
            default:
                res = false;
        }
        return res;
    }
}
