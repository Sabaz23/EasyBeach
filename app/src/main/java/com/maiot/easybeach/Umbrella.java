package com.maiot.easybeach;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
public class Umbrella {
    private int uid;
    //Il tipo è indicato con A,B,C
    private char type;
    private boolean free;

    public Umbrella()
    {
        this.type = 'A';
        this.free = true;
    }
    public Umbrella(int uid, char t, boolean f)
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

    public int getUid() {return this.uid;}


    public void UpdateUmbrella(Umbrella u)
    {
        this.type = u.getType();
        this.free = u.isFree();
    }
}
