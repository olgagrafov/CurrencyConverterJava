package com.olgag.currencyconverter.myServices;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by olgag on 10/11/2017.
 */

public class CurrencyServicr extends IntentService {
    public static final String CURRENCY_SERVICE="com.example.olgag.currencyconverter.services.CURRENCY_SERVICE";

    public CurrencyServicr() { super(CurrencyServicr.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String putCurrency=intent.getStringExtra("putCurrency");
        double money=intent.getDoubleExtra("putMoney",0);


        String urlString ="https://free.currencyconverterapi.com/api/v5/convert?apiKey=1840888f7750147fbe8a&compact=y&q=" +putCurrency;
                //"https://free.currencyconverterapi.com/api/v5/convert?apiKey=1840888f7750147fbe8a&compact=y&q=USD_PHP" ;


        HttpURLConnection connection = null;
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();
        URL url = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }

            double rate=0;
            double value=0;


           if(!builder.toString().isEmpty()) {
               JSONObject root= new JSONObject(builder.toString());
               JSONObject results = root.getJSONObject(putCurrency);
               rate=Double.parseDouble(results.getString("val"));
               value=money*rate;
              }

            Intent broadIntent = new Intent(CURRENCY_SERVICE);
            broadIntent.putExtra("value", value);
            broadIntent.putExtra("rate", rate);

            LocalBroadcastManager.getInstance(this).sendBroadcast(broadIntent);


       } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
}
