package com.olgag.currencyconverter.activities;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.olgag.currencyconverter.R;
import com.olgag.currencyconverter.controller.CoinAdapter;
import com.olgag.currencyconverter.fragments.FragTo;
import com.olgag.currencyconverter.fragments.ResultFragment;
import com.olgag.currencyconverter.model.Coin;
import com.olgag.currencyconverter.myServices.CurrencyReceiver;
import com.olgag.currencyconverter.myServices.CurrencyServicr;




public class MainActivity extends AppCompatActivity implements View.OnClickListener,CoinAdapter.OnCurrencyClickListener,FragTo.OnChoosenCurrencyClickListener {
    private TextView txtFrom,txtTo,txtSumForChange,txtKindOfSumToChange,txtKindOfSumFromChange,txtSumResult,txtRate;
    private EditText txtCountForChange,myRate;
    private ImageButton btnFrom,btnTo,btnGo,btnSwap;
    private CurrencyReceiver curencyReceiver;
    private FragTo fragmentTo;
    private ResultFragment fragResult;
    private boolean flagFromTo, isInternet;
    private Coin coinFrom, coinTo;
    private SharedPreferences sp;
    private TextView txtToast;
    private Toast myToast;
    private View toastLayout;
    private InputMethodManager imm;
    private SearchView searchCurrency;
    private LinearLayout newRateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRate=findViewById(R.id.myRate);
        txtCountForChange=findViewById(R.id.txtCountForChange);
        txtFrom= (TextView) findViewById(R.id.txtFrom);
        txtTo= (TextView) findViewById(R.id.txtTo);
        btnFrom= (ImageButton) findViewById(R.id.btnFrom);
        btnTo= (ImageButton) findViewById(R.id.btnTo);
        btnGo= (ImageButton) findViewById(R.id.btnGo);
        btnSwap= (ImageButton) findViewById(R.id.btnSwap);
        btnFrom.setOnClickListener(this);
        btnTo.setOnClickListener(this);
        btnGo.setOnClickListener(this);
        btnSwap.setOnClickListener(this);
        txtFrom.setOnClickListener(this);
        txtTo.setOnClickListener(this);

        LayoutInflater inflater = getLayoutInflater();
        toastLayout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        txtToast = (TextView) toastLayout.findViewById(R.id.txtToast);
        myToast = new Toast(getApplicationContext());
        isInternet=true;
        if(!isOnline()) {
            isInternet =false;

            txtToast.setText("You don't have Internet connection! \n This app won't get the currency rate without an Internet connection!");
            myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            myToast.setDuration(Toast.LENGTH_LONG);
            myToast.setView(toastLayout);
            myToast.show();
            ViewGroup.LayoutParams lp =  myRate.getLayoutParams();
            lp.width=ViewGroup.LayoutParams.MATCH_PARENT;
            myRate.setLayoutParams(lp);
            myRate.setVisibility(View.VISIBLE);

        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        txtFrom.setText(sp.getString("fromCurrency", ""));
        txtTo.setText(sp.getString("toCurrency", ""));
        coinFrom = new Coin(sp.getString("fromCurId", ""), txtFrom.getText().toString());
        coinTo = new Coin(sp.getString("toCurId", ""), txtTo.getText().toString());

        fragmentTo= (FragTo) getSupportFragmentManager().findFragmentByTag("fragTo");
        fragResult= (ResultFragment) getSupportFragmentManager().findFragmentByTag("fragResult");
        if(savedInstanceState == null){
            fragmentTo=new FragTo();
            fragResult=new ResultFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer,fragmentTo,"fragTo")
                    .hide(fragmentTo)
                    .add(R.id.fragmentContainer,fragResult,"fragResult")
                    .hide(fragResult)
                    .commit();

        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isInternet", isInternet);
        fragmentTo.setArguments(bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        txtSumForChange= fragResult.getView().findViewById(R.id.txtSumForChange);
        txtKindOfSumToChange= fragResult.getView().findViewById(R.id.txtKindOfSumToChange);
        txtKindOfSumFromChange= fragResult.getView().findViewById(R.id.txtKindOfSumFromChange);
        txtSumResult= fragResult.getView().findViewById(R.id.txtSumResult);
        txtRate= fragResult.getView().findViewById(R.id.txtRate);
        newRateLayout= fragResult.getView().findViewById(R.id.newRateLayout);
        searchCurrency=fragmentTo.getView().findViewById(R.id.searchCurrency);
    }

    @Override
    public void onClick(View view) {

        final LinearLayout container = (LinearLayout)fragResult.getView().findViewById(R.id.container);
        String strCurrency;
        Intent intent;

        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        switch (view.getId())
        {
            case  R.id.txtTo:
            case R.id.btnTo:
                flagFromTo=true;


                searchCurrency.setQuery("",false);

                for (int i = 0; i < container.getChildCount(); i++) {
                    View rowView = container.getChildAt(i);
                    rowView.animate().setStartDelay(i * 500)
                            .scaleX(0).scaleY(0);
                }

                getSupportFragmentManager().beginTransaction()
                        .hide(fragResult)
                        .show(fragmentTo)
                        .commit();
                break;
            case  R.id.txtFrom:
            case R.id.btnFrom:
                flagFromTo=false;


                searchCurrency.setQuery("",false);

                for (int i = 0; i < container.getChildCount(); i++) {
                    View rowView = container.getChildAt(i);
                    rowView.animate().setStartDelay(i * 500)
                            .scaleX(0).scaleY(0);
                }

                getSupportFragmentManager().beginTransaction()
                        .hide(fragResult)
                        .show(fragmentTo)
                        .commit();

                break;
            case R.id.btnGo:
                if(txtCountForChange.getText().toString().isEmpty() ||( txtCountForChange.getText().toString().contains(".") && txtCountForChange.getText().toString().length()==1)) {
                    txtToast.setText("Enter the sum of money!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }
                if(!isInternet && (myRate.getText().toString().isEmpty() ||( myRate.getText().toString().contains(".") && myRate.getText().toString().length()==1))){
                    txtToast.setText("Enter the rate!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }
                if(txtFrom.getText().toString().isEmpty()) {
                    txtToast.setText("Choose a currency!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }
                if(txtTo.getText().toString().isEmpty()) {
                    txtToast.setText("Choose a currency!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }

                newRateLayout.setVisibility(View.INVISIBLE);


                for (int i = 0; i < container.getChildCount(); i++) {
                    View rowView = container.getChildAt(i);
                    rowView.animate().setStartDelay(i * 500)
                            .scaleX(1).scaleY(1);
                }


                txtSumForChange.setText(txtCountForChange.getText());
                txtKindOfSumFromChange.setText(" " + coinFrom.getCurrencyName() + " = ");
                txtKindOfSumToChange.setText(coinTo.getCurrencyName());



                if(isInternet) {
                    strCurrency = coinFrom.getCurrencyId().trim() + "_" + coinTo.getCurrencyId().trim();

                    fragResult.setIdCurrencyForDate(strCurrency);

                    intent = new Intent(this, CurrencyServicr.class);
                    intent.putExtra("putCurrency", strCurrency);
                    intent.putExtra("putMoney", Double.parseDouble(txtCountForChange.getText().toString()));
                    this.startService(intent);


                    curencyReceiver = new CurrencyReceiver(this, txtSumResult, txtRate);
                    LocalBroadcastManager.getInstance(this).registerReceiver(curencyReceiver, new IntentFilter(CurrencyServicr.CURRENCY_SERVICE));
                }
                else{
                    txtRate.setText("your rate is: " + myRate.getText());
                    txtRate.setCompoundDrawables(null, null, null, null);
                    Double dd =Double.parseDouble(myRate.getText().toString()) * Double.parseDouble(txtCountForChange.getText().toString());
                    txtSumResult.setText("" +  (double)Math.round(dd * 100) / 100);
                }


                getSupportFragmentManager().beginTransaction()
                        .hide(fragmentTo)
                        .show(fragResult)
                        .commit();

                break;
            case R.id.btnSwap:

                if(txtCountForChange.getText().toString().isEmpty() ||( txtCountForChange.getText().toString().contains(".") && txtCountForChange.getText().toString().length()==1)) {
                    txtToast.setText("Enter the sum of money!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }
                if(!isInternet && (myRate.getText().toString().isEmpty() ||( myRate.getText().toString().contains(".") && myRate.getText().toString().length()==1))){
                    txtToast.setText("Enter the rate!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }
                if(txtFrom.getText().toString().isEmpty()) {
                    txtToast.setText("Choose a currency!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }
                if(txtTo.getText().toString().isEmpty()) {
                    txtToast.setText("Choose a currency!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }


                Coin tmpCoin=new Coin(coinFrom.getCurrencyId(),coinFrom.getCurrencyName());
                coinFrom.setCurrencyId(coinTo.getCurrencyId());
                coinFrom.setCurrencyName(coinTo.getCurrencyName());
                coinTo.setCurrencyId(tmpCoin.getCurrencyId());
                coinTo.setCurrencyName(tmpCoin.getCurrencyName());

                txtTo.setText(coinTo.getCurrencyName());
                txtFrom.setText(coinFrom.getCurrencyName());

                newRateLayout.setVisibility(View.INVISIBLE);

                txtSumForChange.setText(txtCountForChange.getText());
                txtKindOfSumFromChange.setText(" " + coinFrom.getCurrencyName() + " = ");
                txtKindOfSumToChange.setText(coinTo.getCurrencyName());

                if(isInternet) {
                    strCurrency = coinFrom.getCurrencyId().trim() + "_" + coinTo.getCurrencyId().trim();

                    fragResult.setIdCurrencyForDate(strCurrency);

                    intent = new Intent(this, CurrencyServicr.class);
                    intent.putExtra("putCurrency", strCurrency);
                    intent.putExtra("putMoney", Double.parseDouble(txtCountForChange.getText().toString()));
                    this.startService(intent);

                    curencyReceiver = new CurrencyReceiver(this, txtSumResult, txtRate);
                    LocalBroadcastManager.getInstance(this).registerReceiver(curencyReceiver, new IntentFilter(CurrencyServicr.CURRENCY_SERVICE));
                }
                else{

                    txtRate.setText("your rate is: " + myRate.getText());
                    txtRate.setCompoundDrawables(null, null, null, null);
                    Double dd =Double.parseDouble(myRate.getText().toString()) * Double.parseDouble(txtCountForChange.getText().toString());
                    txtSumResult.setText("" +  (double)Math.round(dd * 100) / 100);
                }

                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(curencyReceiver);
        String toCurrency = txtTo.getText().toString();
        String fromCurrency = txtFrom.getText().toString();
        String toCurId=coinTo.getCurrencyId();
        String fromCurId=coinFrom.getCurrencyId();
        sp.edit()
                .putString("toCurrency", toCurrency)
                .putString("fromCurrency", fromCurrency)
                .putString("toCurId", toCurId)
                .putString("fromCurId", fromCurId)
                .apply();
    }

    @Override
    public void setCurrency(Coin curentCoin)
    {
        if(flagFromTo) {
            txtTo.setText(curentCoin.getCurrencyName());
            coinTo.setCurrencyId(curentCoin.getCurrencyId());
            coinTo.setCurrencyName(curentCoin.getCurrencyName());
            String toCurrency = txtTo.getText().toString();
            String toCurId=curentCoin.getCurrencyId();
            sp.edit()
                    .putString("toCurrency", toCurrency)
                    .putString("toCurId", toCurId)
                    .apply();
        }
        else {
            txtFrom.setText(curentCoin.getCurrencyName());
            coinFrom.setCurrencyId(curentCoin.getCurrencyId());
            coinFrom.setCurrencyName(curentCoin.getCurrencyName());
            String fromCurrency = txtFrom.getText().toString();
            String fromCurId=curentCoin.getCurrencyId();
            sp.edit()
                    .putString("fromCurrency", fromCurrency)
                    .putString("fromCurId", fromCurId)
                    .apply();
        }
        getSupportFragmentManager().beginTransaction()
                .hide(fragmentTo)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.closeApp:
                finish();
                break;
            case R.id.infoApp:
                txtToast.setText("All currency  rate information from: \n https://free.currencyconverterapi.com/");
                myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                myToast.setDuration(Toast.LENGTH_LONG);
                myToast.setView(toastLayout);
                myToast.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null){
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        return false;
    }


}
