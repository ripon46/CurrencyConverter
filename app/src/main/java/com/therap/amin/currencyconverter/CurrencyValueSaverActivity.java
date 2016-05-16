package com.therap.amin.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by amin on 5/3/16.
 */
public class CurrencyValueSaverActivity extends AppCompatActivity {

    String[] availableCurrencies;
    Spinner inputCurrencySpinner, outputCurrencySpinner;
    Button saveButton;
    EditText etConversionValue;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currencyvaluesaver);

        availableCurrencies = getResources().getStringArray(R.array.currencies);
        inputCurrencySpinner = (Spinner) findViewById(R.id.spnLeftCurrency);
        outputCurrencySpinner = (Spinner) findViewById(R.id.spnRightCurrency);
        saveButton = (Button) findViewById(R.id.btnSave);
        etConversionValue = (EditText) findViewById(R.id.etRightCurrencyValue);

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

        ArrayAdapter<String> inputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
        inputCurrencySpinner.setAdapter(inputCurrencyAdapter);


        ArrayAdapter<String> outputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
        outputCurrencySpinner.setAdapter(outputCurrencyAdapter);

        if (sharedPreferences.contains(Constants.INPUT_CURRENCY_KEY)) {
            int fromCurrencyposition = inputCurrencyAdapter.getPosition(sharedPreferences.getString(Constants.INPUT_CURRENCY_KEY, ""));
            inputCurrencySpinner.setSelection(fromCurrencyposition);
            int toCurrencyposition = inputCurrencyAdapter.getPosition(sharedPreferences.getString(Constants.OUTPUT_CURRENCY_KEY, ""));
            outputCurrencySpinner.setSelection(toCurrencyposition);
            etConversionValue.setText(sharedPreferences.getString(Constants.CONVERSION_RATE_KEY, ""));
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etConversionValue.getText().toString().equals("")) {

                    String inputCurrency = inputCurrencySpinner.getSelectedItem().toString();
                    String outputCurrency = outputCurrencySpinner.getSelectedItem().toString();
                    if (sharedPreferences.contains(Constants.INPUT_CURRENCY_KEY)) {
                        removeFromPreference();
                        addToPreference(inputCurrency, outputCurrency);
                    } else {
                        addToPreference(inputCurrency, outputCurrency);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please give input correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeFromPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.INPUT_CURRENCY_KEY);
        editor.remove(Constants.OUTPUT_CURRENCY_KEY);
        editor.remove(Constants.CONVERSION_RATE_KEY);
        editor.apply();
    }

    public void addToPreference(String inputCurrency, String outputCurrency) {
        if (inputCurrency.equals(outputCurrency)) {
            Toast.makeText(getApplicationContext(), "You can't set " + inputCurrency + " -> " + outputCurrency + " conversion value", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.CONVERSION_RATE_KEY, etConversionValue.getText().toString());
            editor.putString(Constants.INPUT_CURRENCY_KEY, inputCurrency);
            editor.putString(Constants.OUTPUT_CURRENCY_KEY, outputCurrency);
            editor.apply();
            Toast.makeText(getApplicationContext(), "Set successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            finish();
        } else {
            if (sharedPreferences.contains(Constants.INPUT_CURRENCY_KEY)) {
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            } else {
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
            }
        }


        super.onBackPressed();
    }
}
