package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;


import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ManageUmbrella extends AppCompatActivity {

    private NfcAdapter adapter = null;
    private final String TAG = "ManageUmbrella";
    private Button bttok = null;
    private TextView tvfree = null;
    private TextView tvinseriscidati = null;
    private EditText etnomecognome = null;
    private DatePicker datePickerPartenza = null;
    private DatePicker datePickerArrivo = null;
    private int rNum = 0;
    private int UmbrellaNumber = 0;

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
        ParseNfcMessage(this.getIntent());

        tvfree = findViewById(R.id.tvfree);
        tvinseriscidati = findViewById(R.id.tvinseriscidati);
        etnomecognome = findViewById(R.id.etnomecognome);

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
        String splittedString[] = msg.split("-");
        //rNum e UmbrellaNumber sono scriti sul tag, quindi 0-00 corrisponde al primo ombrellone
        //della prima fila. Analogalmente, 1-11 corrisponde al dodicesimo ombrellone della seconda
        //fila.

        rNum = Integer.parseInt(splittedString[0]);
        UmbrellaNumber = Integer.parseInt(splittedString[1]);
    }

    void FillView()
    {
        Umbrella u = rows[rNum].UmbrellaAtPosition(UmbrellaNumber);

    }

}