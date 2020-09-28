package org.example.test24.bd.usertypes;

import java.util.Date;

public class User {
    final public static int RANG_USERS = 0;
    final public static int RANG_PUSHERS = 1;
    public long id_user;
    public Date date;
    public long id_loggerUser;
    public String name;
    public String password;
    public int rang;
    public Date date_unreg;

    public User(long id_user, Date date, long id_loggerUser, String name, String password, int rang, Date date_unreg) {
        this.id_user = id_user;
        this.date = date;
        this.id_loggerUser = id_loggerUser;
        this.name = name;
        this.password = password;
        this.rang = rang;
        this.date_unreg = date_unreg;
    }

    @Override
    public String toString() {
        return name;
    }
}
