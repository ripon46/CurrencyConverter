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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TextView tvOutputCurrency,tvOutputCurrencyValue, tvInputCurrency;
    EditText etInputCurrencyValue;

    boolean onresumeExecution;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onresumeExecution = true;
        Log.d(Constants.TAG, "onCreate: ");

        tvOutputCurrency = (TextView) findViewById(R.id.tvOutputCurrency);
        tvOutputCurrencyValue = (TextView) findViewById(R.id.tvOutputCurrencyValue);
        tvInputCurrency = (TextView) findViewById(R.id.tvInputCurrency);
        etInputCurrencyValue = (EditText) findViewById(R.id.etCurrencyAmount);
        tvOutputCurrencyValue.setText("0");

        sharedPreferences = getSharedPreferences(Constants.preferenceKey, Context.MODE_PRIVATE);

        etInputCurrencyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    double convertedAmount = Double.parseDouble(s.toString());
                    float conversionVal = sharedPreferences.getFloat(Constants.conversionRateKey, 0);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_currencyvalues:
                Intent intent = new Intent(MainActivity.this, CurrencyValueSaver.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean insideOnResumeForFirstTime = !sharedPreferences.contains(Constants.inputCurrencyKey) && onresumeExecution;
        boolean insideOnResumeForSecondTimeButNoCurrencySaved = !sharedPreferences.contains(Constants.inputCurrencyKey) && !onresumeExecution;
        if (insideOnResumeForFirstTime) {
            onresumeExecution = false;
            Toast.makeText(getApplicationContext(), "Please set value at first", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CurrencyValueSaver.class);
            startActivity(intent);
        } else if (insideOnResumeForSecondTimeButNoCurrencySaved) {
            finish();
        } else {
            tvInputCurrency.setText(sharedPreferences.getString(Constants.inputCurrencyKey, ""));
            tvOutputCurrency.setText(sharedPreferences.getString(Constants.outputCurrencyKey, ""));
        }

        Log.d(Constants.TAG, "onResume: ");
    }
}

