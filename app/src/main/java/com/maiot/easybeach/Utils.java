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

public class Utils {
    private static String TAG = "Utils";

    public static Row[] LoadUmbrellaFile(File umbrellaFile, Context AppContext)
    {
        FileInputStream fis = null;
        try {
            boolean r = umbrellaFile.createNewFile(); //Crea il file solo se necessario (Ex. la prima volta che si avvia in assoluto)
            Log.i(TAG, "File creato: " + r);
            fis = AppContext.openFileInput(umbrellaFile.getName());
        } catch (IOException e) {
            Log.e(TAG,"Errore nella creazione del file " + e.getMessage());
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();

        int UmbrellaPerRow = 12;
        int i = 0;
        int rowNumber = 0;
        Umbrella tmp = null;
        Umbrella[] tmpArray = new Umbrella[]{};
        String[] SplittedLine = null;
        Row[] rows = new Row[]{};
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                //TODO qualcosa non funziona
                if(i % UmbrellaPerRow == 0 && i!=0) {
                    rows[rowNumber] = new Row(tmpArray,rowNumber+1);
                    tmpArray = new Umbrella[]{};
                    rowNumber++;
                }
                SplittedLine = line.split(",");
                tmp = new Umbrella(SplittedLine[0],
                        Integer.parseInt(SplittedLine[1]),Integer.parseInt(SplittedLine[2]),
                        SplittedLine[3].charAt(0),Boolean.parseBoolean(SplittedLine[4]),null);
                tmpArray[i] = tmp;
                stringBuilder.append(line).append('\n');
                line = reader.readLine();

            }
        } catch (IOException e) {
            Log.e(TAG,"Errore nell'apertura del file " + e.getMessage());
        } finally {
            String contents = stringBuilder.toString();
            return rows;
        }
    }

    public static void SaveUmbrellaFile(File umbrellaFile, Row[] data, Context AppContext)
    {
        try{
            FileOutputStream fos = AppContext.openFileOutput(umbrellaFile.getName(), Context.MODE_PRIVATE);
            for(int i = 0; i<data.length;i++)
            {
                for(int j=0;j<12;j++)
                    fos.write((data[i].UmbrellaAtPosition(j).getClientName() + "," +
                            data[i].UmbrellaAtPosition(j).getRow() + "," +
                            data[i].UmbrellaAtPosition(j).getNumber() + "," +
                            data[i].UmbrellaAtPosition(j).getType() + "," +
                            data[i].UmbrellaAtPosition(j).isFree() + "\n").getBytes(StandardCharsets.UTF_8));
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
}
