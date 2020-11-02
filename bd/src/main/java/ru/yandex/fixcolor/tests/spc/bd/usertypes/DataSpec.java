package ru.yandex.fixcolor.tests.spc.bd.usertypes;

import java.util.Date;

public class DataSpec {
    public long id_dataSpec;
    public long id_user;
    public long id_pusher;
    public Date date_upd;

    public DataSpec(long id_dataSpec, long id_user, long id_pusher, Date date_upd) {
        this.id_dataSpec = id_dataSpec;
        this.id_user = id_user;
        this.id_pusher = id_pusher;
        this.date_upd = date_upd;
    }

    public DataSpec(long id_dataSpec, long id_user, long id_pusher) {
        this.id_dataSpec = id_dataSpec;
        this.id_user = id_user;
        this.id_pusher = id_pusher;
    }
}
