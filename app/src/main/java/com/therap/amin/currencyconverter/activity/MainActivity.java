package com.therap.amin.currencyconverter.activity;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.CurrencyConversionApplication;
import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.R;
import com.therap.amin.currencyconverter.component.ActivityComponent;
import com.therap.amin.currencyconverter.component.DaggerActivityComponent;
import com.therap.amin.currencyconverter.service.CurrencyConverterService;
import com.therap.amin.currencyconverter.service.FileProcessor;

import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {

    @Inject
    FileProcessor fileProcessor;

    boolean tabletSize;
    Menu menu;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    CurrencyConverterService service;

    private ActivityComponent activityComponent;

    @Inject
    CurrencyConversionFragment currencyConversionFragment;

    @Inject
    CurrencyValueSaverFragment currencyValueSaverFragment;

    @Inject
    ArrayList<Integer> integers;

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .applicationComponent(CurrencyConversionApplication.get(this).getComponent())
                    .build();
        }
        return activityComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabletSize = getResources().getBoolean(R.bool.isTablet);

        getActivityComponent().inject(this);
        Toast.makeText(getApplicationContext(), integers.size()+"", Toast.LENGTH_LONG).show();

        if (tabletSize) {
            setContentView(R.layout.activity_main_tab);
        } else {
            setContentView(R.layout.activity_main);

            Log.d(Constants.TAG, "onCreate: ");
            replaceFragment(currencyConversionFragment);
            if (fileProcessor.values.isEmpty() && sharedPreferences.getAll().isEmpty()) {
                replaceFragment(currencyValueSaverFragment);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        if (tabletSize) {
            MenuItem item = menu.findItem(R.id.set_currency_rates);
            item.setVisible(false);
            this.invalidateOptionsMenu();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentcontainer);
            setMenus(fragment);
            this.invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_currency_rates:
                replaceFragment(new CurrencyValueSaverFragment());
                setMenus(new CurrencyValueSaverFragment());
                return true;

            case R.id.load_currency_rates:
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                service.retreiveData(Constants.URL, null, new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        progressDialog.dismiss();
                        if (msg.what == Constants.SUCCESS) {
                            JSONObject response = (JSONObject) msg.obj;
                            Toast.makeText(MainActivity.this, "Successfully Loaded ", Toast.LENGTH_LONG).show();
                            fileProcessor.writeToFile(response.toString());
                            callUpdateUI();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed Loading Currency Rates", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentcontainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (tabletSize) {
            super.onBackPressed();
        } else {
            if (fileProcessor.values.isEmpty() && sharedPreferences.getAll().isEmpty()) {
                super.onBackPressed();
            } else {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentcontainer);
                if (fragment instanceof CurrencyValueSaverFragment) {
                    replaceFragment(new CurrencyConversionFragment());
                    setMenus(new CurrencyConversionFragment());
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public void setMenus(Fragment fragment) {
        MenuItem loadRates = menu.findItem(R.id.load_currency_rates);
        MenuItem setRates = menu.findItem(R.id.set_currency_rates);
        if (fragment instanceof CurrencyValueSaverFragment) {
            loadRates.setVisible(true);
            setRates.setVisible(false);
        } else {
            loadRates.setVisible(false);
            setRates.setVisible(true);
        }
    }

    public void callUpdateUI() {
        Log.d(Constants.TAG, "callUpdateUI: ");
        if (tabletSize) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.currencyconversionfragment);
            ((CurrencyConversionFragment) fragment).updateUI();
            fragment = getSupportFragmentManager().findFragmentById(R.id.valuesaverfragment);
            ((CurrencyValueSaverFragment) fragment).updateUI();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentcontainer);
            if (fragment instanceof CurrencyValueSaverFragment) {
                ((CurrencyValueSaverFragment) fragment).updateUI();
            } else {
                ((CurrencyConversionFragment) fragment).updateUI();
            }
        }
    }

}

