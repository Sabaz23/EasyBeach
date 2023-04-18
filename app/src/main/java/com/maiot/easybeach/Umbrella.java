package com.maiot.easybeach;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;
@Entity
public class Umbrella {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo @NonNull
    private int number;
    @ColumnInfo @NonNull
    private int row;
    //A is chair+chair, B is chair+bed, C is bed+bed
    @ColumnInfo @NonNull
    private char type;
    @ColumnInfo @NonNull
    private boolean free;
    @ColumnInfo
    private Location position;

    public Umbrella()
    {
        this.number = 0;
        this.row = 0;
        this.type = 'A';
        this.free = true;
        this.position = null;
    }
    public Umbrella(int num, int r, char t, boolean f, Location pos, Reservation[] res)
    {
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

    public Reservation[] getReservation()
    {
        return this.reservation;
    }

    public void setReservation(Reservation[] res)
    {
        this.reservation = res;
    }


    public void UpdateUmbrella(Umbrella u)
    {
        this.number = u.getNumber();
        this.row = u.getRow();
        this.type = u.getType();
        this.free = u.isFree();
        this.position = u.getPosition();
        this.reservation = u.getReservation();
    }
}
