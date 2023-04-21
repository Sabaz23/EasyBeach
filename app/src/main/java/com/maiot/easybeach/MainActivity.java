package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int UMBRELLANUMBER = 12;
    private TextView time = null;
    private Spinner spinner = null;

    private Button UmbrellaButtons[] = null;

    private File umbrellaFile = null;
    private File mapFile = null;

    private Umbrella[] umbrellas = null;

    private JSONArray FetchedMap = null;
    private JSONArray oldMap = null;

    private int rowDisplaying = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!haveNetworkConnection())
            Toast.makeText(getApplicationContext(), "La mappa potrebbe non essere aggiornata senza internet.", Toast.LENGTH_LONG).show();

        String filePath = getApplicationContext().getFilesDir().getPath().toString()
                + "/" + getString(R.string.UMBRELLA_FILENAME);

        String mapPath = getApplicationContext().getFilesDir().getPath().toString()
                + "/" + getString(R.string.MAP_FILENAME);

        umbrellaFile = new File(filePath);
        mapFile = new File(mapPath);


        Thread FetchUmbrellas = new Thread(this::CheckAndUpdateMap);




        umbrellaFile.delete();
        Log.i(TAG, "Esiste " + umbrellaFile.exists() + filePath);
        if(umbrellaFile.exists())
            umbrellas = Utils.LoadUmbrellaFile(umbrellaFile,getApplicationContext());
        else
            umbrellas = Utils.PopulateRowsFirstTime(umbrellaFile, getApplicationContext());

        Log.i(TAG, "Ombrelloni caricati.");


        //Get current Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        String currentDate = "Data corrente: " + date;

        //Set ID
        time = findViewById(R.id.tvtime);

        //Set Date
        time.setText(currentDate);

        //Set UmbrellaButtons array
        UmbrellaButtons = new Button[]{findViewById(R.id.btt0),findViewById(R.id.btt1),
                findViewById(R.id.btt2),findViewById(R.id.btt3),findViewById(R.id.btt4),
                findViewById(R.id.btt5), findViewById(R.id.btt6),findViewById(R.id.btt7),
                findViewById(R.id.btt8),findViewById(R.id.btt9),findViewById(R.id.btt10),
                findViewById(R.id.btt11)};

        //Start Thread to fetch umbrella map and update colors
        FetchUmbrellas.start();

        //Create Handler to check the map every 5 seconds
        Handler handler = new Handler();
        int delay = 5000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {CheckAndUpdateMap();}
                }).start();
            }
        }, delay);

        //Set listeners
        for(int i=0;i<UMBRELLANUMBER;i++)
        {
            UmbrellaButtons[i].setOnClickListener(UmbrellaListener);
        }

    }


    private View.OnClickListener UmbrellaListener = view -> {
        PopUpClass popUpClass = new PopUpClass();
        Button b = (Button)view;
        int OmbrellaIndex = Integer.parseInt(b.getText().toString());
        Umbrella u = umbrellas[OmbrellaIndex-1];
        String numeroFila = "Ombrellone numero " + b.getText();
        String header = null;
        String tipo = "Due lettini";

        switch(u.getType())
        {
            case 'A':
                tipo = "Due lettini";
                break;
            case 'B':
                tipo = "Lettino e sdraio";
                break;
            case 'C':
                tipo = "Due sdraio";
                break;
        }

        if(u.isFree())
            header = "Questo ombrellone è attualmente libero.";
        else
            header = "Questo ombrellone è attualmente occupato.";
        popUpClass.showPopupWindow(view,numeroFila,header,tipo);
    };

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void PaintButtons()
    {
        for(int i=0; i<UmbrellaButtons.length; i++)
        {
            if(umbrellas[i].isFree())
                UmbrellaButtons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            else
                UmbrellaButtons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        }
    }


    private void CheckAndUpdateMap()
    {
        try {
            FetchedMap = Utils.TestFetch();
            Log.i(TAG, "Controllo la mappa da remoto...");
            if(mapFile.exists()) {
                Log.i(TAG, "La mappa esiste...");
                oldMap = new JSONArray(Utils.ReadFile(getApplicationContext(), mapFile));
            }
            else {
                Log.i(TAG, "La mappa non esiste...");
                oldMap = FetchedMap;
                SaveNewMap();
            }

            if(!FetchedMap.equals(oldMap))
            {
                Log.i(TAG, "La mappa non è uguale...aggiorno");
                SaveNewMap();
                UpdateColors(FetchedMap, UmbrellaButtons);
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void UpdateColors(JSONArray map, Button[] buttons) throws JSONException {
        int index = 0;
        for(int i=0;i<UMBRELLANUMBER;i++)
        {
            if(map.getJSONObject(i) != null)
                buttons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            else
                buttons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        }
    }

    private void SaveNewMap() throws IOException, JSONException {
        FileOutputStream fos = getApplicationContext().openFileOutput(mapFile.getName(), Context.MODE_PRIVATE);
        byte[] byteToWrite;
        byteToWrite = "[".getBytes(StandardCharsets.UTF_8);
        for(int i=0;i<FetchedMap.length();i++)
        {
            if(i != FetchedMap.length()-1) byteToWrite = (byteToWrite + FetchedMap.getString(i) + ",").getBytes(StandardCharsets.UTF_8);
        }
        byteToWrite = (byteToWrite + FetchedMap.getString(FetchedMap.length()-1) + "]").getBytes(StandardCharsets.UTF_8);
        fos.write(byteToWrite);
        Log.i(TAG, "Ho scritto sul file " + Utils.ReadFile(getApplicationContext(),mapFile));
    }




}