package com.maiot.easybeach;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {
    private static final String TAG = "Utils";
    private static final int TotalUmbrellas = 12;

    private final static String FetchMapUrl = "http://192.168.1.186/umbrellaapp/fetch_map.php";
    public final static String ServerUrl = "http://192.168.1.186/";

    public static JSONArray TestFetch() throws  IOException{
        //Controlla se riesce a connettersi al server dove si trova il file della mappa
        if(isConnectedToThisServer(ServerUrl,1000)) {
            final OkHttpClient client = new OkHttpClient();

            //Crea la richiesta e prova a prenderne il body (il file php ritorna un file json)
            Request request = new Request.Builder().url(FetchMapUrl).build();
            try (Response response = client.newCall(request).execute()) {
                return new JSONArray(response.body().string());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }else {
            //Se non riesce a connettersi, ritorna null
            return null;
        }
    }

    public static Umbrella[] LoadUmbrellaFile(File umbrellaFile, Context AppContext)
    {
        String contents = ReadFile(AppContext, umbrellaFile);
        ArrayList<Umbrella> umbrellaArrayList = new ArrayList<>();
        String[] Umbrellas = contents.split("\n");
        String[] UmbrellaValues;
        Umbrella tmp;
        ArrayList<Umbrella> tmpArray = new ArrayList<>();
        for(int i=0;i<Umbrellas.length;i++)
        {
            UmbrellaValues = Umbrellas[i].split(",");

            //Variabili per inizializzare UmbrellaTmp
            char TypeOfUmbrella = UmbrellaValues[0].charAt(0);
            boolean free = Boolean.parseBoolean(UmbrellaValues[1]);
            String token = null;

            //Se non è libero, esiste un token associato
            if(!free)
                token = UmbrellaValues[2];

            //Creo un ombrellone temporaneo con la riga letta e lo aggiungo all'array
            tmp = new Umbrella(i+1, TypeOfUmbrella, free, token);
            umbrellaArrayList.add(tmp);
        }
        Umbrella[] arrayOfUmbrellas = new Umbrella[umbrellaArrayList.size()];
        umbrellaArrayList.toArray(arrayOfUmbrellas);
        return arrayOfUmbrellas;
    }

    public static void SaveUmbrellaFile(File umbrellaFile, Umbrella[] data, Context AppContext)
    {
        try{
            boolean r = umbrellaFile.createNewFile(); //Crea il file solo se necessario (Ex. la prima volta che si avvia in assoluto)
            Log.i(TAG,"File creato: " + r);
            FileOutputStream fos = AppContext.openFileOutput(umbrellaFile.getName(), Context.MODE_PRIVATE);
            String dataToWrite;
            byte[] byteToWrite;
            for(int i = 0; i<data.length;i++)
            {
                dataToWrite = (data[i].getType() + "," + data[i].isFree());

                if(data[i].getToken() != null)
                    byteToWrite = (dataToWrite + "," + data[i].getToken() + "\n").getBytes(StandardCharsets.UTF_8);
                else
                    byteToWrite = (dataToWrite + "\n").getBytes(StandardCharsets.UTF_8);

                fos.write(byteToWrite);
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG,"File non trovato durante il salvataggio: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Errore durante la scrittura: " + e.getMessage());
        }
    }

    //Per semplicità la prima volta che installiamo l'app generiamo la mappa degli
    //ombrelloni casualmente. Questo metodo non sarebbe applicato anche in un contesto
    //reale, ma andrebbe fatto manualmente basandosi su degli input da parte dell'
    //utilizzatore.

    public static Umbrella[] PopulateRowsFirstTime(File filename, Context appContext)
    {
        Umbrella u;
        ArrayList<Umbrella>uArr = new ArrayList<>();
        String UmbrellaTypes = "ABC";
        for(int i=0; i<TotalUmbrellas; i++)
        {
            Random r = new Random();
            u = new Umbrella(i+1, UmbrellaTypes.charAt(r.nextInt(UmbrellaTypes.length())),
                    true,null);
            uArr.add(u);
        }

        Umbrella[] UmbArray = new Umbrella[uArr.size()];
        uArr.toArray(UmbArray);
        SaveUmbrellaFile(filename, UmbArray, appContext);

        return UmbArray;
    }


    public static String ReadFile(Context AppContext, File filename)
    {
        FileInputStream fis = null;
        try {
            fis = AppContext.openFileInput(filename.getName());
        } catch (IOException e) {
            Log.e(TAG,"Errore nella apertura del file " + e.getMessage());
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            stringBuilder.append(line);
            do{
                line= reader.readLine();
                if(line != null)
                    stringBuilder.append("\n").append(line);
            }while(line != null);
        } catch (IOException e) {
            Log.e(TAG,"Errore nell'apertura del file " + e.getMessage());
        }
        return stringBuilder.toString();
    }

    public static boolean isConnectedToThisServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            Log.e(TAG,"Errore nella connessione : " + e);
            return false;
        }
    }


}
