package ru.yandex.fixcolor.tests.spc.bd.usertypes;

import java.util.Date;

public class User {
    final public static int RANG_USERS = 0;
    final public static int RANG_PUSHERS = 1;
    public long id_user;
    public Date date_reg;
    public long id_loggerUser;
    public Date date_upd;
    public long id_loggerUserEdit;
    public String surName;
    public String userPassword;
    public int rang;
    public Date date_unreg;

    public User(long id_user, Date date_reg, long id_loggerUser, Date date_upd, long id_loggerUserEdit, String surName, String userPassword, int rang, Date date_unreg) {
        this.id_user = id_user;
        this.date_reg = date_reg;
        this.id_loggerUser = id_loggerUser;
        this.date_upd = date_upd;
        this.id_loggerUserEdit = id_loggerUserEdit;
        this.surName = surName;
        this.userPassword = userPassword;
        this.rang = rang;
        this.date_unreg = date_unreg;
    }

    @Override
    public String toString() {
        return surName;
    }
}
