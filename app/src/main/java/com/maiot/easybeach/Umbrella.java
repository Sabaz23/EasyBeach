package com.maiot.easybeach;

import android.location.Location;

public class Umbrella {

    private String ClientName;
    private int number;
    private int row;
    //A is chair+chair, B is chair+bed, C is bed+bed
    private char type;
    private boolean free;
    private Location position;

    public Umbrella()
    {
        this.ClientName = null;
        this.number = 0;
        this.row = 0;
        this.type = 'A';
        this.free = true;
        this.position = null;

    }
    public Umbrella(String client, int num, int r, char t, boolean f, Location pos)
    {
        this.ClientName = client;
        this.number = num;
        this.row = r;
        this.type = t;
        this.free = f;
        this.position = pos;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }
}
