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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Row rows[] = null;

    private static final int UMBRELLANUMBER = 12;
    private TextView time = null;
    private Spinner spinner = null;

    private Button UmbrellaButtons[] = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Populate Rows (TODO OnCreate Load)
        Umbrella[] tmp = new Umbrella[]{new Umbrella(),new Umbrella(),new Umbrella(),new Umbrella()
                ,new Umbrella(),new Umbrella(),new Umbrella(),new Umbrella(),new Umbrella(),new Umbrella()
                ,new Umbrella(),new Umbrella()};
        rows = new Row[]{new Row(tmp,0),new Row(tmp,1),new Row(tmp,2),new Row(tmp,3)};

        //Get current Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        String currentDate = "Data corrente: " + date.toString();

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
                Log.i(TAG, "Hai cliccato su " + adapterView.getItemAtPosition(i).toString());
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
            switch (view.getId()) {
                //TODO GET CURRENT ROW CLICKED
                case R.id.btt0:
                    Log.i(TAG,"Hai Cliccato su 1");
                    if(rows[0].UmbrellaAtPosition(0).isFree()) {
                        UmbrellaButtons[0].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                        rows[0].UmbrellaAtPosition(0).setFree(false);
                    }
                    else
                    {
                        UmbrellaButtons[0].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
                        rows[0].UmbrellaAtPosition(0).setFree(true);
                    }

            }
        }
    };


}