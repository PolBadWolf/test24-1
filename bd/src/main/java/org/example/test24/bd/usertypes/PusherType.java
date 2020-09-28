package org.example.test24.bd.usertypes;

import java.util.Date;

public class PusherType {
    public long id_typePusher;
    public Date date_upd;                   // когда правили/создали
    public long id_loggerUser;          // кто правил/создал
    public long id_loggerTypePusher;    // id записи
    public String name;                 // названия типа толкателя
    public int force;                   // номинальное усилие
    public int move_min;                // минимальное значение номинального хода
    public int move_max;                // максимальное значение номинального хода
    public Date date_unreg;             // когда деактивировали

    public PusherType(long id_typePusher, Date date_upd, long id_loggerUser, long id_loggerTypePusher, String name, int force, int move_min, int move_max, Date date_unreg) {
        this.id_typePusher = id_typePusher;
        this.date_upd = date_upd;
        this.id_loggerUser = id_loggerUser;
        this.id_loggerTypePusher = id_loggerTypePusher;
        this.name = name;
        this.force = force;
        this.move_min = move_min;
        this.move_max = move_max;
        this.date_unreg = date_unreg;
    }

    @Override
    public String toString() { return name; }
}
