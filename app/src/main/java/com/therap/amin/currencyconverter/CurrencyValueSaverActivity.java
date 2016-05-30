package com.therap.amin.currencyconverter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import roboguice.RoboGuice;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;


/**
 * Created by amin on 5/3/16.
 */

@ContentView(R.layout.currencyvaluesaver)
public class CurrencyValueSaverActivity extends RoboActionBarActivity {

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
    SharedPreferences sharedPreferences;
    @Inject
    FileProcessor fileProcessor;
    @InjectView(R.id.tvPresentCurrencyRelation)
    TextView tvPresentCurrencyRelation;
    ArrayAdapter<String> inputCurrencyAdapter, outputCurrencyAdapter;

    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Set Currency Rates");
        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

        inputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
        inputCurrencySpinner.setAdapter(inputCurrencyAdapter);
        outputCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableCurrencies);
        outputCurrencySpinner.setAdapter(outputCurrencyAdapter);

        Intent intent = getIntent();
        String from = intent.getStringExtra(Constants.FROM_CURRENCY_KEY);
        String to = intent.getStringExtra(Constants.TO_CURRENCY_KEY);

        inputCurrencySpinner.setSelection(inputCurrencyAdapter.getPosition(from));
        outputCurrencySpinner.setSelection(outputCurrencyAdapter.getPosition(to));

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
                        Toast.makeText(getApplicationContext(), "Set successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent();
                        i.putExtra(Constants.FROM_CURRENCY_KEY, inputCurrencySpinner.getSelectedItem().toString());
                        i.putExtra(Constants.TO_CURRENCY_KEY, outputCurrencySpinner.getSelectedItem().toString());
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "You cant set " + inputCurrency + "->" + outputCurrency + " conversion value", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please give input correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fileProcessor.values.isEmpty() && sharedPreferences.getAll().isEmpty()) {
            Intent i = new Intent();
            setResult(RESULT_CANCELED, i);
            finish();
        } else {
            Intent i = new Intent();
            i.putExtra(Constants.FROM_CURRENCY_KEY, inputCurrencySpinner.getSelectedItem().toString());
            i.putExtra(Constants.TO_CURRENCY_KEY, outputCurrencySpinner.getSelectedItem().toString());
            setResult(RESULT_OK, i);
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuforcurrencyvaluesaver, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load:
                final ProgressDialog progressDialog = new ProgressDialog(CurrencyValueSaverActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                FetchCurrencyValues.get(Constants.URL, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        progressDialog.dismiss();
                        if (response.toString().isEmpty()) {
                            Toast.makeText(CurrencyValueSaverActivity.this, "Failed Loading Currency Values", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CurrencyValueSaverActivity.this, "Successfully Loaded", Toast.LENGTH_LONG).show();
                            FileProcessor fileProcessor = new FileProcessor(CurrencyValueSaverActivity.this);
                            fileProcessor.writeToFile(response.toString());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        progressDialog.dismiss();
                        Toast.makeText(CurrencyValueSaverActivity.this, "Failed Loading Currency Values", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
