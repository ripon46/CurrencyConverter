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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String[] availableCurrencies;
    TextView tvOutputCurrencyValue, tvSavedCurrency;
    EditText etInputCurrencyValue;
    Spinner fromCurrencySpinner, toCurrencySpinner;
    public Map<String, Double> values;
    FileProcessor fileProcessor;
    SharedPreferences sharedPreferences;
    RadioGroup sourceRadioGroup;
    RadioButton webRadioButton, ownvalueRadioButton;
    String selectedSource = "web";
    boolean tabletSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            setContentView(R.layout.activity_main_tab);
        } else {
            setContentView(R.layout.activity_main);
            Log.d(Constants.TAG, "onCreate: ");
            values = new HashMap<String, Double>();
            fileProcessor = new FileProcessor(MainActivity.this);
            sharedPreferences = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

            sourceRadioGroup = (RadioGroup) findViewById(R.id.rgSource);
            webRadioButton = (RadioButton) findViewById(R.id.rbWeb);
            ownvalueRadioButton = (RadioButton) findViewById(R.id.rbOwn);

            values = fileProcessor.readFileAndProcess();
            if (values.isEmpty() && sharedPreferences.getAll().isEmpty()) {
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
            tvSavedCurrency = (TextView) findViewById(R.id.tvSavedCurrency);
            fromCurrencySpinner = (Spinner) findViewById(R.id.spnFromCurrency);
            toCurrencySpinner = (Spinner) findViewById(R.id.spnToCurrency);

            ArrayAdapter<String> inputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
            fromCurrencySpinner.setAdapter(inputCurrencyAdapter);
            ArrayAdapter<String> outputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
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
                            val = sharedPreferences.getString(from + to, null);
                            if (val != null) {
                                conversionVal = Double.parseDouble(val);
                            }
                        } else if (values.containsKey(from + to)) {
                            conversionVal = values.get(from + to);
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
                    intent.putExtra("from", from);
                    intent.putExtra("to", to);
                    intent.putExtra("conversionVal", values.get(from + to));
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
        values = fileProcessor.readFileAndProcess();
        Double valueFromWeb = null;
        String valueFromPreference = sharedPreferences.getString(from + to, null);
        if (valueFromPreference != null && selectedSource.equals("own")) {
            valueFromWeb = Double.parseDouble(valueFromPreference);
        } else if (selectedSource.equals("web")) {
            valueFromWeb = values.get(from + to);
        }
        if (valueFromWeb != null) {
            tvSavedCurrency.setText("1 " + from + " = " + valueFromWeb + " " + to);
        } else {
            tvSavedCurrency.setText("Not Set");
        }
    }

}

