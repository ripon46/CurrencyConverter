package com.therap.amin.currencyconverter.Fragments;


import android.content.Context;
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

import com.google.inject.Inject;
import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.FileProcessor;
import com.therap.amin.currencyconverter.R;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by amin on 5/10/16.
 */
public class CurrencyConversionFragment extends RoboFragment {

    @InjectResource(R.array.currencies)
    String[] availableCurrencies;

    @InjectView(R.id.tvOutputCurrencyValue)
    TextView tvOutputCurrencyValue;

    @InjectView(R.id.tvSavedCurrency)
    TextView tvSavedCurrencyRate;

    @InjectView(R.id.etCurrencyAmount)
    EditText etInputCurrencyValue;

    @InjectView(R.id.spnFromCurrency)
    Spinner fromCurrencySpinner;

    @InjectView(R.id.spnToCurrency)
    Spinner toCurrencySpinner;

    @Inject
    FileProcessor fileProcessor;

    @InjectView(R.id.rgSource)
    RadioGroup sourceRadioGroup;

    @InjectView(R.id.rbWeb)
    RadioButton webRadioButton;

    @InjectView(R.id.rbOwn)
    RadioButton ownvalueRadioButton;

    @InjectResource(R.string.own)
    String ownSavedValue;

    @InjectResource(R.string.web)
    String webFetchedValue;

    @Inject
    String selectedSource;

    ArrayAdapter<String> inputCurrencyAdapter;
    ArrayAdapter<String> outputCurrencyAdapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency_conversion, container, false);
    }

    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        selectedSource = webFetchedValue;
        inputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        fromCurrencySpinner.setAdapter(inputCurrencyAdapter);
        outputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        toCurrencySpinner.setAdapter(outputCurrencyAdapter);

        if (sharedPreferences.contains(Constants.LAST_SAVED_FROM_CURRENCY_KEY)) {
            String lastSavedFromCurrency = sharedPreferences.getString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, "");
            String lastSavedToCurrency = sharedPreferences.getString(Constants.LAST_SAVED_TO_CURRENCY_KEY, "");
            fromCurrencySpinner.setSelection(inputCurrencyAdapter.getPosition(lastSavedFromCurrency));
            toCurrencySpinner.setSelection(outputCurrencyAdapter.getPosition(lastSavedToCurrency));
        }

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

        etInputCurrencyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    updateUI();
                } else {
                    tvOutputCurrencyValue.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sourceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = sourceRadioGroup.getCheckedRadioButtonId();

                if (id == webRadioButton.getId()) {
                    selectedSource = webFetchedValue;
                } else if (id == ownvalueRadioButton.getId()) {
                    selectedSource = ownSavedValue;
                }
                updateUI();
            }
        });

    }

    public void updateUI() {
        String from = fromCurrencySpinner.getSelectedItem().toString();
        String to = toCurrencySpinner.getSelectedItem().toString();

        Double conversionRate = null;
        if (selectedSource.equals(ownSavedValue)) {
            String valueFromPreference = sharedPreferences.getString(from + to, null);
            if (valueFromPreference != null)
                conversionRate = Double.parseDouble(valueFromPreference);
        } else if (selectedSource.equals(webFetchedValue) && fileProcessor.calculateConversionRate(from, to) != -1) {
            conversionRate = fileProcessor.calculateConversionRate(from, to);
        }
        if (conversionRate != null) {
            tvSavedCurrencyRate.setText(String.format("1 %s = %.2f %s", from, conversionRate, to));
            if (!etInputCurrencyValue.getText().toString().equals("")) {
                conversionRate *= Double.parseDouble(etInputCurrencyValue.getText().toString());
                tvOutputCurrencyValue.setText(String.format("%.2f", conversionRate));
            }
        } else {
            tvSavedCurrencyRate.setText(R.string.not_available);
            tvOutputCurrencyValue.setText("0");
        }
        sharedPreferences.edit().putString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, fromCurrencySpinner.getSelectedItem().toString()).apply();
        sharedPreferences.edit().putString(Constants.LAST_SAVED_TO_CURRENCY_KEY, toCurrencySpinner.getSelectedItem().toString()).apply();
    }
}
