package ru.yandex.fixcolor.tests.spc.bd.usertypes;

import java.sql.Blob;
import java.util.Date;

public class DataUnitMeasured {
    public long id_data;
    public Date dateTime;
    public long id_spec;
    public int n_cicle;
    public int ves;
    public int tik_shelf;
    public int tik_back;
    public int tik_stop;
    public int forceNominal;
    public int moveNominal;
    public int unclenchingTime;
    public Blob dataMeasured;
    public long id_user;
    public long id_pusher;

    public DataUnitMeasured(long id_data, Date dateTime, long id_spec, int n_cicle, int ves, int tik_shelf,
                            int tik_back, int tik_stop, int forceNominal, int moveNominal, int unclenchingTime,
                            Blob dataMeasured, long id_user, long id_pusher) {
        this.id_data = id_data;
        this.dateTime = dateTime;
        this.id_spec = id_spec;
        this.n_cicle = n_cicle;
        this.ves = ves;
        this.tik_shelf = tik_shelf;
        this.tik_back = tik_back;
        this.tik_stop = tik_stop;
        this.forceNominal = forceNominal;
        this.moveNominal = moveNominal;
        this.unclenchingTime = unclenchingTime;
        this.dataMeasured = dataMeasured;
        this.id_user = id_user;
        this.id_pusher = id_pusher;
    }
}
