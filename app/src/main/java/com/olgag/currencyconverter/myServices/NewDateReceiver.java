package com.olgag.currencyconverter.myServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by olgag on 27/01/2018.
 */

public class NewDateReceiver extends BroadcastReceiver {
    private Context cont;
    private TextView txtResult, txtRate;

    public NewDateReceiver(Context cont) {
        this.cont = cont;
    }

    public NewDateReceiver(Context cont, TextView txtResult, TextView txtRate) {
        this.cont = cont;
        this.txtResult = txtResult;
        this.txtRate = txtRate;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        txtRate.setVisibility(View.INVISIBLE);
        txtResult.setVisibility(View.INVISIBLE);
        double result = intent.getDoubleExtra("value", 0);
        String newDate = intent.getStringExtra("putDate");
        String newYear = newDate.substring(0, 4);
        String newDay = newDate.substring(8, 10);
        String newMonth = newDate.substring(5, 7);
        newDate = newDay + "/" + newMonth + "/" + newYear;
        String first ="This rate was correct on " + newDate ;
        String next = " <BR><B><U>" +intent.getDoubleExtra("rate", 0) + "</U></B>";
        txtRate.setText(Html.fromHtml(first + next));
        txtResult.setText( ((double)Math.round(result * 100) / 100) + "" );
        blink();
    }

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 800;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtRate.setVisibility(View.VISIBLE);
                        txtResult.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }
}