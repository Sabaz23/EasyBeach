package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;


import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;

public class ManageUmbrella extends AppCompatActivity {

    private NfcAdapter adapter = null;
    private final String TAG = "ManageUmbrella";
    private Button bttok = null;
    private TextView tvfree = null;
    private TextView tvinseriscidati = null;
    private EditText etnomecognome = null;
    private DatePicker datePickerStart = null;
    private DatePicker datePickerFinish = null;
    private Button btnConfirm = null;
    private int rNum = 0;
    private int UmbrellaNumber = 0;

    private Umbrella u = null;
    private Row[] rows = null;

    File umbrellaFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_umbrella);
        String filePath = getApplicationContext().getFilesDir().getPath().toString()
                + "/" + getString(R.string.UMBRELLA_FILENAME);
        umbrellaFile = new File(filePath);

        if(umbrellaFile.exists())
            rows = Utils.LoadUmbrellaFile(umbrellaFile,getApplicationContext());
        else
            rows = Utils.PopulateRowsFirstTime(umbrellaFile, getApplicationContext());


        tvfree = findViewById(R.id.tvfree);
        tvinseriscidati = findViewById(R.id.tvinseriscidati);
        etnomecognome = findViewById(R.id.etnomecognome);
        btnConfirm = findViewById(R.id.bttok);
        datePickerStart = findViewById(R.id.datePickerStart);
        datePickerFinish = findViewById(R.id.datePickerFinish);

        btnConfirm.setOnClickListener(BttListener);
        btnConfirm.setOnLongClickListener(bttLongClick);

        ParseNfcMessage(this.getIntent());
        FillView();
    }


    void ParseNfcMessage(Intent intent)
    {
        Parcelable[] NdefMessageArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage ndefMessage = (NdefMessage) NdefMessageArray[0];
        String msg = new String(ndefMessage.getRecords()[0].getPayload());
        ParsePayload(msg);
    }

    void ParsePayload(String msg)
    {
        String[] splittedString = msg.split("-");

        //rNum e UmbrellaNumber sono scriti sul tag, quindi 0-00 corrisponde al primo ombrellone
        //della prima fila. Analogalmente, 1-11 corrisponde al dodicesimo ombrellone della seconda
        //fila.

        rNum = Integer.parseInt(splittedString[0]);
        UmbrellaNumber = Integer.parseInt(splittedString[1]);
    }

    void FillView()
    {
        u = rows[rNum].UmbrellaAtPosition(UmbrellaNumber);

        //Coloriamo il testo in base alla disponibilità dell'ombrellone
        if(u.isFree()) {
            tvfree.setText(R.string.UMBRELLA_FREE);
            tvfree.setTextColor(Color.GREEN);

            tvinseriscidati.setText(R.string.HEADER_FREE);

            btnConfirm.setText(R.string.BUTTON_FREE);

            Calendar c = Calendar.getInstance();
            datePickerStart.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            datePickerStart.setMinDate(c.getTimeInMillis());
            datePickerFinish.setMinDate(c.getTimeInMillis());

        }
        else {

            tvfree.setText(R.string.UMBRELLA_OCCUPIED);
            tvfree.setTextColor(Color.RED);

            tvinseriscidati.setText(R.string.HEADER_OCCUPIED);
            etnomecognome.setText(u.getClientName());

            btnConfirm.setText(R.string.BUTTON_OCCUPIED);
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(u.getStartDate());
            c2.setTime(u.getFinishDate());

            datePickerStart.updateDate(c1.get(Calendar.YEAR),c1.get(Calendar.MONTH),c1.get(Calendar.DAY_OF_MONTH));
            datePickerFinish.updateDate(c2.get(Calendar.YEAR),c2.get(Calendar.MONTH),c2.get(Calendar.DAY_OF_MONTH));



        }

    }

    private View.OnClickListener BttListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Button b = (Button)view;
            String bttText = b.getText().toString();
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();

            c1.set(datePickerStart.getYear(),datePickerStart.getMonth(),datePickerStart.getDayOfMonth());
            c2.set(datePickerFinish.getYear(),datePickerFinish.getMonth(), datePickerFinish.getDayOfMonth());
            u.setStartDate(c1.getTime());
            u.setFinishDate(c2.getTime());

            if(etnomecognome.getText().toString().equals(""))
                Toast.makeText(getApplicationContext(), "Inserisci nome e cognome", Toast.LENGTH_LONG).show();
            else if(c2.before(c1))
            {
                Toast.makeText(getApplicationContext(), "La data di partenza non può essere prima di quella di arrivo", Toast.LENGTH_LONG).show();
            }
            else
            {

                if(getString(R.string.BUTTON_FREE).equals(bttText)) u.setFree(false);

                u.setClientName(etnomecognome.getText().toString());
                rows[rNum].UmbrellaAtPosition(UmbrellaNumber).UpdateUmbrella(u);
                Utils.SaveUmbrellaFile(umbrellaFile, rows, getApplicationContext());
                Toast.makeText(getApplicationContext(),"Ombrellone salvato con successo!", Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(ManageUmbrella.this, MainActivity.class);
                ManageUmbrella.this.startActivity(myIntent);
            }

        }
    };

    private View.OnLongClickListener bttLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            Button b = (Button)view;
            //Se il testo è = a quello del bottone occupato (Quindi l'ombrellone è occupato)
            if(b.getText().toString().equals(getString(R.string.BUTTON_OCCUPIED)))
            {
                rows[rNum].UmbrellaAtPosition(UmbrellaNumber).setUmbrellaFree();
                Utils.SaveUmbrellaFile(umbrellaFile, rows, getApplicationContext());
                Toast.makeText(getApplicationContext(), "Ombrellone liberato!", Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(ManageUmbrella.this, MainActivity.class);
                ManageUmbrella.this.startActivity(myIntent);
            }
            return true;
        }
    };

}