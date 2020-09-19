package org.example.test24.bd;

import java.util.Date;

public class UserClass {
    final public static int RANG_USERS = 0;
    final public static int RANG_PUSHERS = 1;
    public int id;
    public Date date_reg;
    public String name;
    public String password;
    public int rang;
    public int id_unreg;

    public UserClass(int id, Date date_reg, String name, String password, int rang, int id_unreg) {
        this.id = id;
        this.date_reg = date_reg;
        this.name = name;
        this.password = password;
        this.rang = rang;
        this.id_unreg = id_unreg;
    }
    public UserClass() {
        this.id = 0;
        this.date_reg = null;
        this.name = "";
        this.password = "";
        this.rang = 0;
        this.id_unreg = -1;
    }

    @Override
    public String toString() {
        return name;
    }
}
