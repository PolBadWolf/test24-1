import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.bd.TypeBaseDate;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Pusher;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.TypePusher;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.User;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BD {
    protected static BaseData conn;
    static String testRecord1 = "test-record1";
    static String testRecord2 = "test-record2";

    private static BaseData getConn() throws Exception {
        if (conn == null) {
            // чтение конфига
            BaseData.Config config = BaseData.Config.create();
            config.load();
            TypeBaseDate typeBaseDate = config.getTypeBaseData();
            BaseData.Parameters parameters = BaseData.Parameters.create(typeBaseDate);
            parameters.load();
            conn = BaseData.create(parameters);
            conn.openConnect(parameters);
        }
        return conn;
    }

    private boolean searchUser(String sampleName, User[] listUsers, User[] target) {
        if (sampleName == null || listUsers == null || target == null) return true;
        if (sampleName.length() == 0 || target.length != 1) return true;
        boolean flag = true;
        for (User user : listUsers) {
            if (user.surName.equals(sampleName)) {
                target[0] = user;
                flag = false;
                break;
            }
        }
        return flag;
    }

    private boolean searchTypePusher(String testRecord, TypePusher[] listTypePushers, TypePusher[] target) {
        if (testRecord == null || listTypePushers == null || target == null) return true;
        if (testRecord.length() == 0 || target.length != 1) return true;
        boolean flag = true;
        for (TypePusher typePusher : listTypePushers) {
            if (typePusher.loggerTypePusher.nameType.equals(testRecord)) {
                target[0] = typePusher;
                flag = false;
                break;
            }
        }
        return flag;
    }

    private boolean searchPusher(String testRecord, Pusher[] listPushers, Pusher[] target) {
        if (testRecord == null || listPushers == null || target == null) return true;
        if (testRecord.length() == 0 || target.length != 1) return true;
        boolean flag = true;
        for (Pusher pusher : listPushers) {
            if (pusher.loggerPusher.namePusher.equals(testRecord)) {
                target[0] = pusher;
                flag = false;
                break;
            }
        }
        return flag;
    }
    
    @Test
    public void _01_getListBase() throws Exception {
        System.out.println("getListBase:");
        String[] list;
        list = getConn().getListBase();
        if (list == null) throw new Exception("ошибка получения списка");
        Arrays.stream(list).forEach(System.out::println);
        System.out.println();
    }

    @Test
    public void _02_checkStructureBd() throws Exception {
        System.out.println("checkStructureBd:");
        BaseData conn = getConn();
        boolean result = conn.checkStructureBd(conn.getCurrentBase());
        Assert.assertTrue(result);
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _03_writeNewUser() throws Exception {
        System.out.println("writeNewUser:");
        BaseData conn = getConn();
        String pass = "11";
        int rang = (1 << User.RANG_USERS) | (1 << User.RANG_PUSHERS);
        conn.writeNewUser(0, testRecord1, pass, rang);
        System.out.println("создан пользователь: \"" + testRecord1 + "\" с паролем \"" + pass + "\"");
        System.out.println();
    }

    @Test
    public void _04_getListUsers_actual() throws Exception {
        System.out.println("getListUsers(true):");
        BaseData conn = getConn();
        User[] listUser = conn.getListUsers(true);
        Arrays.stream(listUser).forEach(user -> System.out.println("пользователь: \"" + user.surName + "\" , пароль: \"" + user.userPassword + "\""));
        System.out.println();
    }

    @Test
    public void _05_setNewUserPassword() throws Exception {
        System.out.println("setNewUserPassword замена пароля:");
        BaseData conn = getConn();
        User[] listUsers = conn.getListUsers(true);
        User[] target = new User[1];
        if (searchUser(testRecord1, listUsers, target)) throw  new Exception("ошибка поиска пользователя");
        User user = target[0];
        String password = "12";
        conn.setNewUserPassword(0, user, password);
        System.out.println("user " + user + " set password \"" + password + "\"");
        System.out.println();
    }

    @Test
    public void _06_updateDataUser() throws Exception {
        System.out.println("updateDataUser");
        BaseData conn = getConn();
        User[] listUsers = conn.getListUsers(true);
        User[] target = new User[1];
        if (searchUser(testRecord1, listUsers, target)) throw new Exception("ошибка поиска пользователя");
        User user = target[0];
        conn.updateDataUser(user, 0, testRecord2, user.userPassword, user.rang);
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _07_deleteUser() throws Exception {
        System.out.println("deleteUser");
        BaseData conn =getConn();
        User[] target = new User[1];
        User user;
        while (!searchUser(testRecord1, conn.getListUsers(true), target)) {
            user = target[0];
            conn.deleteUser(0, user);
            System.out.println(" delete \"" + user.id_user + ":" + user.id_loggerUser + ":" + user.surName + "\"");
        }
        while (!searchUser(testRecord2, conn.getListUsers(true), target)) {
            user = target[0];
            conn.deleteUser(0, user);
            System.out.println(" delete \"" + user.id_user + ":" + user.id_loggerUser + ":" + user.surName + "\"");
        }
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _08_getListUsers_all() throws Exception {
        System.out.println("getListUsers(false):");
        BaseData conn = getConn();
        User[] listUser = conn.getListUsers(false);
        Arrays.stream(listUser).forEach(System.out::println);
        System.out.println();
    }

    @Test
    public void _09_writeNewTypePusher() throws Exception {
        System.out.println("writeNewTypePusher: ");
        BaseData conn = getConn();
        conn.writeNewTypePusher(0, testRecord1, 100, 90, 0.26f, 0.26f, 30);
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _10_getListTypePushers() throws Exception {
        System.out.println("getListTypePushers:");
        BaseData conn = getConn();
        TypePusher[] listTypePushers = conn.getListTypePushers(true);
        TypePusher typePusher;
        for (TypePusher listTypePusher : listTypePushers) {
            typePusher = listTypePusher;
            System.out.println(typePusher.id_typePusher + ":" + typePusher.loggerTypePusher.id_loggerTypePusher + ":" + typePusher.loggerTypePusher.nameType);
        }
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _11_updateTypePusher() throws Exception {
        System.out.println("updateTypePusher:");
        BaseData conn = getConn();
        TypePusher[] listTypePushers = conn.getListTypePushers(true);
        TypePusher[] target = new TypePusher[1];
        if (searchTypePusher(testRecord1, listTypePushers, target)) throw new Exception("ошибка поиска типа пользователя");
        TypePusher typePusher = target[0];
        conn.updateTypePusher(typePusher, 0, testRecord2, 101, 88, 0.47f, 0.77f, 200);
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _12_deleteTypePusher() throws Exception {
        System.out.println("deleteTypePusher:");
        BaseData conn = getConn();
        TypePusher[] target = new TypePusher[1];
        TypePusher typePusher;
        while (!searchTypePusher(testRecord1, conn.getListTypePushers(true), target)) {
            typePusher = target[0];
            conn.deleteTypePusher(0, typePusher);
            System.out.println("delete " + typePusher.id_typePusher + ":" + typePusher.loggerTypePusher.id_loggerTypePusher + ":" + typePusher.loggerTypePusher.nameType);
        }
        while (!searchTypePusher(testRecord2, conn.getListTypePushers(true), target)) {
            typePusher = target[0];
            conn.deleteTypePusher(0, typePusher);
            System.out.println("delete " + typePusher.id_typePusher + ":" + typePusher.loggerTypePusher.id_loggerTypePusher + ":" + typePusher.loggerTypePusher.nameType);
        }
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _13_getListTypePushers() throws Exception {
        System.out.println("getListTypePushers:");
        BaseData conn = getConn();
        TypePusher[] listTypePushers = conn.getListTypePushers(false);
        TypePusher typePusher;
        for (TypePusher listTypePusher : listTypePushers) {
            typePusher = listTypePusher;
            System.out.println(typePusher.id_typePusher + ":" + typePusher.loggerTypePusher.id_loggerTypePusher + ":" + typePusher.loggerTypePusher.nameType +
                    "  <==>  " + (typePusher.date_unreg == null ? "NULL" : typePusher.date_unreg.toString()));
        }
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _14_getListPushers() throws Exception {
        System.out.println("getListPushers(actual):");
        BaseData conn = getConn();
        Pusher[] listPushers = conn.getListPushers(true);
        for (Pusher pusher : listPushers) {
            System.out.println(pusher.id_pusher + ":" +
                    pusher.loggerPusher.id_loggerPusher + ":" +
                    pusher.loggerPusher.namePusher + " ==> " +
                    pusher.loggerPusher.typePusher.id_typePusher + ":" +
                    pusher.loggerPusher.typePusher.loggerTypePusher.nameType
            );
        }
        System.out.println();
    }

    @Test
    public void _15_writeNewPusher() throws Exception {
        System.out.println("writeNewPusher");
        BaseData conn = getConn();
        long id_typePusher = conn.writeNewTypePusher(0, testRecord1, 222, 22, 0.2f, 0.3f, 200);
        long id_pusher = conn.writeNewPusher(0, testRecord1, id_typePusher);
        Assert.assertTrue(id_pusher > 0);
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _16_updatePusher() throws Exception {
        System.out.println("updatePusher: ");
        BaseData conn = getConn();
        Pusher[] listPushers = conn.getListPushers(true);
        Pusher[] target = new Pusher[1];
        Pusher pusher;
        if (searchPusher(testRecord1, listPushers, target)) throw new Exception("толкателей не найдено");
        pusher = target[0];
        conn.updatePusher(pusher, 0, testRecord2, pusher.loggerPusher.typePusher.id_typePusher);
        System.out.println("ok");
        System.out.println();
    }

    @Test
    public void _17_deletePusher() throws Exception {
        System.out.println("deletePusher:");
        BaseData conn = getConn();
        Pusher[] target = new Pusher[1];
        Pusher pusher;
        while (!searchPusher(testRecord1, conn.getListPushers(true), target)) {
            pusher = target[0];
            conn.deletePusher(0, pusher);
            System.out.println(
                    pusher.id_pusher + ":" +
                            pusher.loggerPusher.id_loggerPusher + ":" +
                            pusher.loggerPusher.namePusher + " ==> " +
                            pusher.loggerPusher.typePusher.id_typePusher + ":" +
                            pusher.loggerPusher.typePusher.loggerTypePusher.id_loggerTypePusher + ":" +
                            pusher.loggerPusher.typePusher.loggerTypePusher.nameType
            );
        }
        while (!searchPusher(testRecord2, conn.getListPushers(true), target)) {
            pusher = target[0];
            conn.deletePusher(0, pusher);
            System.out.println(
                    pusher.id_pusher + ":" +
                            pusher.loggerPusher.id_loggerPusher + ":" +
                            pusher.loggerPusher.namePusher + " ==> " +
                            pusher.loggerPusher.typePusher.id_typePusher + ":" +
                            pusher.loggerPusher.typePusher.loggerTypePusher.id_loggerTypePusher + ":" +
                            pusher.loggerPusher.typePusher.loggerTypePusher.nameType
            );
        }
        System.out.println("end del");
        System.out.println();
        _12_deleteTypePusher();
    }

    @Test
    public void _18_getListPushers() throws Exception {
        System.out.println("getListPushers(all): ");
        BaseData conn = getConn();
        Pusher[] listPushers = conn.getListPushers(false);
        for (Pusher pusher : listPushers) {
            System.out.println(pusher.id_pusher + ":" +
                    pusher.loggerPusher.id_loggerPusher + ":" +
                    pusher.loggerPusher.namePusher + " ==> " +
                    pusher.loggerPusher.typePusher.id_typePusher + ":" +
                    pusher.loggerPusher.typePusher.loggerTypePusher.nameType + "\t" +
                    (pusher.date_unreg == null ? "NULL" : pusher.date_unreg.toString())
            );
        }
        System.out.println();
    }

    @Test
    public void _19_getCountPushersFromType() throws Exception {
        System.out.println("getCountPushersFromType:");
        BaseData conn = getConn();
        long id_typePusher;
        int count;
        TypePusher[] targetTypePusher = new TypePusher[1];
        String[] targetNamePusher = new String[1];
        Pusher[] targetPusher = new Pusher[1];
        // создать тип толкателя
        id_typePusher = conn.writeNewTypePusher(0, "c_pu", 222, 22, 0.2f, 0.22f, 200);
        // подсчитать
        count = conn.getCountPushersFromType(id_typePusher, targetNamePusher);
        Assert.assertNotEquals(0, count);
        // удалить тип толкателя
        while (!searchTypePusher("c_pu", conn.getListTypePushers(true), targetTypePusher)) {
            conn.deleteTypePusher(0, targetTypePusher[0]);
        }
        // подсчитать
        count = conn.getCountPushersFromType(id_typePusher, targetNamePusher);
        Assert.assertNotEquals(0, count);
        // удалить толкатель
        while (!searchPusher("pu_c", conn.getListPushers(true), targetPusher)) {
            conn.deletePusher(0, targetPusher[0]);
        }
        // создать тип толкателя
        id_typePusher = conn.writeNewTypePusher(0, "c_pu", 222, 22, 0.24f, 0.13f, 200);
        // подсчитать
        count = conn.getCountPushersFromType(id_typePusher, targetNamePusher);
        Assert.assertEquals(0, count);
        // удалить тип толкателя
        conn.deleteTypePusher(0, conn.getTypePusher(id_typePusher));
        //
        System.out.println();
    }
}
