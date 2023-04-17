package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
    private Row rows[] = null;

    private static final int UMBRELLANUMBER = 12;
    private TextView time = null;
    private Spinner spinner = null;

    private Button UmbrellaButtons[] = null;

    private File umbrellaFile = null;

    private int rowDisplaying = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String filePath = getApplicationContext().getFilesDir().getPath().toString()
                + "/" + getString(R.string.UMBRELLA_FILENAME);

        umbrellaFile = new File(filePath);
        //umbrellaFile.delete();
        Log.i(TAG, "Esiste " + umbrellaFile.exists() + filePath);
        if(umbrellaFile.exists())
        {
            rows = Utils.LoadUmbrellaFile(umbrellaFile,getApplicationContext());
        }
        else {
            rows = Utils.PopulateRowsFirstTime(umbrellaFile, getApplicationContext());
        }


        Log.i(TAG, "Ombrelloni caricati.");

        for(int i=0;i<rows.length;i++)
            rows[i].LogAllRow();

        //Get current Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        String currentDate = "Data corrente: " + date;

        //Set IDs
        spinner = findViewById(R.id.spinnermain);
        time = findViewById(R.id.tvtime);


        //Set Date
        time.setText(currentDate);

        //Set UmbrellaButtons array
        UmbrellaButtons = new Button[]{findViewById(R.id.btt0),findViewById(R.id.btt1),findViewById(R.id.btt2),findViewById(R.id.btt3),findViewById(R.id.btt4),findViewById(R.id.btt5),
                findViewById(R.id.btt6),findViewById(R.id.btt7),findViewById(R.id.btt8),findViewById(R.id.btt9),findViewById(R.id.btt10),findViewById(R.id.btt11)};

        //Set listeners
        for(int i=0;i<UMBRELLANUMBER;i++)
        {
            UmbrellaButtons[i].setOnClickListener(UmbrellaListener);
        }



        //Populate spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.SpinnerMainOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PaintButtons(i);
                rowDisplaying = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "Nulla di selezionato");
            }

        });
    }


    private View.OnClickListener UmbrellaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopUpClass popUpClass = new PopUpClass();
            Button b = (Button)view;
            int OmbrellaIndex = Integer.parseInt(b.getText().toString())-1; //L'ombrellone 1 ha indice 0 ad esempio
            Umbrella u = rows[rowDisplaying].UmbrellaAtPosition(OmbrellaIndex);
            String fila = Integer.toString(rowDisplaying+1);
            String numeroFila = "Ombrellone numero " + b.getText() + "\nFila numero: " + fila;
            String nomeCognome = null;
            String tipo = "Due lettini";
            String sd = null;
            String fd = null;
            if(u.getStartDate() != null) {
                sd = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(u.getStartDate());
                fd = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(u.getFinishDate());
                sd = "Data di Arrivo: " + sd;
                fd = "Data di Partenza: " + fd;
            }else
            {
                sd = "";
                fd = "";
            }


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
                nomeCognome = "Questo ombrellone è attualmente libero.";
            else
                nomeCognome = "Nome cliente: " + u.getClientName();
            popUpClass.showPopupWindow(view,numeroFila,nomeCognome,tipo,sd,fd);
        }
    };


    private void PaintButtons(int row)
    {
        for(int i=0; i<UmbrellaButtons.length; i++)
        {
            if(rows[row].UmbrellaAtPosition(i).isFree())
                UmbrellaButtons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            else
                UmbrellaButtons[i].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        }
    }



}