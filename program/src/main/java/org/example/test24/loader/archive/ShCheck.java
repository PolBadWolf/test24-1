package org.example.test24.loader.archive;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.usertypes.DataUnit;
import org.example.test24.lib.MyLogger;

import java.util.ArrayList;
import java.util.logging.Level;

public class ShCheck extends Shablon {
    public ShCheck(String name, long idx) {
        super(name, idx);
    }

    public ShCheck(String name, long idx, int level) {
        super(name, idx, level);
    }

    public static ArrayList<Shablon> getListChecks(BaseData conn, String sample) {
        ArrayList<DataUnit> listConn;
        ArrayList<Shablon> list = new ArrayList<>();
        DataUnit dataUnit;
        String name;
        try {
            listConn = conn.getListFromPusherChecks(sample);
        } catch (BaseDataException e) {
            MyLogger.myLog.log(Level.SEVERE, "получение списка толкателей", e);
            return list;
        }
        int n = 0, n_cicle;
        for (int i = 0; i < listConn.size(); i++) {
            dataUnit = listConn.get(i);
            n_cicle = dataUnit.n_cicle;
            if (n_cicle < 2) n++;
            name = String.valueOf(n);
            if (n_cicle > 0) name += ":" + n_cicle;
            name += " - " + dataUnit.pusherName + "/" + dataUnit.typeName;
            list.add(new ShCheck(name, dataUnit.data_id, 3));
        }
        return list;
    }
}
