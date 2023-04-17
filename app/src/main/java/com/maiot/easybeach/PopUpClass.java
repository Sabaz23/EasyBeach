package com.maiot.easybeach;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PopUpClass {

    //PopupWindow display method

    public void showPopupWindow(final View view,String NumeroFila, String NomeCognome, String tipo, String sd, String fd) {


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
        TextView tvpopuppartenza = popupView.findViewById(R.id.tvpopuppartenza);
        TextView tvpopuparrivo = popupView.findViewById(R.id.tvpopuparrivo);
        TextView tvpopuptipo = popupView.findViewById(R.id.tvpopuptipo);

        //Set the textviews
        tvpopupnome.setText(NomeCognome);
        tvpopupnumeroefila.setText(NumeroFila);
        tvpopuptipo.setText(tipo);
        tvpopuppartenza.setText(fd);
        tvpopuparrivo.setText(sd);

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

}