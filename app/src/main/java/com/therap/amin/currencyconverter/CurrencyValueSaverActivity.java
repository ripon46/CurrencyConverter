package com.therap.amin.currencyconverter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
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
    Map<String, Double> values;
    FileProcessor fileProcessor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currencyvaluesaver);

        values = new HashMap<String, Double>();
        fileProcessor = new FileProcessor(CurrencyValueSaverActivity.this);
        values = fileProcessor.readFileAndProcess();


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

        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        String to = intent.getStringExtra("to");
        String conversionValue = intent.getStringExtra("conversionVal");

        int fromCurrencyposition = inputCurrencyAdapter.getPosition(from);
        inputCurrencySpinner.setSelection(fromCurrencyposition);
        int toCurrencyposition = inputCurrencyAdapter.getPosition(to);
        outputCurrencySpinner.setSelection(toCurrencyposition);
        etConversionValue.setText(conversionValue);

        inputCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String from = inputCurrencySpinner.getSelectedItem().toString();
                String to = outputCurrencySpinner.getSelectedItem().toString();
                Double v = values.get(from + to);
                if (v != null) {
                    etConversionValue.setText(v + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        outputCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String from = inputCurrencySpinner.getSelectedItem().toString();
                String to = outputCurrencySpinner.getSelectedItem().toString();
                Double v = values.get(from + to);
                if (v != null) {
                    etConversionValue.setText(v + "");
                }
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
                        //sharedPreferences.edit().clear().apply();
                        sharedPreferences.edit().putString(inputCurrency+outputCurrency,etConversionValue.getText().toString()).apply();

                        Toast.makeText(getApplicationContext(), "Set successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"You cant set " + inputCurrency+ "->" +outputCurrency +" conversion value",Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please give input correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            finish();
        } else {
            if (values.isEmpty() && sharedPreferences.getAll().isEmpty()) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
            } else {
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            return false;
        } else {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menuforcurrencyvaluesaver, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.action_search:
                    FetchCurrencyValues fetchCurrencyValues = new FetchCurrencyValues(CurrencyValueSaverActivity.this);
                    fetchCurrencyValues.fetch(Constants.URL);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }
}
