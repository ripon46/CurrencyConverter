package com.therap.amin.currencyconverter.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.FileProcessor;
import com.therap.amin.currencyconverter.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amin on 5/10/16.
 */
public class CurrencyConversionFragment extends Fragment{


    String[] availableCurrencies;
    TextView tvOutputCurrencyValue,tvSavedCurrency;
    EditText etInputCurrencyValue;
    Spinner fromCurrencySpinner,toCurrencySpinner;
    public Map<String,Double> values;
    FileProcessor fileProcessor;
    SharedPreferences sharedPreferences;
    RadioGroup sourceRadioGroup;
    RadioButton webRadioButton,ownvalueRadioButton;
    String selectedSource = "web";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);

        values = new HashMap<String, Double>();
        fileProcessor = new FileProcessor(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

        sourceRadioGroup = (RadioGroup) view.findViewById(R.id.rgSource);
        webRadioButton = (RadioButton) view.findViewById(R.id.rbWeb);
        ownvalueRadioButton = (RadioButton) view.findViewById(R.id.rbOwn);

        values = fileProcessor.readFileAndProcess();

        sourceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = sourceRadioGroup.getCheckedRadioButtonId();

                if (id == webRadioButton.getId()) {
                    selectedSource = "web";
                } else if (id == ownvalueRadioButton.getId()) {
                    selectedSource = "own";
                }
                updateUI();
            }
        });


        availableCurrencies = getResources().getStringArray(R.array.currencies);
        tvOutputCurrencyValue = (TextView) view.findViewById(R.id.tvOutputCurrencyValue);
        etInputCurrencyValue = (EditText) view.findViewById(R.id.etCurrencyAmount);
        tvOutputCurrencyValue.setText("0");

        fromCurrencySpinner = (Spinner) view.findViewById(R.id.spnFromCurrency);
        toCurrencySpinner = (Spinner) view.findViewById(R.id.spnToCurrency);

        ArrayAdapter<String> inputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        fromCurrencySpinner.setAdapter(inputCurrencyAdapter);
        ArrayAdapter<String> outputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        toCurrencySpinner.setAdapter(outputCurrencyAdapter);


        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvSavedCurrency = (TextView) view.findViewById(R.id.tvSavedCurrency);
        etInputCurrencyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    String from = fromCurrencySpinner.getSelectedItem().toString();
                    String to = toCurrencySpinner.getSelectedItem().toString();
                    double conversionVal = 0;
                    String val = null;
                    if (selectedSource.equals("own")) {
                        val = sharedPreferences.getString(from+to,null);
                        if (val != null) {
                            conversionVal = Double.parseDouble(val);
                        }
                    } else if (values.containsKey(from+to)){
                        conversionVal = values.get(from+to);
                    }
                    double convertedAmount = Double.parseDouble(s.toString());
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

        return view;
    }

    public void updateUI () {
        String from = fromCurrencySpinner.getSelectedItem().toString();
        String to = toCurrencySpinner.getSelectedItem().toString();

        fileProcessor = new FileProcessor(getActivity());
        values = fileProcessor.readFileAndProcess();
        Double valueFromWeb = null;
        String valueFromPreference = sharedPreferences.getString(from+to,null);
        if (valueFromPreference != null && selectedSource.equals("own")) {
            valueFromWeb = Double.parseDouble(valueFromPreference);
        } else if (selectedSource.equals("web")){
            valueFromWeb = values.get(from+to);
        }
        if (valueFromWeb != null) {
            tvSavedCurrency.setText("1 "+from+" = "+valueFromWeb+" "+to);
        } else {
            tvSavedCurrency.setText("Not Set");
        }
    }
}
