package com.therap.amin.currencyconverter.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
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

import com.google.inject.Inject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.FetchCurrencyValues;
import com.therap.amin.currencyconverter.FileProcessor;
import com.therap.amin.currencyconverter.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by amin on 5/10/16.
 */
public class CurrencyValueSaverFragment extends RoboFragment {

    @InjectResource(R.array.currencies)
    String[] availableCurrencies;

    @InjectView(R.id.spnLeftCurrency)
    Spinner inputCurrencySpinner;

    @InjectView(R.id.spnRightCurrency)
    Spinner outputCurrencySpinner;

    @InjectView(R.id.btnSave)
    Button saveButton;

    @InjectView(R.id.etRightCurrencyValue)
    EditText etConversionValue;

    @Inject
    FileProcessor fileProcessor;

    @InjectView(R.id.tvPresentCurrencyRelation)
    TextView tvPresentCurrencyRelation;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        ArrayAdapter<String> inputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        inputCurrencySpinner.setAdapter(inputCurrencyAdapter);
        ArrayAdapter<String> outputCurrencyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableCurrencies);
        outputCurrencySpinner.setAdapter(outputCurrencyAdapter);

        inputCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        outputCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currencyvaluesaver, container, false);
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);
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
            case R.id.action_load:
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                FetchCurrencyValues.get(Constants.URL, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        progressDialog.dismiss();
                        if (response.toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Failed Loading Currency Values", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Successfully Loaded", Toast.LENGTH_LONG).show();
                            fileProcessor.writeToFile(response.toString());
                            updateUI();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed Loading Currency Values", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            default:
                break;
        }
        return false;
    }

    public void updateUI() {
        String from = inputCurrencySpinner.getSelectedItem().toString();
        String to = outputCurrencySpinner.getSelectedItem().toString();
        Double conversionRate = fileProcessor.calculateConversionRate(from, to);

        if (conversionRate != -1) {
            tvPresentCurrencyRelation.setText(String.format("1 %s = %.2f %s", from, conversionRate, to));
        } else {
            tvPresentCurrencyRelation.setText(R.string.not_available);
        }
        etConversionValue.setText(sharedPreferences.getString(from + to, ""));
    }
}
