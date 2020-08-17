package org.example.bd;

public interface SqlCheck_interface {
    static String[] getConnectListBd(String typeBD, String ip, String portServer, String login, String password) throws Exception {
        String[] list = new String[0];
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
}
