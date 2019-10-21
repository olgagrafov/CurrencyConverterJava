package com.olgag.currencyconverter.myServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;



/**
 * Created by olgag on 11/11/2017.
 */

public class CurrencyReceiver extends BroadcastReceiver {
    private Context cont;
    private TextView txtResult,txtRate;


    public CurrencyReceiver(Context cont) {
        this.cont = cont;
    }


    public CurrencyReceiver(Context cont, TextView txtResult, TextView txtRate) {
        this.cont = cont;
        this.txtResult = txtResult;
        this.txtRate = txtRate;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        double result=intent.getDoubleExtra("value",0);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String first ="The rate is correct at " + df.format("dd/MM/yyyy hh:mm", new java.util.Date()) ;
        String next = " <BR><B><U>" +intent.getDoubleExtra("rate",0) + "</U></B>";
        txtRate.setText(Html.fromHtml(first + next));
        txtResult.setText( ((double)Math.round(result * 100) / 100) + "" );
    }
}
