import org.example.test24.bd.BaseData;
import org.example.test24.bd.TypeBaseDate;
import org.example.test24.bd.usertypes.User;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BD {
    protected static BaseData conn;
    
    private static BaseData getConn() throws Exception {
        if (conn == null) {
            // чтение конфига
            BaseData.Config config = BaseData.Config.create();
            config.load1();
            TypeBaseDate typeBaseDate = config.getTypeBaseData();
            BaseData.Parameters parameters = BaseData.Parameters.create(typeBaseDate);
            parameters.load();
            conn = BaseData.create(parameters);
            conn.openConnect(parameters);
        }
        return conn;
    }
    
    @Test
    public void _1_getListBase() throws Exception {
        System.out.println("getListBase:");
        String[] list = null;
        list = getConn().getListBase();
        if (list == null) throw new Exception("ошибка получения списка");
        Arrays.stream(list).forEach(System.out::println);
        System.out.println();
    }

    @Test
    public void _2_checkStructureBd() throws Exception {
        System.out.println("checkStructureBd:");
        BaseData conn = getConn();
        boolean result = conn.checkStructureBd(conn.getCurrentBase());
        Assert.assertTrue(result);
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _3_getListUsers_actual() throws Exception {
        System.out.println("getListUsers(true):");
        BaseData conn = getConn();
        User[] listUser = conn.getListUsers(true);
        Arrays.stream(listUser).forEach(System.out::println);
        System.out.println();
    }

    @Test
    public void _4_getListUsers_all() throws Exception {
        System.out.println("getListUsers(false):");
        BaseData conn = getConn();
        User[] listUser = conn.getListUsers(false);
        Arrays.stream(listUser).forEach(System.out::println);
        System.out.println();
    }

    @Test
    public void _5_setNewUserPassword() throws Exception {
        System.out.println("setNewUserPassword замена пароля:");
        BaseData conn = getConn();
        User[] listUsers = conn.getListUsers(true);
        User user = listUsers[0];
        String password = "12";
        conn.setNewUserPassword(0, user, password);
        System.out.println("user " + user + " set password \"" + password + "\"");
        System.out.println();
    }
}
