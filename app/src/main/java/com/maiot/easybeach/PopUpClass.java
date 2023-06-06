package com.maiot.easybeach;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopUpClass{

    public void showPopupWindow(final View view,String NumeroFila, String NomeCognome, String tipo, String DataInizio, String prezzo, MainActivity mainActivity) {
        //Crea il view object attraverso l'inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        //Specifica la lunghezza e la larghezza
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Elementi del popup//
        TextView tvpopupnome = popupView.findViewById(R.id.tvpopupnome);
        TextView tvpopupnumeroefila = popupView.findViewById(R.id.tvpopupnumeroefila);
        TextView tvpopuptipo = popupView.findViewById(R.id.tvpopuptipo);
        TextView tvdatainizio = popupView.findViewById(R.id.tvdatainizio);
        TextView tvprezzo = popupView.findViewById(R.id.tvPrezzoDaPagare);
        Button bttliberaombrellone = popupView.findViewById(R.id.bttlibera);

        //Impostiamo i testi//
        tvpopupnome.setText(NomeCognome);
        tvpopupnumeroefila.setText(NumeroFila);
        tvpopuptipo.setText(tipo);
        tvdatainizio.setText(DataInizio);
        tvprezzo.setText(prezzo);

        //Impostiamo il bottone visibile o meno in base al testo del prezzo
        //(se non c'è, non è occupato l'ombrellone)
        if(tvprezzo.getText() == ""){
            bttliberaombrellone.setVisibility(View.GONE);
            bttliberaombrellone.setEnabled(false);
        }
        else{
            bttliberaombrellone.setVisibility(View.VISIBLE);
            bttliberaombrellone.setEnabled(true);
        }


        //Listener del bottone
        bttliberaombrellone.setOnClickListener(view1 -> {
            Thread thr = new Thread(() -> {
                if(Utils.isConnectedToThisServer(Utils.ServerUrl,Utils.Timeout))
                {
                    Utils.FreeUmbrella(Integer.parseInt(tvpopupnumeroefila.getText().toString().replace("Ombrellone numero ","")));
                }
            });

            thr.start();
            try {
                //Il join è necessario perchè altrimenti il thread che libera l'ombrellone
                //potrebbe terminare dopo che IsMapToUpdateFromPopup cambia valore, e in quel caso
                //la mappa non viene aggiornata in maniera corretta.
                thr.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //Chiudiamo il popup e facciamo aggiornare la mappa alla main activity
            popupWindow.dismiss();
            mainActivity.IsMapToUpdateFromPopup.postValue(!mainActivity.IsMapToUpdateFromPopup.getValue());

        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                popupWindow.dismiss();
                return true;
            }
        });
    }


}