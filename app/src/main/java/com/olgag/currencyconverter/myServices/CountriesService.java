package com.olgag.currencyconverter.myServices;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.olgag.currencyconverter.R;
import com.olgag.currencyconverter.db.DBCoinNameHelper;
import com.olgag.currencyconverter.db.DbProvider;
import com.olgag.currencyconverter.model.Coin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by olgag on 14/11/2017.
 */

public class CountriesService extends IntentService {
    public static final String COUNTRIES_SERVICE="com.example.olgag.currencyconverter.services.COUNTRIES_SERVICE";

    public CountriesService() { super(CountriesService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String urlString ="https://free.currencyconverterapi.com/api/v5/currencies?apiKey="+getResources().getString(R.string.api_key);
                // "https://free.currconv.com/api/v7/convert?q=USD_PHP&compact=ultra&apiKey=b2155f37d9b8bc66429b";
        ArrayList<Coin> listCountries=new ArrayList<>();
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
            if(!builder.toString().isEmpty()) {

                getContentResolver().delete(DbProvider.CONTENT_TABLE_URI, null, null);

                ContentValues values = new ContentValues();

                JSONObject root = new JSONObject(builder.toString());
                JSONObject results = root.getJSONObject("results");
                Iterator<?> keys = results.keys();
                while (keys.hasNext()){
                    String  key = (String) keys.next();
                    JSONObject nextCountry= (JSONObject) results.get(key);
                    String currencyName=nextCountry.getString("currencyName")+ " ";
                    String id=nextCountry.getString("id")+ " ";
                    if(!id.contains("BTC")&& !id.contains("ILS") && !id.contains("EUR") && !id.contains("USD")
                            && !id.contains("JPY") && !id.contains("GBP") && !id.contains("RUB")) {
                        listCountries.add(new Coin(id, currencyName));
                        values.put(DBCoinNameHelper.COIN_ID,id);
                        values.put(DBCoinNameHelper.COIN_NAME, currencyName);
                        getContentResolver().insert(DbProvider.CONTENT_TABLE_URI, values);

                    }
                }

            }
            Intent broadIntent = new Intent(COUNTRIES_SERVICE);
            broadIntent.putExtra("listCountries", listCountries);
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
