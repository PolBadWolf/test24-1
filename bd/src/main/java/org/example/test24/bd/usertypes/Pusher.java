package org.example.test24.bd.usertypes;

import java.util.Date;

public class Pusher {
    public int id_pusher;
    public Date date_reg;
    public Date date_unreg;
    public String name;

    public Pusher(int id_pusher, Date date_reg, Date date_unreg, String name) {
        this.id_pusher = id_pusher;
        this.date_reg = date_reg;
        this.date_unreg = date_unreg;
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
