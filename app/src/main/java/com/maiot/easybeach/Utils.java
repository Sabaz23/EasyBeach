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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils {
    private static final String TAG = "Utils";
    private static final int TotalUmbrellas = 12;
    private static final float PrezzoAlMinuto = 0.07f;
    public static final SimpleDateFormat sdfDisplay = new SimpleDateFormat("HH:mm:ss", Locale.ITALIAN);
    public static final int Timeout = 5000;
    public final static String ServerUrl = "http://localhost/";
    private final static String FetchMapUrl = ServerUrl + "umbrellapp/fetch_map.php";


    private final static String GetAllUrl = ServerUrl + "umbrellapp/getMyUmbrellas.php";
    private final static String FreeUmbrellaUrl = ServerUrl + "umbrellapp/elaborateReservation.php";

    //Questo metodo ritorna il prezzo da pagare con una precisione di 2 dopo la virgola.
    public static float getPrezzoDaPagare(Calendar sd)
    {
        Date sdDate = sd.getTime();
        Date fdDate = Calendar.getInstance().getTime();
        long diffInSecs = (fdDate.getTime()-sdDate.getTime())/1000;
        long diff = TimeUnit.MINUTES.convert(diffInSecs,TimeUnit.SECONDS);
        float prezzo = diff * PrezzoAlMinuto;
        BigDecimal bd = new BigDecimal(Double.toString(prezzo));
        bd = bd.setScale(2, RoundingMode.HALF_DOWN);
        return bd.floatValue();
    }

    //Questo metodo invia una richiesta HTTP al server per recuperare la mappa
    public static JSONArray FetchMap() throws  IOException{
            final OkHttpClient client = new OkHttpClient();

            //Crea la richiesta e prova a prenderne il body (il file php ritorna un file json)
            Request request = new Request.Builder().url(FetchMapUrl).build();
            try (Response response = client.newCall(request).execute()) {
                return new JSONArray(response.body().string());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
    }

    //Questo metodo carica gli ombrelloni da umbrellaFile
    public static Umbrella[] LoadUmbrellaFile(File umbrellaFile, Context AppContext)
    {
        String contents = ReadFile(AppContext, umbrellaFile);
        ArrayList<Umbrella> umbrellaArrayList = new ArrayList<>();
        String[] Umbrellas = contents.split("\n");
        String[] UmbrellaValues;
        Umbrella tmp;
        for(int i=0;i<Umbrellas.length;i++)
        {
            UmbrellaValues = Umbrellas[i].split(",");

            //Variabili per inizializzare UmbrellaTmp
            char TypeOfUmbrella = UmbrellaValues[0].charAt(0);
            boolean free = Boolean.parseBoolean(UmbrellaValues[1]);

            //Creo un ombrellone temporaneo con la riga letta e lo aggiungo all'array
            tmp = new Umbrella(i+1, TypeOfUmbrella, free);
            umbrellaArrayList.add(tmp);
        }
        Umbrella[] arrayOfUmbrellas = new Umbrella[umbrellaArrayList.size()];
        umbrellaArrayList.toArray(arrayOfUmbrellas);
        return arrayOfUmbrellas;
    }

    //Questo metodo salva gli ombrelloni dall'array al file.
    public static void SaveUmbrellaFile(File umbrellaFile, Umbrella[] data, Context AppContext)
    {
        try{
            boolean r = umbrellaFile.createNewFile(); //Crea il file solo se necessario (Ex. la prima volta che si avvia in assoluto)
            Log.i(TAG,"File creato: " + r);
            FileOutputStream fos = AppContext.openFileOutput(umbrellaFile.getName(), Context.MODE_PRIVATE);
            String dataToWrite;
            byte[] byteToWrite;
            for (Umbrella datum : data) {
                dataToWrite = (datum.getType() + "," + datum.isFree());
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


    //Questo metodo genera casualmente gli ombrelloni
    public static Umbrella[] GenerateUmbrellaFirstTime(File filename, Context appContext)
    {
        Umbrella u;
        ArrayList<Umbrella>uArr = new ArrayList<>();
        String UmbrellaTypes = "ABC";
        for(int i=0; i<TotalUmbrellas; i++)
        {
            Random r = new Random();
            u = new Umbrella(i+1, UmbrellaTypes.charAt(r.nextInt(UmbrellaTypes.length())),
                    true);
            uArr.add(u);
        }

        Umbrella[] UmbArray = new Umbrella[uArr.size()];
        uArr.toArray(UmbArray);
        //Dopo averli generati, salva il file
        SaveUmbrellaFile(filename, UmbArray, appContext);

        return UmbArray;
    }

    //Metodo per semplificare la lettura
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

    //Questo metodo verifica di essere connessi al server che viene passato come argomento
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

    //Questo metodo ritorna tutti gli ombrelloni come array di stringhe.
    private static String[] GetAllRequest()
    {
        final OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("token", "admin")
                .build();

        Request request = new Request.Builder()
                .url(GetAllUrl)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string().split(";");
        } catch (IOException e) {
            return new String[]{};
        }
    }
    //Questo metodo libera un ombrellone passato per parametro sul server.
    public static boolean FreeUmbrella(int uid)
    {
        final OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("uid", Integer.toString(uid))
                .add("token", "null")
                .add("inizioprenotazione", "null")
                .build();

        Request request = new Request.Builder()
                .url(FreeUmbrellaUrl)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    //Questo metodo recupera e parsa la data di inizio di un dato ombrellone.
    public static Calendar GetDataInizio(int uid) throws ParseException {
        String[] allUmbrella = GetAllRequest();
        for (String s : allUmbrella) {
            String[] tmp = s.split(" ");
            if (tmp[0].equals(Integer.toString(uid))) {
                if (tmp.length != 1) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdfDisplay.parse(tmp[1]));
                    c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                    c.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
                    c.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    return c;
                }
            }
        }
        return null;
    }


}
