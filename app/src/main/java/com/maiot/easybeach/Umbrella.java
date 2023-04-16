package com.maiot.easybeach;

import android.location.Location;

import java.util.Date;

public class Umbrella {
    private String ClientName;
    private int number;
    private int row;
    //A is chair+chair, B is chair+bed, C is bed+bed
    private char type;
    private boolean free;
    private Location position;

    private Date startDate, finishDate;

    public Umbrella()
    {
        this.ClientName = null;
        this.number = 0;
        this.row = 0;
        this.type = 'A';
        this.free = true;
        this.position = null;
        this.startDate = null;
        this.finishDate = null;
    }
    public Umbrella(String client, int num, int r, char t, boolean f, Location pos, Date sd, Date fd)
    {
        this.ClientName = client;
        this.number = num;
        this.row = r;
        this.type = t;
        this.free = f;
        this.position = pos;
        this.startDate = sd;
        this.finishDate = sd;
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

    public String getClientName()
    {
        return this.ClientName;
    }

    public void setClientName(String clientName)
    {
        this.ClientName = clientName;
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

    public Date getStartDate()
    {
        return this.startDate;
    }

    public Date getFinishDate()
    {
        return this.finishDate;
    }

    public void setStartDate(Date sd)
    {
        this.startDate = sd;
    }

    public void setFinishDate(Date fd)
    {
        this.finishDate = fd;
    }

    public void UpdateUmbrella(Umbrella u)
    {
        this.ClientName = u.getClientName();
        this.number = u.getNumber();
        this.row = u.getRow();
        this.type = u.getType();
        this.free = u.isFree();
        this.position = u.getPosition();
        this.startDate = u.getStartDate();
        this.finishDate = u.getFinishDate();
    }

    public void setUmbrellaFree()
    {
        this.ClientName = null;
        this.free = true;
        this.startDate = null;
        this.finishDate = null;
    }
}
