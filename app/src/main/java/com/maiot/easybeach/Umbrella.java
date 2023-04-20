package com.maiot.easybeach;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;
public class Umbrella {
    private int number;
    private int row;
    //A is chair+chair, B is chair+bed, C is bed+bed
    private char type;
    private boolean free;

    private String token;

    public Umbrella()
    {
        this.number = 0;
        this.row = 0;
        this.type = 'A';
        this.free = true;
    }
    public Umbrella(int num, int r, char t, boolean f, String token)
    {
        this.number = num;
        this.row = r;
        this.type = t;
        this.free = f;
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

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return this.token;
    }

    public void UpdateUmbrella(Umbrella u)
    {
        this.number = u.getNumber();
        this.row = u.getRow();
        this.type = u.getType();
        this.free = u.isFree();
    }
}
