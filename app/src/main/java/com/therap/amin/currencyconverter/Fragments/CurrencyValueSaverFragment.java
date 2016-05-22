package com.therap.amin.currencyconverter.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.FetchCurrencyValues;
import com.therap.amin.currencyconverter.FileProcessor;
import com.therap.amin.currencyconverter.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amin on 5/10/16.
 */
public class CurrencyValueSaverFragment extends Fragment {

    String[] availableCurrencies;
    Spinner inputCurrencySpinner, outputCurrencySpinner;
    Button saveButton;
    EditText etConversionValue;
    SharedPreferences sharedPreferences;
    Map<String, Double> values;
    FileProcessor fileProcessor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currencyvaluesaver, container, false);
        values = new HashMap<String, Double>();
        fileProcessor = new FileProcessor(getActivity());
        values = fileProcessor.readFileAndProcess();
        availableCurrencies = getResources().getStringArray(R.array.currencies);
        inputCurrencySpinner = (Spinner) view.findViewById(R.id.spnLeftCurrency);
        outputCurrencySpinner = (Spinner) view.findViewById(R.id.spnRightCurrency);
        saveButton = (Button) view.findViewById(R.id.btnSave);
        etConversionValue = (EditText) view.findViewById(R.id.etRightCurrencyValue);

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

        ArrayAdapter<String> inputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        inputCurrencySpinner.setAdapter(inputCurrencyAdapter);
        ArrayAdapter<String> outputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        outputCurrencySpinner.setAdapter(outputCurrencyAdapter);

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
                        sharedPreferences.edit().putString(inputCurrency + outputCurrency, etConversionValue.getText().toString()).apply();
                        Toast.makeText(getActivity(), "Set successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "You cant set " + inputCurrency + "->" + outputCurrency + " conversion value", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please give input correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menuforcurrencyvaluesaver, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                FetchCurrencyValues fetchCurrencyValues = new FetchCurrencyValues(getActivity());
                fetchCurrencyValues.fetch(Constants.URL);
                return true;
            default:
                break;
        }
        return false;
    }
}
