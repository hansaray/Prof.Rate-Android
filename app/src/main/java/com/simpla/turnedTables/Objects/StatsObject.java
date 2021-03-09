package com.simpla.turnedTables.Objects;

public class StatsObject {

    private String name;
    private long num;

    public StatsObject(String name, long num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
