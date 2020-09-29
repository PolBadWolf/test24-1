package org.example.test24.bd.usertypes;

import java.util.Date;

public class LoggerPusher {
    public long id_loggerPusher;
    public Date date_upd;
    public long id_loggerUserEdit;
    public long id_pusher;
    public String namePusher;
    public LoggerTypePusher loggerTypePusher;

    public LoggerPusher(long id_loggerPusher, Date date_upd, long id_loggerUserEdit, long id_pusher,
                        String namePusher, LoggerTypePusher loggerTypePusher) {
        this.id_loggerPusher = id_loggerPusher;
        this.date_upd = date_upd;
        this.id_loggerUserEdit = id_loggerUserEdit;
        this.id_pusher = id_pusher;
        this.namePusher = namePusher;
        this.loggerTypePusher = loggerTypePusher;
    }
}