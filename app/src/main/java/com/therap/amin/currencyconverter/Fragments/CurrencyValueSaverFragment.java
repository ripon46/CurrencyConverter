package com.therap.amin.currencyconverter.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.FileProcessor;
import com.therap.amin.currencyconverter.MainActivity;
import com.therap.amin.currencyconverter.R;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by amin on 5/10/16.
 */
public class CurrencyValueSaverFragment extends RoboFragment {

    @InjectResource(R.array.currencies)
    String[] availableCurrencies;

    @InjectView(R.id.spnLeftCurrency)
    Spinner inputCurrencySpinner;

    @InjectView(R.id.spnRightCurrency)
    Spinner outputCurrencySpinner;

    @InjectView(R.id.btnSave)
    Button saveButton;

    @InjectView(R.id.etRightCurrencyValue)
    EditText etConversionValue;

    @Inject
    FileProcessor fileProcessor;

    @InjectView(R.id.tvPresentCurrencyRelation)
    TextView tvPresentCurrencyRelation;

    @InjectResource(R.bool.isTablet)
    boolean tabletSize;

    SharedPreferences sharedPreferences;
    ArrayAdapter<String> inputCurrencyAdapter;
    ArrayAdapter<String> outputCurrencyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency_rate_saver, container, false);
    }

    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        inputCurrencySpinner.setAdapter(inputCurrencyAdapter);
        outputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        outputCurrencySpinner.setAdapter(outputCurrencyAdapter);

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(Constants.LAST_SAVED_FROM_CURRENCY_KEY)) {
            String lastSavedFromCurrency = sharedPreferences.getString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, "");
            String lastSavedToCurrency = sharedPreferences.getString(Constants.LAST_SAVED_TO_CURRENCY_KEY, "");
            inputCurrencySpinner.setSelection(inputCurrencyAdapter.getPosition(lastSavedFromCurrency));
            outputCurrencySpinner.setSelection(outputCurrencyAdapter.getPosition(lastSavedToCurrency));
        }

        inputCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        outputCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etConversionValue.getText().toString().equals("")) {
                    String inputCurrency = inputCurrencySpinner.getSelectedItem().toString();
                    String outputCurrency = outputCurrencySpinner.getSelectedItem().toString();
                    if (!inputCurrency.equals(outputCurrency)) {
                        sharedPreferences.edit().putString(inputCurrency + outputCurrency, etConversionValue.getText().toString()).apply();
                        Toast.makeText(getActivity(), "Set successfully", Toast.LENGTH_SHORT).show();
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (!tabletSize) {
                            mainActivity.replaceFragment(new CurrencyConversionFragment());
                            mainActivity.setMenus(new CurrencyConversionFragment());
                        }
                        sharedPreferences.edit().putString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, inputCurrencySpinner.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString(Constants.LAST_SAVED_TO_CURRENCY_KEY, outputCurrencySpinner.getSelectedItem().toString()).apply();
                    } else {
                        Toast.makeText(getActivity(), "You cant set " + inputCurrency + "->" + outputCurrency + " conversion value", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please give input correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void updateUI() {
        String from = inputCurrencySpinner.getSelectedItem().toString();
        String to = outputCurrencySpinner.getSelectedItem().toString();
        Double conversionRate = fileProcessor.calculateConversionRate(from, to);

        if (conversionRate != -1) {
            tvPresentCurrencyRelation.setText(String.format("1 %s = %.2f %s", from, conversionRate, to));
        } else {
            tvPresentCurrencyRelation.setText(R.string.not_available);
        }
        etConversionValue.setText(sharedPreferences.getString(from + to, ""));
        boolean canPreferenceBeEditedFromThisFragment = sharedPreferences.contains(Constants.LAST_SAVED_FROM_CURRENCY_KEY) || !fileProcessor.values.isEmpty();
        if (canPreferenceBeEditedFromThisFragment) {
            sharedPreferences.edit().putString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, inputCurrencySpinner.getSelectedItem().toString()).apply();
            sharedPreferences.edit().putString(Constants.LAST_SAVED_TO_CURRENCY_KEY, outputCurrencySpinner.getSelectedItem().toString()).apply();
        }
    }
}
