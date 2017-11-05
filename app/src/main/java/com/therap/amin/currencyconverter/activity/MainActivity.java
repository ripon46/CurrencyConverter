package com.therap.amin.currencyconverter.activity;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.therap.amin.currencyconverter.component.AppComponent;
import com.therap.amin.currencyconverter.component.DaggerAppComponent;
import com.therap.amin.currencyconverter.interfaces.MainActivityPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityViewInterface;
import com.therap.amin.currencyconverter.module.ActivityModule;
import com.therap.amin.currencyconverter.module.ApplicationModule;
import com.therap.amin.currencyconverter.module.FragmentModule;
import com.therap.amin.currencyconverter.service.FileProcessor;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity implements MainActivityViewInterface {

    @Inject
    MainActivityPresenterInterface mainActivityPresenter;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    FileProcessor fileProcessor;

    @Inject
    CurrencyConversionFragment currencyConversionFragment;

    @Inject
    CurrencyValueSaverFragment currencyValueSaverFragment;

    @Inject
    ProgressDialog progressDialog;

    AppComponent appComponent;
    Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appComponent = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(getApplication()))
                .fragmentModule(new FragmentModule(getApplication()))
                .activityModule(new ActivityModule(this))
                .build();
        appComponent.inject(this);

        mainActivityPresenter.setView(this);

        if (isTablet()) {
            setContentView(R.layout.activity_main_tab);
        } else {
            setContentView(R.layout.activity_main);

            replaceFragment(currencyConversionFragment);
            if (fileProcessor.values.isEmpty() && sharedPreferences.getAll().isEmpty()) {
                replaceFragment(currencyValueSaverFragment);
            }
        }
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentcontainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        if (isTablet()) {
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

    @Override
    public void dismissDialog() {
        progressDialog.dismiss();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_currency_rates:
                return mainActivityPresenter.setCurrencyRatesClicked();
            case R.id.load_currency_rates:
                showProgressDiallog("Loading");
                return mainActivityPresenter.loadCurrencyRatesClicked();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showProgressDiallog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isTablet()) {
            super.onBackPressed();
        } else {
            if (fileProcessor.values.isEmpty() && sharedPreferences.getAll().isEmpty()) {
                super.onBackPressed();
            } else {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentcontainer);
                if (fragment instanceof CurrencyValueSaverFragment) {
                    replaceFragment(currencyConversionFragment);
                    setMenus(currencyConversionFragment);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    @Override
    public void conversionRateSavedSuccessfully() {
        if (!isTablet()) {
            replaceFragment(currencyConversionFragment);
            setMenus(currencyConversionFragment);
        }
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateUI() {
        Log.d(Constants.TAG, "callUpdateUI: ");
        if (isTablet()) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.currencyconversionfragment);
            ((CurrencyConversionFragment) fragment).updateUI();
            fragment = getSupportFragmentManager().findFragmentById(R.id.valuesaverfragment);
            ((CurrencyValueSaverFragment) fragment).updateUI();
//            currencyConversionFragment.updateUI();
//            currencyValueSaverFragment.updateUI();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentcontainer);
            if (fragment instanceof CurrencyConversionFragment) {
                //currencyConversionFragment.updateUI();
                ((CurrencyConversionFragment) fragment).updateUI();
            } else {
                //currencyValueSaverFragment.updateUI();
                ((CurrencyValueSaverFragment) fragment).updateUI();
            }
        }
    }
}

