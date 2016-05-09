package com.therap.amin.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by amin on 5/3/16.
 */
public class CurrencyValueSaver extends AppCompatActivity {

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

        sharedPreferences = getSharedPreferences(Constants.preferenceKey, Context.MODE_PRIVATE);

        ArrayAdapter<String> inputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
        inputCurrencySpinner.setAdapter(inputCurrencyAdapter);

        ArrayAdapter<String> outputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
        outputCurrencySpinner.setAdapter(outputCurrencyAdapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etConversionValue.getText().toString().equals("")) {

                    String inputCurrency = inputCurrencySpinner.getSelectedItem().toString();
                    String outputCurrency = outputCurrencySpinner.getSelectedItem().toString();
                    if (inputCurrency.equals(outputCurrency)) {
                        Toast.makeText(getApplicationContext(), "You can't set " + inputCurrency + " -> " + outputCurrency + " conversion value", Toast.LENGTH_SHORT).show();
                    } else if (sharedPreferences.contains(Constants.inputCurrencyKey)) {
                        removeFromPreference();
                        addToPreference(inputCurrency,outputCurrency);
                    } else {
                        addToPreference(inputCurrency,outputCurrency);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please give input correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeFromPreference () {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.inputCurrencyKey);
        editor.remove(Constants.outputCurrencyKey);
        editor.remove(Constants.conversionRateKey);
        editor.apply();
    }

    public void addToPreference (String inputCurrency,String outputCurrency) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Constants.conversionRateKey, Float.parseFloat(etConversionValue.getText().toString()));
        editor.putString(Constants.inputCurrencyKey, inputCurrency);
        editor.putString(Constants.outputCurrencyKey, outputCurrency);
        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
        editor.apply();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent it = new Intent(CurrencyValueSaver.this, MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(it);
        finish();
    }
}
