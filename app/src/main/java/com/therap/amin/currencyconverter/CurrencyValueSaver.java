package com.therap.amin.currencyconverter;

import android.content.Context;
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

    String[] currencies,leftSideCurrencies;
    Spinner leftCurrency, rightCurrency;
    Button save;
    EditText conversionValue;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currencyvaluesaver);

        currencies = getResources().getStringArray(R.array.currencies);


        leftCurrency = (Spinner) findViewById(R.id.spnLeftCurrency);
        rightCurrency = (Spinner) findViewById(R.id.spnRightCurrency);

        save = (Button) findViewById(R.id.btnSave);

        conversionValue = (EditText) findViewById(R.id.etRightCurrencyValue);

        sharedPreferences = getSharedPreferences("currencyPreference", Context.MODE_PRIVATE);


        ArrayAdapter<String> leftCurrencyAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,currencies);
        leftCurrency.setAdapter(leftCurrencyAdapter);

        ArrayAdapter<String> rightCurrencyAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,currencies);
        rightCurrency.setAdapter(rightCurrencyAdapter);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!conversionValue.getText().toString().equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String left = leftCurrency.getSelectedItem().toString();
                    String right =rightCurrency.getSelectedItem().toString();
                    if (left.equals(right)) {
                        Toast.makeText(getApplicationContext(),"You can't set "+left+" -> "+right+" conversion value",Toast.LENGTH_SHORT).show();
                        conversionValue.getText().clear();
                    } else {
                        Map<String, ?> allEntries = sharedPreferences.getAll();
                        if (!allEntries.isEmpty()) {
                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                                editor.remove(entry.getKey());
                            }
                        }
                        String key = left +"->"+ right;
                        editor.putFloat(key,Float.parseFloat(conversionValue.getText().toString()));
                        editor.commit();
                        Toast.makeText(getApplicationContext(),"Saved Successfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"Please give input correctly",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
