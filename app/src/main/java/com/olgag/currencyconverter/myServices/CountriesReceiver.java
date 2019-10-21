package com.olgag.currencyconverter.myServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.olgag.currencyconverter.controller.CoinAdapter;
import com.olgag.currencyconverter.model.Coin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by olgag on 14/11/2017.
 */

public class CountriesReceiver extends BroadcastReceiver {
    private CoinAdapter coinsAdapter;
    private Context cont;



   public CountriesReceiver(Context cont) {
        this.cont = cont;
    }

   public CountriesReceiver(Context context, CoinAdapter coinsAdapter) {
        this.cont = cont;
        this.coinsAdapter=coinsAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Coin> listCountries= ( ArrayList<Coin>) intent.getSerializableExtra("listCountries");
        Collections.sort(listCountries, new Comparator<Coin>() {
            @Override
            public int compare(Coin coin, Coin t1) {
                return coin.getCurrencyName().compareToIgnoreCase(t1.getCurrencyName());
            }
            @Override
            public boolean equals(Object o) {
                return false;
            }
        });

         coinsAdapter.addAll(listCountries);

    }
}
