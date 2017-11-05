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
import com.therap.amin.currencyconverter.interfaces.ConversionFragmentViewInterface;
import com.therap.amin.currencyconverter.interfaces.ConversionPresenterInterface;

import java.util.Locale;

import javax.inject.Inject;


/**
 * @author Ripon
 */
public class CurrencyConversionFragment extends Fragment implements AdapterView.OnItemSelectedListener, TextWatcher, ConversionFragmentViewInterface {

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

    private String selectedSource;

    @Inject
    ConversionPresenterInterface currencyConversionPresenter;

    @Inject
    ArrayAdapter<String> currencyAdapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency_conversion, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CurrencyConversionApplication.getComponent().inject(this);
        currencyConversionPresenter.setView(this, getActivity());

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

        fromCurrencySpinner.setAdapter(currencyAdapter);
        toCurrencySpinner.setAdapter(currencyAdapter);


        if (sharedPreferences.contains(Constants.LAST_SAVED_FROM_CURRENCY_KEY)) {
            String lastSavedFromCurrency = sharedPreferences.getString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, "");
            String lastSavedToCurrency = sharedPreferences.getString(Constants.LAST_SAVED_TO_CURRENCY_KEY, "");
            fromCurrencySpinner.setSelection(currencyAdapter.getPosition(lastSavedFromCurrency));
            toCurrencySpinner.setSelection(currencyAdapter.getPosition(lastSavedToCurrency));
        }

        etInputCurrencyValue.addTextChangedListener(this);
        fromCurrencySpinner.setOnItemSelectedListener(this);
        toCurrencySpinner.setOnItemSelectedListener(this);

        sourceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = sourceRadioGroup.getCheckedRadioButtonId();

                if (id == webRadioButton.getId()) {
                    selectedSource = webFetchedValue;
                } else if (id == ownvalueRadioButton.getId()) {
                    selectedSource = ownSavedValue;
                }
                currencyConversionPresenter.calculateConversionRate(fromCurrencySpinner.getSelectedItem().toString(),
                        toCurrencySpinner.getSelectedItem().toString(), selectedSource);

                currencyConversionPresenter.saveToPreference(fromCurrencySpinner.getSelectedItem().toString(),
                        toCurrencySpinner.getSelectedItem().toString());
            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currencyConversionPresenter.calculateConversionRate(fromCurrencySpinner.getSelectedItem().toString(),
                toCurrencySpinner.getSelectedItem().toString(), selectedSource);

        currencyConversionPresenter.saveToPreference(fromCurrencySpinner.getSelectedItem().toString(),
                toCurrencySpinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        if (!s.toString().equals("")) {
            currencyConversionPresenter.calculateConversionRate(fromCurrencySpinner.getSelectedItem().toString(),
                    toCurrencySpinner.getSelectedItem().toString(), selectedSource);

            currencyConversionPresenter.saveToPreference(fromCurrencySpinner.getSelectedItem().toString(),
                    toCurrencySpinner.getSelectedItem().toString());
        } else {
            tvOutputCurrencyValue.setText("0");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void updateTextViews(Double conversionRate, String from, String to) {
        if (conversionRate != null) {
            tvSavedCurrencyRate.setText(String.format(Locale.ENGLISH, "1 %s = %.2f %s", from, conversionRate, to));
            if (!etInputCurrencyValue.getText().toString().equals("")) {
                conversionRate *= Double.parseDouble(etInputCurrencyValue.getText().toString());
                tvOutputCurrencyValue.setText(String.format(Locale.ENGLISH, "%.2f", conversionRate));
            }
        } else {
            tvSavedCurrencyRate.setText(R.string.not_available);
            tvOutputCurrencyValue.setText("0");
        }
    }

    @Override
    public void updateUI() {
        currencyConversionPresenter.calculateConversionRate(fromCurrencySpinner.getSelectedItem().toString(),
                toCurrencySpinner.getSelectedItem().toString(), selectedSource);

        currencyConversionPresenter.saveToPreference(fromCurrencySpinner.getSelectedItem().toString(),
                toCurrencySpinner.getSelectedItem().toString());
    }
}
