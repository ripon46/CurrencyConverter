package com.therap.amin.currencyconverter.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.R;

/**
 * Created by amin on 5/10/16.
 */
public class CurrencyValueSaverFragment extends Fragment{

    String[] availableCurrencies;
    Spinner inputCurrencySpinner, outputCurrencySpinner;
    Button saveButton;
    EditText etConversionValue;

    SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currencyvaluesaver,container,false);
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

        if (sharedPreferences.contains(Constants.INPUT_CURRENCY_KEY)) {
            String fromCurrency = sharedPreferences.getString(Constants.INPUT_CURRENCY_KEY,"");
            String toCurrency = sharedPreferences.getString(Constants.OUTPUT_CURRENCY_KEY,"");
            String conversionValue = sharedPreferences.getString(Constants.CONVERSION_RATE_KEY,"");
            int fromCurrencyposition = inputCurrencyAdapter.getPosition(fromCurrency);
            inputCurrencySpinner.setSelection(fromCurrencyposition);
            int toCurrencyposition = inputCurrencyAdapter.getPosition(toCurrency);
            outputCurrencySpinner.setSelection(toCurrencyposition);
            etConversionValue.setText(conversionValue);

        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etConversionValue.getText().toString().equals("")) {

                    String inputCurrency = inputCurrencySpinner.getSelectedItem().toString();
                    String outputCurrency = outputCurrencySpinner.getSelectedItem().toString();
                    if (sharedPreferences.contains(Constants.INPUT_CURRENCY_KEY)) {
                        removeFromPreference();
                        addToPreference(inputCurrency,outputCurrency);
                    } else {
                        addToPreference(inputCurrency,outputCurrency);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please give input correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    public void removeFromPreference () {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.INPUT_CURRENCY_KEY);
        editor.remove(Constants.OUTPUT_CURRENCY_KEY);
        editor.remove(Constants.CONVERSION_RATE_KEY);
        editor.apply();
    }

    public void addToPreference (String inputCurrency,String outputCurrency) {
        if (inputCurrency.equals(outputCurrency)) {
            Toast.makeText(getActivity(), "You can't set " + inputCurrency + " -> " + outputCurrency + " conversion value", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.CONVERSION_RATE_KEY, etConversionValue.getText().toString());
            editor.putString(Constants.INPUT_CURRENCY_KEY, inputCurrency);
            editor.putString(Constants.OUTPUT_CURRENCY_KEY, outputCurrency);
            editor.apply();
            Toast.makeText(getActivity(),"Set successfully",Toast.LENGTH_SHORT).show();
            TextView tvInputCurrency = (TextView) getActivity().findViewById(R.id.tvInputCurrency);
            TextView tvOutputCurrency = (TextView) getActivity().findViewById(R.id.tvOutputCurrency);
            TextView tvSavedCurrency = (TextView) getActivity().findViewById(R.id.tvSavedCurrency);
            tvInputCurrency.setText(sharedPreferences.getString(Constants.INPUT_CURRENCY_KEY, "Not Set"));
            tvOutputCurrency.setText(sharedPreferences.getString(Constants.OUTPUT_CURRENCY_KEY, "Not Set"));
            String fromCurrency = sharedPreferences.getString(Constants.INPUT_CURRENCY_KEY,"");
            String toCurrency = sharedPreferences.getString(Constants.OUTPUT_CURRENCY_KEY,"");
            String conversionValue = sharedPreferences.getString(Constants.CONVERSION_RATE_KEY,"");
            tvSavedCurrency.setText("1 "+fromCurrency+" = "+conversionValue+" "+toCurrency);
        }

    }

}
