package com.maiot.easybeach;

import androidx.appcompat.app.AppCompatActivity;


import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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
        Log.i(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        final String action = intent.getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {
            Parcelable nfctag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(nfctag != null)
            {
                Log.i(TAG,nfctag.toString());
            }
        }

    }


}