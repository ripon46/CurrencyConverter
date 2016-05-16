package com.therap.amin.currencyconverter.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.R;

/**
 * Created by amin on 5/10/16.
 */
public class CurrencyConversionFragment extends Fragment{

    SharedPreferences sharedPreferences;
    TextView tvOutputCurrency,tvOutputCurrencyValue,tvInputCurrency;
    EditText etInputCurrencyValue;
    TextView tvSavedCurrency;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);
        tvOutputCurrency = (TextView) view.findViewById(R.id.tvOutputCurrency);
        tvOutputCurrencyValue = (TextView) view.findViewById(R.id.tvOutputCurrencyValue);
        tvInputCurrency = (TextView) view.findViewById(R.id.tvInputCurrency);
        etInputCurrencyValue = (EditText) view.findViewById(R.id.etCurrencyAmount);
        tvOutputCurrencyValue.setText("0");

        tvSavedCurrency = (TextView) view.findViewById(R.id.tvSavedCurrency);
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

        String fromCurrency = sharedPreferences.getString(Constants.INPUT_CURRENCY_KEY,"");
        String toCurrency = sharedPreferences.getString(Constants.OUTPUT_CURRENCY_KEY,"");
        String conversionValue = sharedPreferences.getString(Constants.CONVERSION_RATE_KEY,"");
        if (fromCurrency.equals("")) {
            tvSavedCurrency.setText("Not Set Yet");
        } else {
            tvSavedCurrency.setText("1 "+fromCurrency+" = "+conversionValue+" "+toCurrency);
        }


        etInputCurrencyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    double convertedAmount = Double.parseDouble(s.toString());
                    double conversionVal = Double.parseDouble(sharedPreferences.getString(Constants.CONVERSION_RATE_KEY, "0"));
                    convertedAmount *= conversionVal;
                    tvOutputCurrencyValue.setText(String.format("%.2f",convertedAmount));
                } else {
                    tvOutputCurrencyValue.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvInputCurrency.setText(sharedPreferences.getString(Constants.INPUT_CURRENCY_KEY, "Not Set"));
        tvOutputCurrency.setText(sharedPreferences.getString(Constants.OUTPUT_CURRENCY_KEY, "Not Set"));
        return view;
    }
}
