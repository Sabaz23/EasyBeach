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

public class ManageUmbrella extends AppCompatActivity {

    private NfcAdapter adapter = null;
    private final String TAG = "ManageUmbrella";
    private Button bttok = null;
    private TextView tvfree = null;
    private TextView tvinseriscidati = null;
    private EditText etnomecognome = null;
    private DatePicker datePickerPartenza = null;
    private DatePicker datePickerArrivo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_umbrella);
        ParseNfcMessage(this.getIntent());
        Log.i(TAG, "onCreate");
    }


    void ParseNfcMessage(Intent intent)
    {
        Parcelable[] NdefMessageArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage ndefMessage = (NdefMessage) NdefMessageArray[0];
        String msg = new String(ndefMessage.getRecords()[0].getPayload());
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();

    }
}