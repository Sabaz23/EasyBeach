package com.maiot.easybeach;

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

}
