package com.maiot.easybeach;

import java.util.Calendar;
import java.util.Date;

public class Reservation
{
    private String ClientName;
    private Date StartDate;
    private Date FinishDate;


    public Reservation()
    {
        this.StartDate = null;
        this.FinishDate = null;
    }

    public Reservation(Date sd, Date fd)
    {
        this.StartDate = sd;
        this.FinishDate = fd;
    }

    public boolean isReservedNow()
    {
        Calendar c = Calendar.getInstance();
        return c.after(StartDate) && c.before(FinishDate);
    }

    public boolean isInPast()
    {
        Calendar c = Calendar.getInstance();
        return c.after(FinishDate);
    }

    public boolean isInFuture()
    {
        Calendar c = Calendar.getInstance();
        return c.before(StartDate);
    }

    public Date getStartDate() {
        return StartDate;
    }

    public Date getFinishDate()
    {
        return FinishDate;
    }

    public String getClientName()
    {
        return ClientName;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public void setFinishDate(Date finishDate) {
        FinishDate = finishDate;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }
}
