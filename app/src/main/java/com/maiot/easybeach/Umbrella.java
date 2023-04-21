package com.maiot.easybeach;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
public class Umbrella {
    private int uid;
    //A is chair+chair, B is chair+bed, C is bed+bed
    private char type;
    private boolean free;
    private String token;

    public Umbrella()
    {
        this.type = 'A';
        this.free = true;
    }
    public Umbrella(int uid, char t, boolean f, String token)
    {
        this.uid = uid;
        this.type = t;
        this.free = f;
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

    public int getUid() {return this.uid;}


    public void UpdateUmbrella(Umbrella u)
    {
        this.type = u.getType();
        this.free = u.isFree();
    }
}
