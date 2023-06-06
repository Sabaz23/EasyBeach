package com.maiot.easybeach;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PopUpClass{

    //PopupWindow display method

    public void showPopupWindow(final View view,String NumeroFila, String NomeCognome, String tipo, String DataInizio, String prezzo, MainActivity mainActivity) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler
        TextView tvpopupnome = popupView.findViewById(R.id.tvpopupnome);
        TextView tvpopupnumeroefila = popupView.findViewById(R.id.tvpopupnumeroefila);
        TextView tvpopuptipo = popupView.findViewById(R.id.tvpopuptipo);
        TextView tvdatainizio = popupView.findViewById(R.id.tvdatainizio);
        TextView tvprezzo = popupView.findViewById(R.id.tvPrezzoDaPagare);
        Button bttliberaombrellone = popupView.findViewById(R.id.bttlibera);

        //Set the textviews
        tvpopupnome.setText(NomeCognome);
        tvpopupnumeroefila.setText(NumeroFila);
        tvpopuptipo.setText(tipo);
        tvdatainizio.setText(DataInizio);
        tvprezzo.setText(prezzo);

        if(tvprezzo.getText() == ""){
            bttliberaombrellone.setVisibility(View.GONE);
            bttliberaombrellone.setEnabled(false);
            //bttliberaombrellone.setBackgroundColor(Color.rgb(192, 192, 192));
        }
        else{
            bttliberaombrellone.setVisibility(View.VISIBLE);
            bttliberaombrellone.setEnabled(true);
            //bttliberaombrellone.setBackgroundColor(Color.rgb(0, 0, 255));
        }



        bttliberaombrellone.setOnClickListener(view1 -> {
            Thread thr = new Thread(() -> {
                if(Utils.isConnectedToThisServer(Utils.ServerUrl,Utils.Timeout))
                {
                    Utils.FreeUmbrella(Integer.parseInt(tvpopupnumeroefila.getText().toString().replace("Ombrellone numero ","")));
                }
            });

            thr.start();

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