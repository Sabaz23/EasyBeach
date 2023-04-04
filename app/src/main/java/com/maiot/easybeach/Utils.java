package com.maiot.easybeach;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class Utils {
    private static String TAG = "Utils";
    private static final int UmbrellaPerRow = 12;
    public static Row[] LoadUmbrellaFile(File umbrellaFile, Context AppContext)
    {

        FileInputStream fis = null;
        try {
            fis = AppContext.openFileInput(umbrellaFile.getName());
        } catch (IOException e) {
            Log.e(TAG,"Errore nella creazione del file " + e.getMessage());
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Row> rows = new ArrayList<>();
        String contents = null;
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(TAG,"Errore nell'apertura del file " + e.getMessage());
        }
        contents = stringBuilder.toString();
        String[] Umbrellas = contents.split("\n");

        String[] UmbrellaValues = null;
        Umbrella tmp = null;
        ArrayList<Umbrella> tmpArray = new ArrayList<>();
        int rowNumber = 0;
        for(int i=0;i<Umbrellas.length;i++)
        {
            if((i % UmbrellaPerRow == 0 && i!=0) || i == Umbrellas.length - 1)
            {
                //Converto la lista di ombrelloni della fila in array
                Umbrella[] array = new Umbrella[tmpArray.size()];
                tmpArray.toArray(array);

                //La aggiungo all'array di file
                rows.add(new Row(array,rowNumber));

                //Svuoto l'array tmp per la nuova fila
                tmpArray.clear();

                rowNumber++;
            }

            UmbrellaValues = Umbrellas[i].split(",");

            //Variabili per inizializzare UmbrellaTmp
            String name = UmbrellaValues[0];
            int UmbrellaNum = Integer.parseInt(UmbrellaValues[1]);
            char TypeOfUmbrella = UmbrellaValues[3].charAt(0);
            boolean free = Boolean.parseBoolean(UmbrellaValues[4]);


            //Creo un ombrellone temporaneo con la riga letta e lo aggiungo all'array
            tmp = new Umbrella(name, UmbrellaNum, rowNumber, TypeOfUmbrella, free,null);
            tmpArray.add(tmp);
        }
        Row[] arrayOfRows = new Row[rows.size()];
        rows.toArray(arrayOfRows);
        return arrayOfRows;
    }

    public static void SaveUmbrellaFile(File umbrellaFile, Row[] data, Context AppContext)
    {
        try{
            boolean r = umbrellaFile.createNewFile(); //Crea il file solo se necessario (Ex. la prima volta che si avvia in assoluto)
            Log.i(TAG, "File creato: " + r);
            FileOutputStream fos = AppContext.openFileOutput(umbrellaFile.getName(), Context.MODE_PRIVATE);
            byte[] dataToWrite = null;

            for(int i = 0; i<data.length;i++)
            {
                for(int j=0;j<UmbrellaPerRow;j++) {
                    dataToWrite = (data[i].UmbrellaAtPosition(j).getClientName() + "," +
                            data[i].UmbrellaAtPosition(j).getNumber() + "," +
                            data[i].UmbrellaAtPosition(j).getRow() + "," +
                            data[i].UmbrellaAtPosition(j).getType() + "," +
                            data[i].UmbrellaAtPosition(j).isFree() + "\n").getBytes(StandardCharsets.UTF_8);
                    fos.write(dataToWrite);
                    Log.i(TAG, "DataToWrite" + data[i].UmbrellaAtPosition(j).getClientName() + "," +
                            data[i].UmbrellaAtPosition(j).getNumber() + "," +
                            data[i].UmbrellaAtPosition(j).getRow() + "," +
                            data[i].UmbrellaAtPosition(j).getType() + "," +
                            data[i].UmbrellaAtPosition(j).isFree() + "\n");
                }
            }
            Log.i(TAG, "Tutto ok! File scritto!");
        }
        catch (FileNotFoundException e) {
            Log.e(TAG,"File non trovato durante il salvataggio: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Errore durante la scrittura: " + e.getMessage());
        }
    }

    public static Row[] PopulateRowsFirstTime(File filename, Context appContext)
    {
        ArrayList<Row> rows = new ArrayList<>();
        Umbrella u = null;
        ArrayList<Umbrella>uArr = new ArrayList<>();
        int TotalUmbrellas = UmbrellaPerRow * 4;
        int rowNumber = 0;
        int umbrellaNumber = 1;
        String UmbrellaTypes = "ABC";
        for(int i=0; i<TotalUmbrellas; i++)
        {
            if(i % UmbrellaPerRow-1 == 0 && i!=0)
            {
                Umbrella[] UmbArray = new Umbrella[uArr.size()];
                uArr.toArray(UmbArray);

                rows.add(new Row(UmbArray,rowNumber));

                uArr.clear();

                rowNumber++;
                umbrellaNumber = 1;
            }
            Random r = new Random();
            u = new Umbrella(null, umbrellaNumber, rowNumber, UmbrellaTypes.charAt(r.nextInt(UmbrellaTypes.length())),
                    true,null);
            uArr.add(u);
            umbrellaNumber++;
        }
        Row[] RowArray = new Row[rows.size()];
        rows.toArray(RowArray);

        SaveUmbrellaFile(filename, RowArray, appContext);
        return RowArray;
    }

}
