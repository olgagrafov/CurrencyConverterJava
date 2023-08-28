package com.olgag.currencyconverter.myServices;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.olgag.currencyconverter.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by olgag on 27/01/2018.
 */

public class NewDateService extends IntentService {
    public static final String NEWDATE_SERVICE="com.example.olgag.currencyconverter.services.NEWDATE_SERVICE";

    public NewDateService() {
        super(NewDateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String putCurrency=intent.getStringExtra("putCurrency");
        String putDate=intent.getStringExtra("putDate");
        double money=intent.getDoubleExtra("putMoney",0);
       // String urlString ="https://free.currencyconverterapi.com/api/v5/convert?q=RUB_USD&compact=ultra&date=2017-12-31";
        String urlString ="https://free.currencyconverterapi.com/api/v5/convert?apiKey="+getResources().getString(R.string.api_key)+"&compact=ultra&q="+putCurrency+"&date="+putDate;

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
            double value=0;
            double rate=0;

            if (!builder.toString().isEmpty()) {
                JSONObject root = new JSONObject(builder.toString());;
                JSONObject results = root.getJSONObject(putCurrency);
                String jsRate = results.getString(putDate);
                rate=Double.parseDouble(jsRate);
                value=money*rate;
            }

            Intent broadIntent = new Intent(NEWDATE_SERVICE);
            broadIntent.putExtra("value", value);
            broadIntent.putExtra("rate", rate);
            broadIntent.putExtra("putDate", putDate);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadIntent);


        }
        catch (MalformedURLException e) {
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

