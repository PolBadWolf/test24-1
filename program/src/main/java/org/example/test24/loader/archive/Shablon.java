package org.example.test24.loader.archive;

import java.util.ArrayList;

public class Shablon {
    protected String name;
    protected int idx;
    protected int level;
    public ArrayList<Shablon> children = new ArrayList<>();

    public Shablon(String name, int idx) {
        this.name = name;
        this.idx = idx;
    }

    public Shablon(String name, int idx, int level) {
        this.name = name;
        this.idx = idx;
        this.level = level;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getIdx() {
        return idx;
    }

    public int getLevel() {
        return level;
    }

    public int nChildren() {
        return children.size();
    }
}
