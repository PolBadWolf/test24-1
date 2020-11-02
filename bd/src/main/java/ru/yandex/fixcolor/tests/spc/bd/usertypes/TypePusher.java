package ru.yandex.fixcolor.tests.spc.bd.usertypes;

import java.util.Date;

public class TypePusher {
    public long id_typePusher;          // индификатор
    public Date date_reg;               // когда создали
    public LoggerTypePusher loggerTypePusher;
    public Date date_unreg;             // когда деактивировали

    public TypePusher(long id_typePusher, Date date_reg, LoggerTypePusher loggerTypePusher, Date date_unreg) {
        this.id_typePusher = id_typePusher;
        this.date_reg = date_reg;
        this.loggerTypePusher = loggerTypePusher;
        this.date_unreg = date_unreg;
    }

    @Override
    public String toString() { return loggerTypePusher.nameType; }
}
