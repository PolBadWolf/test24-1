package org.example.test24.bd.usertypes;

import java.util.Date;

public class PusherType {
    public long id_typePusher;          // индификатор
    public Date date_reg;               // когда создали
    public long id_loggerTypePusher;    // id записи типа толкателя
    public Date date_upd;               // когда правили
    public long id_loggerUser;          // кто правил
    public String nameType;             // названия типа толкателя
    public int forceNominal;            // номинальное усилие
    public int move_min;                // минимальное значение номинального хода
    public int move_max;                // максимальное значение номинального хода
    public Date date_unreg;             // когда деактивировали

    public PusherType(long id_typePusher, Date date_reg, long id_loggerTypePusher, Date date_upd, long id_loggerUser, String nameType, int forceNominal, int move_min, int move_max, Date date_unreg) {
        this.id_typePusher = id_typePusher;
        this.date_reg = date_reg;
        this.id_loggerTypePusher = id_loggerTypePusher;
        this.date_upd = date_upd;
        this.id_loggerUser = id_loggerUser;
        this.nameType = nameType;
        this.forceNominal = forceNominal;
        this.move_min = move_min;
        this.move_max = move_max;
        this.date_unreg = date_unreg;
    }

    @Override
    public String toString() { return nameType; }
}
