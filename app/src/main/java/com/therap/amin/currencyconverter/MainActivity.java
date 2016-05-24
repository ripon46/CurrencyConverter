package com.therap.amin.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {

    String[] availableCurrencies;
    TextView tvOutputCurrencyValue, tvSavedCurrencyRate;
    EditText etInputCurrencyValue;
    Spinner fromCurrencySpinner, toCurrencySpinner;
    FileProcessor fileProcessor;
    SharedPreferences sharedPreferences;
    RadioGroup sourceRadioGroup;
    RadioButton webRadioButton, ownvalueRadioButton;
    String selectedSource = "web";
    boolean tabletSize;
    ArrayAdapter<String> inputCurrencyAdapter, outputCurrencyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            setContentView(R.layout.activity_main_tab);
        } else {
            setContentView(R.layout.activity_main);
            Log.d(Constants.TAG, "onCreate: ");
            fileProcessor = new FileProcessor(MainActivity.this);
            sharedPreferences = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

            sourceRadioGroup = (RadioGroup) findViewById(R.id.rgSource);
            webRadioButton = (RadioButton) findViewById(R.id.rbWeb);
            ownvalueRadioButton = (RadioButton) findViewById(R.id.rbOwn);

            if (fileProcessor.values.isEmpty() && !sharedPreferences.contains("USDBDT")) {
                Intent intent = new Intent(MainActivity.this, CurrencyValueSaverActivity.class);
                startActivityForResult(intent, 1);
            }

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
            tvOutputCurrencyValue = (TextView) findViewById(R.id.tvOutputCurrencyValue);
            etInputCurrencyValue = (EditText) findViewById(R.id.etCurrencyAmount);
            tvOutputCurrencyValue.setText("0");
            tvSavedCurrencyRate = (TextView) findViewById(R.id.tvSavedCurrency);
            fromCurrencySpinner = (Spinner) findViewById(R.id.spnFromCurrency);
            toCurrencySpinner = (Spinner) findViewById(R.id.spnToCurrency);

            inputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
            fromCurrencySpinner.setAdapter(inputCurrencyAdapter);
            outputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
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
                        String from = fromCurrencySpinner.getSelectedItem().toString();
                        String to = toCurrencySpinner.getSelectedItem().toString();
                        double conversionVal = 0;
                        if (selectedSource.equals("own")) {
                            String val = sharedPreferences.getString(from + to, null);
                            if (val != null) {
                                conversionVal = Double.parseDouble(val);
                            }
                        } else if (fileProcessor.calculateConversionRate(from, to) != -1) {
                            conversionVal = fileProcessor.calculateConversionRate(from, to);
                        }
                        double convertedAmount = Double.parseDouble(s.toString());
                        convertedAmount *= conversionVal;
                        tvOutputCurrencyValue.setText(String.format("%.2f", convertedAmount));
                    } else {
                        tvOutputCurrencyValue.setText("0");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (tabletSize) {
            return false;
        } else {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.main, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (tabletSize) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.menu_currencyvalues:
                    Intent intent = new Intent(MainActivity.this, CurrencyValueSaverActivity.class);
                    String from = fromCurrencySpinner.getSelectedItem().toString();
                    String to = toCurrencySpinner.getSelectedItem().toString();
                    intent.putExtra(Constants.FROM_CURRENCY_KEY, from);
                    intent.putExtra(Constants.TO_CURRENCY_KEY, to);
                    startActivityForResult(intent, 1);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!tabletSize) {
            if (requestCode == 1) {
                if (resultCode != RESULT_OK) {
                    finish();
                } else {
                    fromCurrencySpinner.setSelection(inputCurrencyAdapter.getPosition(data.getStringExtra(Constants.FROM_CURRENCY_KEY)));
                    toCurrencySpinner.setSelection(outputCurrencyAdapter.getPosition(data.getStringExtra(Constants.TO_CURRENCY_KEY)));
                    updateUI();
                }
            }
            Log.d(Constants.TAG, "onActivityResult: ");
        }
    }

    public void updateUI() {
        String from = fromCurrencySpinner.getSelectedItem().toString();
        String to = toCurrencySpinner.getSelectedItem().toString();

        fileProcessor = new FileProcessor(MainActivity.this);
        Double conversionRate = null;
        if (selectedSource.equals("own")) {
            String valueFromPreference = sharedPreferences.getString(from + to, null);
            if (valueFromPreference != null)
                conversionRate = Double.parseDouble(valueFromPreference);
        } else if (selectedSource.equals("web")) {
            conversionRate = fileProcessor.calculateConversionRate(from, to);
        }
        if (conversionRate != null && conversionRate != -1) {
            tvSavedCurrencyRate.setText(String.format("1 %s = %.2f %s", from, conversionRate, to));
            if (!etInputCurrencyValue.getText().toString().equals("")) {
                conversionRate *= Double.parseDouble(etInputCurrencyValue.getText().toString());
                tvOutputCurrencyValue.setText(String.format("%.2f", conversionRate));
            }
        } else {
            tvSavedCurrencyRate.setText(R.string.not_available);
        }
    }

    @Override
    public void onBackPressed() {
        if (!tabletSize) {
            sharedPreferences.edit().putString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, fromCurrencySpinner.getSelectedItem().toString()).apply();
            sharedPreferences.edit().putString(Constants.LAST_SAVED_TO_CURRENCY_KEY, toCurrencySpinner.getSelectedItem().toString()).apply();
        }
        super.onBackPressed();
    }

}

