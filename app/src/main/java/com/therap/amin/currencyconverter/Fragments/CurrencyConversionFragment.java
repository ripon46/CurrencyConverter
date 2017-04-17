package com.therap.amin.currencyconverter.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.therap.amin.currencyconverter.CurrencyConversionApplication;
import com.therap.amin.currencyconverter.R;
import com.therap.amin.currencyconverter.component.DaggerFragmentComponent;
import com.therap.amin.currencyconverter.component.FragmentComponent;
import com.therap.amin.currencyconverter.module.FragmentModule;
import com.therap.amin.currencyconverter.service.FileProcessor;

import javax.inject.Inject;


/**
 * @author Ripon
 */
public class CurrencyConversionFragment extends Fragment {

    private String ownSavedValue;
    private String webFetchedValue;

    private TextView tvOutputCurrencyValue;
    private TextView tvSavedCurrencyRate;
    private EditText etInputCurrencyValue;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private RadioGroup sourceRadioGroup;
    private RadioButton webRadioButton;
    private RadioButton ownvalueRadioButton;

    @Inject
    FileProcessor fileProcessor;

    private String selectedSource;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ArrayAdapter<String> inputCurrencyAdapter;

    @Inject
    ArrayAdapter<String> outputCurrencyAdapter;

    private FragmentComponent fragmentComponent;

    public FragmentComponent getFragmentComponent() {
        if (fragmentComponent == null) {
            fragmentComponent = DaggerFragmentComponent.builder()
                    .applicationComponent(CurrencyConversionApplication.get(getContext()).getComponent())
                    .fragmentModule(new FragmentModule(getContext()))
                    .build();
        }
        return fragmentComponent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency_conversion, container, false);
    }

    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getFragmentComponent().inject(this);

        tvOutputCurrencyValue = (TextView) view.findViewById(R.id.tvOutputCurrencyValue);
        tvSavedCurrencyRate = (TextView) view.findViewById(R.id.tvSavedCurrency);
        etInputCurrencyValue = (EditText) view.findViewById(R.id.etCurrencyAmount);
        fromCurrencySpinner = (Spinner) view.findViewById(R.id.spnFromCurrency);
        toCurrencySpinner = (Spinner) view.findViewById(R.id.spnToCurrency);
        sourceRadioGroup = (RadioGroup) view.findViewById(R.id.rgSource);
        webRadioButton = (RadioButton) view.findViewById(R.id.rbWeb);
        ownvalueRadioButton = (RadioButton) view.findViewById(R.id.rbOwn);

        ownSavedValue = getString(R.string.own);
        webFetchedValue = getString(R.string.web);

        selectedSource = webFetchedValue;
        fromCurrencySpinner.setAdapter(inputCurrencyAdapter);
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
