package ru.yandex.fixcolor.tests.spc.loader.archive;

import java.util.ArrayList;

public class Shablon {
    protected String name;
    protected long idx;
    protected int level;
    public ArrayList<Shablon> children = new ArrayList<>();

    public Shablon(String name, long idx) {
        this.name = name;
        this.idx = idx;
    }

    public Shablon(String name, long idx, int level) {
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

    public long getIdx() {
        return idx;
    }

    public int getLevel() {
        return level;
    }

    public int nChildren() {
        return children.size();
    }
}
