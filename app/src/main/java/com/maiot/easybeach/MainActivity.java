package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
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
import android.widget.ImageButton;
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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int UMBRELLANUMBER = 12;
    private TextView time = null;
    private Spinner spinner = null;

    private ImageButton UmbrellaButtons[] = null;

    private File umbrellaFile = null;

    private Umbrella[] umbrellas = null;

    private JSONArray FetchedMap = null;
    private JSONArray oldMap = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String filePath = getApplicationContext().getFilesDir().getPath().toString()
                + "/" + getString(R.string.UMBRELLA_FILENAME);

        umbrellaFile = new File(filePath);

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
        UmbrellaButtons = new ImageButton[]{findViewById(R.id.btt1),findViewById(R.id.btt2),
                findViewById(R.id.btt3),findViewById(R.id.btt4),findViewById(R.id.btt5),
                findViewById(R.id.btt6), findViewById(R.id.btt7),findViewById(R.id.btt8),
                findViewById(R.id.btt9),findViewById(R.id.btt10),findViewById(R.id.btt11),
                findViewById(R.id.btt12)};


        //Crea un nuovo thread per aggiornare la mappa ogni minuto
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run() {
                UpdateMap();
            }
        }, 0, 60*1000);


        //Set listeners
        for(int i=0;i<UMBRELLANUMBER;i++)
        {
            UmbrellaButtons[i].setOnClickListener(UmbrellaListener);
        }

    }


    private View.OnClickListener UmbrellaListener = view -> {
        PopUpClass popUpClass = new PopUpClass();
        String id = getResources().getResourceName(view.getId()).replace("com.maiot.easybeach:id/btt","");
        int OmbrellaIndex = Integer.parseInt(id);
        Umbrella u = umbrellas[OmbrellaIndex-1];
        String numeroFila = "Ombrellone numero " + id;
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

    private void UpdateMap()
    {
        try {
            if(Utils.isConnectedToThisServer(Utils.ServerUrl,1000))
            {
                FetchedMap = Utils.TestFetch();;
                Log.i(TAG, "Mappa fetchata!");
                UpdateColors(FetchedMap, UmbrellaButtons);
            }
            else
            {
                Log.i(TAG, "Non sono riuscito a prendere la mappa...");
                this.runOnUiThread(() -> Toast.makeText(this,"Problema di connessione",Toast.LENGTH_LONG).show());
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG,"Errore nel recupero della mappa: " + e.getMessage());
        }
    }

    private void UpdateColors(JSONArray map, ImageButton[] buttons) throws JSONException {
        int index = 0;
        for(int i=0;i<UMBRELLANUMBER;i++)
        {
            if(map.getJSONObject(i).getString("token").equals("null")) {
                buttons[i].setBackgroundResource(R.drawable.umbrellafree);
                umbrellas[i].setFree(true);
            }
            else {
                buttons[i].setBackgroundResource(R.drawable.umbrellaoccupied);
                umbrellas[i].setFree(false);
            }
        }
    }


}