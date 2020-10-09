import org.example.test24.bd.BaseData;
import org.example.test24.bd.ParametersConfig;
import org.example.test24.bd.TypeBaseDate;
import org.junit.Test;

import java.util.Arrays;

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
    public void getListBase() throws Exception {
        System.out.println("getListBase:");
        String[] list = null;
        list = getConn().getListBase();
        if (list == null) throw new Exception("ошибка получения списка");
        Arrays.stream(list).forEach(System.out::println);
        System.out.println();
    }
}
