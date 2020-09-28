package org.example.test24.bd.usertypes;

import java.util.Date;

public class Pusher {
    public long id_pusher;
    public Date date_reg;
    public long id_loggerPusher;
    public Date date_upd;
    public PusherType pusherType;
    public Date date_unreg;

    public Pusher(long id_pusher, Date date_reg, PusherType pusherType, Date date_unreg) {
        this.id_pusher = id_pusher;
        this.date_reg = date_reg;
        this.pusherType = pusherType;
        this.date_unreg = date_unreg;
    }

    @Override
    public String toString() {
        return pusherType.nameType;
    }
}
