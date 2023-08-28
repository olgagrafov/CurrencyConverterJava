package com.olgag.currencyconverter.controller;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.olgag.currencyconverter.R;
import com.olgag.currencyconverter.model.Coin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by olgag on 16/11/2017.
 */

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinHolder>{
    private Context context;
    private ArrayList<Coin> coins = new ArrayList<>();
    private List<Coin> currencyNames = new ArrayList<>() ;
    private OnCurrencyClickListener listener;

    public CoinAdapter(Context context) {
        this.context = context;
        listener = (OnCurrencyClickListener) context;
    }


   public void addAll(ArrayList<Coin> coins){
      this.currencyNames = coins;
      this.coins.addAll(coins);
    }

    @Override
    public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new CoinHolder(LayoutInflater.from(context).inflate(R.layout.item_favor_coin, parent, false));
    }

    @Override
    public void onBindViewHolder(CoinHolder holder, int position) {
    //    holder.bind(coins.get(position));
        holder.bind(currencyNames.get(position));
    }

    @Override
    public int getItemCount() {

     //  return coins.size();
       return currencyNames.size();
    }




   public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        currencyNames.clear();
        if (charText.length() == 0) {
            currencyNames.addAll(coins);
        }
         else {
           for (Coin wp : coins) {
                if (wp.getCurrencyName().toLowerCase(Locale.getDefault()).contains(charText)) {
                   currencyNames.add(wp);
                }
            }
        }
        notifyDataSetChanged();
   }


//**********************HOLDER********************************************
   public class CoinHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView txtName;
    private Coin curentCoin;

        public CoinHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtName.setOnClickListener(this);

        }

    public void bind(Coin coin) {
        curentCoin = coin;
        txtName.setText(coin.getCurrencyName());
    }

    @Override
    public void onClick(View view) {
        listener.setCurrency(curentCoin);
    }
}


    public interface OnCurrencyClickListener{
        void setCurrency(Coin curentCoin);
    }
}

