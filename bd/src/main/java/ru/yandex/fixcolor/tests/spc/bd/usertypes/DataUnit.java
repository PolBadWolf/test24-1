package ru.yandex.fixcolor.tests.spc.bd.usertypes;

import java.util.Date;

public class DataUnit {
    public long data_id;
    public int n_cicle;
    public String pusherName;
    public String typeName;
    public Date dateIzm;

    public DataUnit(long data_id, int n_cicle, String pusherName, String typeName, Date dateIzm) {
        this.data_id = data_id;
        this.n_cicle = n_cicle;
        this.pusherName = pusherName;
        this.typeName = typeName;
        this.dateIzm = dateIzm;
    }
}
