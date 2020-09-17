package org.example.test24.bd;

import java.util.Date;

public class UserClass {
    final public static int RANG_USERS = 0;
    final public static int RANG_PUSHERS = 1;
    public int id;
    public Date date_reg;
    public Date date_unreg;
    public String name;
    public String password;
    public int rang;

    public UserClass(int id, Date date_reg, Date date_unreg, String name, String password, int rang) {
        this.id = id;
        this.date_reg = date_reg;
        this.date_unreg = date_unreg;
        this.name = name;
        this.password = password;
        this.rang = rang;
    }
    public UserClass() {
        this.id = 0;
        this.date_reg = null;
        this.date_unreg = null;
        this.name = "";
        this.password = "";
        this.rang = 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
