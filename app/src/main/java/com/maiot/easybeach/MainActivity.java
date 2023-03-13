package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int UMBRELLANUMBER = 12;
    private TextView time = null;
    private Spinner spinner = null;

    private Button UmbrellaButtons[] = null;
    private int UmbrellaButtonsID[] = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get current Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        //Set IDs
        spinner = findViewById(R.id.spinnermain);
        time = findViewById(R.id.tvtime);

        //Set UmbrellaButtons array
        UmbrellaButtons = new Button[]{findViewById(R.id.btt0),findViewById(R.id.btt1),findViewById(R.id.btt2),findViewById(R.id.btt3),findViewById(R.id.btt4),findViewById(R.id.btt5),
                findViewById(R.id.btt6),findViewById(R.id.btt7),findViewById(R.id.btt8),findViewById(R.id.btt9),findViewById(R.id.btt10),findViewById(R.id.btt11)};

        //Set listeners
        for(int i=0;i<UMBRELLANUMBER;i++)
        {
            UmbrellaButtons[i].setOnClickListener(UmbrellaListener);
        }

        //Set Date
        time.setText("Data corrente: " + date.toString());

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
                case R.id.btt0:
                    Log.i(TAG,"Hai Cliccato su 1");
                    UmbrellaButtons[0].setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
            }
        }
    };
}