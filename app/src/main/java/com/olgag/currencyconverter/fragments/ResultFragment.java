package com.olgag.currencyconverter.fragments;

        import android.animation.ObjectAnimator;
        import android.annotation.SuppressLint;
        import android.app.DatePickerDialog;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v4.app.Fragment;
        import android.support.v4.content.LocalBroadcastManager;
        import android.text.Html;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.RadioGroup;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.olgag.currencyconverter.R;
        import com.olgag.currencyconverter.myServices.NewDateReceiver;
        import com.olgag.currencyconverter.myServices.NewDateService;

        import java.util.Calendar;


public class ResultFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener {

    private TextView txtRate,txtSumResult,txtSumForChange;
    private LinearLayout newRateLayout;
    private EditText txtChangeRate;
    private ImageButton btnGoNewRate,showCalendar;
    private TextView txtToast;
    private Toast myToast;
    private View toastLayout;
    private RadioGroup kindOfChange;
    private String idCurrencyForDate;
    private NewDateReceiver newDateReceiver;
    private ObjectAnimator chageX;
    private InputMethodManager imm;

    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vw=  inflater.inflate(R.layout.fragment_result, container, false);


        btnGoNewRate=vw.findViewById(R.id.btnGoNewRate);
        showCalendar=vw.findViewById(R.id.showCalendar);
        txtChangeRate=vw.findViewById(R.id.txtChangeRate);
        newRateLayout=vw.findViewById(R.id.newRateLayout);
        txtSumForChange=vw.findViewById(R.id.txtSumForChange);
        txtSumResult=vw.findViewById(R.id.txtSumResult);
        txtRate=vw.findViewById(R.id.txtRate);
        kindOfChange=vw.findViewById(R.id.kindOfChange);
        kindOfChange.setOnCheckedChangeListener(this);
        txtRate.setOnClickListener(this);
        btnGoNewRate.setOnClickListener(this);
        showCalendar.setOnClickListener(this);

        toastLayout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) vw.findViewById(R.id.custom_toast_container));
        toastLayout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) vw.findViewById(R.id.custom_toast_container));
        txtToast = (TextView) toastLayout.findViewById(R.id.txtToast);
        myToast = new Toast(getContext());

        return vw;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtRate:
                newRateLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btnGoNewRate:
                if(txtChangeRate.getText().toString().isEmpty() ||( txtChangeRate.getText().toString().contains(".") && txtChangeRate.getText().toString().length()==1)) {
                    txtToast.setText("Enter the new  rate!");
                    myToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    myToast.setDuration(Toast.LENGTH_SHORT);
                    myToast.setView(toastLayout);
                    myToast.show();
                    break;
                }

                imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                String first ="The new rate you choose is: " ;
                String next = " <BR><B><U>" + txtChangeRate.getText() + "</U></B>";
                txtRate.setText(Html.fromHtml(first + next));
                txtSumResult.setText("");
                Double dd=Double.parseDouble(txtChangeRate.getText().toString()) * Double.parseDouble(txtSumForChange.getText().toString());
                 txtSumResult.setText("" +  (double)Math.round(dd * 100) / 100);
                blink();

                newRateLayout.setVisibility(View.INVISIBLE);

                break;
            case R.id.showCalendar:
                Calendar cal = Calendar.getInstance();
                @SuppressLint("ResourceType") DatePickerDialog datePicker = new DatePickerDialog(getContext(),3,this,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                cal.add(Calendar.YEAR, -1);
                datePicker.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePicker.setTitle(getResources().getString(R.string.select_date));
                datePicker.show();
                break;
        }

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int chekedButton) {
        switch (chekedButton){
            case R.id.byDate:
                txtChangeRate.setVisibility(View.INVISIBLE);
                btnGoNewRate.setVisibility(View.INVISIBLE);
                showCalendar.setVisibility(View.VISIBLE);
                break;
            case R.id.byRate:
                showCalendar.setVisibility(View.INVISIBLE);
                txtChangeRate.setVisibility(View.VISIBLE);
                btnGoNewRate.setVisibility(View.VISIBLE);
                break;
        }


    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

       newRateLayout.setVisibility(View.INVISIBLE);

        month++;
        String strMonth= month+"";
        if(month<10){
            strMonth="0"+month;
        }
        String strDay=day+"";
        if(day<10){
            strDay="0"+day;
        }
        Intent intent = new Intent(getContext(), NewDateService.class);
        intent.putExtra("putCurrency", idCurrencyForDate);
        intent.putExtra("putMoney", Double.parseDouble(txtSumForChange.getText().toString()));
        intent.putExtra("putDate", year+"-" +strMonth+"-" +strDay);
        getContext().startService(intent);


        newDateReceiver = new NewDateReceiver(getContext(), txtSumResult, txtRate);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(newDateReceiver, new IntentFilter(NewDateService.NEWDATE_SERVICE));

    }

    public void setIdCurrencyForDate(String idCurrencyForDate){
        this.idCurrencyForDate=idCurrencyForDate;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(newDateReceiver);

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
                        txtSumResult.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

}
