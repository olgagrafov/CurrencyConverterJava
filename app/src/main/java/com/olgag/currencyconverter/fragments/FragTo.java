package com.olgag.currencyconverter.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.olgag.currencyconverter.R;
import com.olgag.currencyconverter.controller.CoinAdapter;
import com.olgag.currencyconverter.db.DBCoinNameHelper;
import com.olgag.currencyconverter.db.DbProvider;
import com.olgag.currencyconverter.model.Coin;
import com.olgag.currencyconverter.myServices.CountriesReceiver;
import com.olgag.currencyconverter.myServices.CountriesService;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragTo extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {
    private CountriesReceiver countriesReceiver;
    private ImageButton btnBitcoim,btnDollar,btnShekel,btnEuro,btnRub,btnPound,btnYena;
    private OnChoosenCurrencyClickListener listener;
    private SearchView searchCurrency;
    private CoinAdapter coinsAdapter;
    private boolean fragIsInternet;

    public FragTo() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnChoosenCurrencyClickListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
       listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vw =inflater.inflate(R.layout.fragment_frag_to, container, false);
     //   Bundle bundle = getArguments();
        savedInstanceState = getArguments();
        fragIsInternet = savedInstanceState.getBoolean("isInternet",false);

        RecyclerView coinsList=vw.findViewById(R.id.listCoins);
        coinsList.setLayoutManager(new GridLayoutManager(vw.getContext(),2));

        searchCurrency=vw.findViewById(R.id.searchCurrency);
        searchCurrency.setOnQueryTextListener(this);
        btnBitcoim=vw.findViewById(R.id.btnBitcoim);
        btnDollar=vw.findViewById(R.id.btnDollar);
        btnShekel=vw.findViewById(R.id.btnShekel);
        btnEuro=vw.findViewById(R.id.btnEuro);
        btnRub=vw.findViewById(R.id.btnRub);
        btnPound=vw.findViewById(R.id.btnPound);
        btnYena=vw.findViewById(R.id.btnYena);
        btnBitcoim.setOnClickListener(this);
        btnDollar.setOnClickListener(this);
        btnShekel.setOnClickListener(this);
        btnEuro.setOnClickListener(this);
        btnRub.setOnClickListener(this);
        btnPound.setOnClickListener(this);
        btnYena.setOnClickListener(this);


        coinsAdapter = new CoinAdapter(vw.getContext());
        coinsList.setAdapter(coinsAdapter);

        if(fragIsInternet) {

            Intent intC = new Intent(getContext(), CountriesService.class);
            getContext().startService(intC);
            countriesReceiver = new CountriesReceiver(getContext(), coinsAdapter);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(countriesReceiver, new IntentFilter(CountriesService.COUNTRIES_SERVICE));
        }
        else{
            Cursor c = getContext().getContentResolver().query(DbProvider.CONTENT_TABLE_URI, null, null, null, "COIN_NAME ASC");
            ArrayList<Coin> listCountries=new  ArrayList<>();
            while (c.moveToNext()) {
                 listCountries.add(new Coin(c.getString(c.getColumnIndex(DBCoinNameHelper.COIN_ID)),c.getString(c.getColumnIndex(DBCoinNameHelper.COIN_NAME))));
            }
            coinsAdapter.addAll(listCountries);
        }

        return vw;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(countriesReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBitcoim:
                listener.setCurrency(new Coin("BTC","Bitcoin"));
                break;
            case R.id.btnDollar:
                listener.setCurrency(new Coin("USD","United States Dollar"));
                break;
            case R.id.btnShekel:
                listener.setCurrency(new Coin("ILS","Israeli New Sheqel"));
                break;
            case R.id.btnEuro:
                listener.setCurrency(new Coin("EUR","Euro"));
                break;
            case R.id.btnRub:
                listener.setCurrency(new Coin("RUB","Russian Ruble"));
                break;
            case R.id.btnPound:
                listener.setCurrency(new Coin("GBP","British Pound"));
                break;
            case R.id.btnYena:
                listener.setCurrency(new Coin("JPY","Japanese Yen"));
                break;
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        coinsAdapter.filter(text);
        return false;
    }

    public interface OnChoosenCurrencyClickListener{
       void    setCurrency(Coin curentCoin);
   }
}
