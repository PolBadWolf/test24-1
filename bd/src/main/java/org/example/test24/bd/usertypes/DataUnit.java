package org.example.test24.bd.usertypes;

public class DataUnit {
    public long data_id;
    public int n_cicle;
    public String pusherName;
    public String typeName;

    public DataUnit(long data_id, int n_cicle, String pusherName, String typeName) {
        this.data_id = data_id;
        this.n_cicle = n_cicle;
        this.pusherName = pusherName;
        this.typeName = typeName;
    }
}
