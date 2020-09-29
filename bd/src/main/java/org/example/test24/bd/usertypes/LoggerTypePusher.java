package org.example.test24.bd.usertypes;

import java.util.Date;

public class LoggerTypePusher {
    public long id_loggerTypePusher;
    public Date data_upd;
    public long id_loggerUserEdit;
    public long id_typePusher;
    public String nameType;
    public int forceNominal;
    public int moveNominal;
    public int unclenchingTime;

    public LoggerTypePusher(long id_loggerTypePusher, Date data_upd, long id_loggerUserEdit, long id_typePusher,
                            String nameType, int forceNominal, int moveNominal, int unclenchingTime) {
        this.id_loggerTypePusher = id_loggerTypePusher;
        this.data_upd = data_upd;
        this.id_loggerUserEdit = id_loggerUserEdit;
        this.id_typePusher = id_typePusher;
        this.nameType = nameType;
        this.forceNominal = forceNominal;
        this.moveNominal = moveNominal;
        this.unclenchingTime = unclenchingTime;
    }
}