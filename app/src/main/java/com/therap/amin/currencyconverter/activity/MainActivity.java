package com.therap.amin.currencyconverter.activity;


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
import com.therap.amin.currencyconverter.interfaces.ConversionFragmentViewInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityViewInterface;
import com.therap.amin.currencyconverter.interfaces.ValueSaverFragmentViewInterface;
import com.therap.amin.currencyconverter.module.ActivityModule;
import com.therap.amin.currencyconverter.module.FragmentModule;
import com.therap.amin.currencyconverter.presenter.MainActivityPresenter;
import com.therap.amin.currencyconverter.service.FileProcessor;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity implements MainActivityViewInterface {

    private AppComponent activityComponent;

    @Inject
    MainActivityPresenterInterface mainActivityPresenter;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    FileProcessor fileProcessor;

    CurrencyConversionFragment currencyConversionFragment;

    CurrencyValueSaverFragment currencyValueSaverFragment;


    Menu menu;

    public AppComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerAppComponent.builder()
                    .applicationComponent(CurrencyConversionApplication.get(this).getComponent())
                    .activityModule(new ActivityModule(this))
                    .fragmentModule(new FragmentModule(this))
                    .build();
        }

        return activityComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currencyConversionFragment = new CurrencyConversionFragment();
        currencyValueSaverFragment = new CurrencyValueSaverFragment();

        getActivityComponent().inject(this);
        getActivityComponent().inject(mainActivityPresenter);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_currency_rates:
                return mainActivityPresenter.setCurrencyRatesClicked();
            case R.id.load_currency_rates:
                return mainActivityPresenter.loadCurrencyRatesClicked();
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    replaceFragment(new CurrencyConversionFragment());
                    setMenus(new CurrencyConversionFragment());
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
        if (isTablet()) {
            replaceFragment(new CurrencyConversionFragment());
            setMenus(new CurrencyConversionFragment());
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
            currencyConversionFragment.updateUI();
            currencyValueSaverFragment.updateUI();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentcontainer);
            if (fragment instanceof CurrencyValueSaverFragment) {
                currencyConversionFragment.updateUI();
            } else {
                currencyValueSaverFragment.updateUI();
            }
        }
    }
}

