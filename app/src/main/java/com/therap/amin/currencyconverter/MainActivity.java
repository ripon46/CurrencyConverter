package com.therap.amin.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ripon";
    TextView output,inputCurrency;
    EditText input;
    String key;
    String currencies[];

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        output = (TextView) findViewById(R.id.tvOutputCurrency);
        inputCurrency = (TextView) findViewById(R.id.tvInputCurrency);
        input = (EditText) findViewById(R.id.etCurrencyAmount);


        sharedPreferences = getSharedPreferences("currencyPreference", Context.MODE_PRIVATE);
        Toast.makeText(getApplicationContext(),"This is changed",Toast.LENGTH_LONG).show();


        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    double convertedAmount = Double.parseDouble(s.toString());
                    float conversionVal = sharedPreferences.getFloat(key,0);
                    convertedAmount *= conversionVal;
                    //output.setText("Converted currency: "+ convertedAmount);
                    if (key != null) {
                        currencies = key.split("->");
                        output.setText("Converted currency in "+currencies[1]+": "+convertedAmount);
                    }

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
        switch (item.getItemId())
        {
            case R.id.menu_currencyvalues:
                Intent intent = new Intent(MainActivity.this,CurrencyValueSaver.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
        outState.putString("output",output.getText().toString());
        outState.putString("value",input.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        input.setText(savedInstanceState.getString("value"));
        output.setText(savedInstanceState.getString("output"));
        Log.d(TAG, "onRestoreInstanceState: ");
    }

    @Override
    protected void onResume() {
        super.onResume();


        Map<String, ?> allEntries = sharedPreferences.getAll();
        if (allEntries.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Please set value at first",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,CurrencyValueSaver.class);
            startActivity(intent);
        } else {
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Toast.makeText(getApplicationContext(),entry.getKey()+" is set",Toast.LENGTH_SHORT).show();
                key = entry.getKey();
            }
            currencies = key.split("->");
            inputCurrency.setText("Enter currency in "+currencies[0]);
            //output.setText("Converted currency in "+currencies[1]+": "+" 0");
        }
        Log.d(TAG, "onResume: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}

