package org.example.test24.loader.archive;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.lib.MyLogger;

import java.util.ArrayList;
import java.util.logging.Level;

public class ShMounth extends Shablon {
    public ShMounth(String name, int idx) {
        super(name, idx);
    }

    public ShMounth(String name, int idx, int level) {
        super(name, idx, level);
    }
    public static ArrayList<Shablon> getListMounth(BaseData conn, String sample) {
        ArrayList<String> listConn;
        ArrayList<Shablon> list = new ArrayList<>();
        try {
            listConn = conn.getListFromMounth(sample);
        } catch (BaseDataException e) {
            MyLogger.myLog.log(Level.SEVERE, "получение списка месяцев", e);
            return list;
        }
        for (int i = 0; i < listConn.size(); i++) {
            list.add(new ShYear(listConn.get(i), i, 0));
        }
        return list;
    }
}
