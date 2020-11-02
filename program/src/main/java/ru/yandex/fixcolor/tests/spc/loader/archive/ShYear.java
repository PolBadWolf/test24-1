package ru.yandex.fixcolor.tests.spc.loader.archive;

import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.bd.BaseDataException;

import java.util.ArrayList;

public class ShYear extends Shablon {
    public ShYear(String name, long idx) {
        super(name, idx);
    }

    public ShYear(String name, long idx, int level) {
        super(name, idx, level);
    }

    public static ArrayList<Shablon> getListYear(BaseData conn) throws BaseDataException {
        ArrayList<String> listConn = conn.getListFromYear();
        ArrayList<Shablon> list = new ArrayList<>();
        for (int i = 0; i < listConn.size(); i++) {
            list.add(new ShYear(listConn.get(i), i, 0));
        }
        return list;
    }
}
