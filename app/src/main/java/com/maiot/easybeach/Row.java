package com.maiot.easybeach;

import android.util.Log;

public class Row {

    private Umbrella Umbrellas[];
    private int RowNumber;

    public Row()
    {
        Umbrellas = null;
        RowNumber = 0;
    }

    public Row(Umbrella umbrellas[], int rowNumber) {
        this.Umbrellas = umbrellas;
        this.RowNumber = rowNumber;
    }

    public int getRowNumber() {
        return RowNumber;
    }

    public void setRowNumber(int rowNumber) {
        RowNumber = rowNumber;
    }

    public Umbrella UmbrellaAtPosition(int i)
    {
        return Umbrellas[i];
    }

    public void LogAllRow()
    {
        Log.i("RowLog", "LogAllRow:");
        for(int i =0; i< Umbrellas.length; i++)
            Log.i("RowLog", "Ombrellone numero" + Umbrellas[i].getNumber() +
                    " con indice " + i + " fila " + Umbrellas[i].getRow());
    }

    public int NumberOfUmbrellaInRow()
    {
        return Umbrellas.length;
    }
}
