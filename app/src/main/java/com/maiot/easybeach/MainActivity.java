package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.Clock;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    //Tag per log
    private static final String TAG = "MainActivity";
    //Variabile final per il numero di ombrelloni della spiaggia
    private static final int UMBRELLANUMBER = 12;

    //TextView//
    private TextView time = null;
    private TextView updateTime = null;

    //Buttons//
    private Button bttAggiorna = null;
    private ImageButton[] UmbrellaButtons = null;

    //File contenente dati degli ombrelloni//
    private File umbrellaFile = null;

    //Array di Ombrelloni//
    private Umbrella[] umbrellas = null;

    //JsonArray contenente la mappa recuperata dal database//
    private JSONArray FetchedMap = null;

    //Timer Per aggiornare la mappa ogni 5 minuti//
    Timer UpdateMapTimer = new Timer();

    //Variabile con observer per capire quando aggiornare la mappa dopo aver liberato
    //un'ombrellone dal popup
    public MutableLiveData<Boolean> IsMapToUpdateFromPopup = new MutableLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String filePath = getApplicationContext().getFilesDir().getPath().toString()
                + "/" + getString(R.string.UMBRELLA_FILENAME);

        umbrellaFile = new File(filePath);

        //Per semplicità la prima volta che installiamo l'app generiamo la mappa degli
        //ombrelloni casualmente. "GenerateUmbrellaFirstTime" non sarebbe applicato anche in un contesto
        //reale, ma andrebbe fatto manualmente basandosi su degli input da parte dell'
        //utilizzatore.
        if(umbrellaFile.exists())
            umbrellas = Utils.LoadUmbrellaFile(umbrellaFile,getApplicationContext());
        else
            umbrellas = Utils.GenerateUmbrellaFirstTime(umbrellaFile, getApplicationContext());

        Log.i(TAG, "Ombrelloni caricati.");

        //ID//
        time = findViewById(R.id.tvtime);
        updateTime = findViewById(R.id.tvultimoaggiornamento);
        bttAggiorna = findViewById(R.id.bttaggiorna);


        //Creiamo un orologio per visualizzare l'ora corrente//
        Timer ClockTimer =new Timer();
        ClockTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    Calendar c = Calendar.getInstance();
                    String date = Utils.sdfDisplay.format(c.getTime());
                    String currentDate = "Orario attuale: " + date;
                    time.setText(currentDate);
                });
            }
        },0,1000);

        //Impostiamo l'array di bottoni di ombrelloni//
        UmbrellaButtons = new ImageButton[]{findViewById(R.id.btt1),findViewById(R.id.btt2),
                findViewById(R.id.btt3),findViewById(R.id.btt4),findViewById(R.id.btt5),
                findViewById(R.id.btt6), findViewById(R.id.btt7),findViewById(R.id.btt8),
                findViewById(R.id.btt9),findViewById(R.id.btt10),findViewById(R.id.btt11),
                findViewById(R.id.btt12)};


        //Crea un nuovo timer per aggiornare la mappa ogni 5 minuti//
        UpdateMapTimer = new Timer();
        UpdateMapTimer.schedule(new TimerTask()
        {
            public void run() {
                UpdateMap();
            }
        }, 0, 300*1000);


        //Listener//
        for(int i=0;i<UMBRELLANUMBER;i++)
        {
            UmbrellaButtons[i].setOnClickListener(UmbrellaListener);
        }
        bttAggiorna.setOnClickListener(UpdateListener);

        //IsMapToUpdateFromPopup viene impostata a false la prima volta, ma in realtà è indifferente.
        //Nella classe PopUpClass, se il bottone per liberare un ombrellone viene premuto, viene
        //impostata la negazione del valore che si trova in quel momento all'interno della variabile. Quindi
        //se true diventa false e se false diventa true. Non hanno un significato logico ma servono
        //solo a "triggerare" l'observer per aggiornare la mappa.
        IsMapToUpdateFromPopup.setValue(false);
        IsMapToUpdateFromPopup.observe(this, aBoolean -> {
            Thread thr = new Thread(this::UpdateMap);
            thr.start();
        });


    }

    //Listener del bottone per "Aggiorna Mappa"//
    private View.OnClickListener UpdateListener = view ->
    {
        Thread thr = new Thread(this::UpdateMap);
        thr.start();

    };
    //Listener di ogni bottone//
    private View.OnClickListener UmbrellaListener = view -> {
        PopUpClass popUpClass = new PopUpClass();
        //Siccome sono chiamati tutti alla stessa maniera,
        //Per tenere l'id rimuoviamo "com.maiot.easybeach:id/btt" che è presente in ogni bottone
        String id = getResources().getResourceName(view.getId()).replace("com.maiot.easybeach:id/btt", "");
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

        String finalHeader = header;
        String finalTipo = tipo;
        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                if(Utils.isConnectedToThisServer(Utils.ServerUrl,Utils.Timeout))
                {
                    String dataInizioString = null;
                    String PrezzoDaPagare = null;
                    Calendar dataInizio = Calendar.getInstance();
                    try {
                        dataInizio = Utils.GetDataInizio(OmbrellaIndex);
                        if(dataInizio == null)
                        {
                            dataInizioString = "";
                            PrezzoDaPagare = "";
                        }else {
                            dataInizioString = "Ora inizio prenotazione: " + Utils.sdfDisplay.format(dataInizio.getTime());
                            PrezzoDaPagare = "Prezzo da pagare: " + Utils.getPrezzoDaPagare(dataInizio) + "€";
                        }
                    } catch (ParseException e) {
                        Log.e(TAG,"Problema nel parsing " + e.getMessage());
                    }
                    String finalDataInizioString = dataInizioString;
                    String finalPrezzoDaPagare = PrezzoDaPagare;
                    //showPopupWindow crea il popup sopra la mainactivity
                    runOnUiThread(() -> popUpClass.showPopupWindow(view,numeroFila, finalHeader,
                            finalTipo, finalDataInizioString, finalPrezzoDaPagare, MainActivity.this));
                }
                else
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Problema di connessione", Toast.LENGTH_LONG).show());
            }
        });
        thr.start();

    };

    @Override
    protected void onPause() {
        super.onPause();
        //Quando l'activity va in pausa non è necessario aggiornare
        //la mappa perchè non la vediamo in ogni caso
        UpdateMapTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Quando riprendiamo l'activity, è necessario ricreare un nuovo timer perchè quello vecchio
        //e' andato distrutto nell'onPause
        UpdateMapTimer = new Timer();
        UpdateMapTimer.schedule(new TimerTask()
        {
            public void run() {
                UpdateMap();
            }
        }, 0, 300 *1000);
    }

    //UpdateMap si occupa di recuperare dal database la mappa e di aggiornare
    //le icone dei bottoni basandosi sui valori presenti all'interno del JSONArray
    //ritornato dal server.
    public void UpdateMap()
    {
        try {
            if(Utils.isConnectedToThisServer(Utils.ServerUrl,Utils.Timeout))
            {
                FetchedMap = Utils.FetchMap();
                Log.i(TAG, "Mappa fetchata!");
                UpdateColors(FetchedMap, UmbrellaButtons);
                this.runOnUiThread(() -> {
                    Calendar c = Calendar.getInstance();
                    String date = Utils.sdfDisplay.format(c.getTime());
                    String currentDate = "Ultimo aggiornamento mappa: " + date;
                    updateTime.setText(currentDate);
                });
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
                int finalI1 = i;
                runOnUiThread((Runnable) () -> buttons[finalI1].setImageResource(R.drawable.umbrellafree));
                umbrellas[i].setFree(true);
            }
            else {
                int finalI = i;
                runOnUiThread((Runnable) () -> buttons[finalI].setImageResource(R.drawable.umbrellaoccupied));
                umbrellas[i].setFree(false);
            }
        }
    }


}