package org.example.test24.bd;

import java.util.Date;

public class UserClass {
    public int id;
    public Date date_reg;
    public Date date_unreg;
    public String name;
    public String password;

    public UserClass(int id, Date date_reg, Date date_unreg, String name, String password) {
        this.id = id;
        this.date_reg = date_reg;
        this.date_unreg = date_unreg;
        this.name = name;
        this.password = password;
    }
}
