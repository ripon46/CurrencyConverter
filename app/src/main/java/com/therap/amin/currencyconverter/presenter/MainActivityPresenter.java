package com.therap.amin.currencyconverter.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.CurrencyConversionApplication;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.activity.MainActivity;
import com.therap.amin.currencyconverter.component.AppComponent;
import com.therap.amin.currencyconverter.component.DaggerAppComponent;
import com.therap.amin.currencyconverter.interfaces.MainActivityPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityViewInterface;
import com.therap.amin.currencyconverter.module.ActivityModule;
import com.therap.amin.currencyconverter.module.FragmentModule;
import com.therap.amin.currencyconverter.service.CurrencyConverterService;
import com.therap.amin.currencyconverter.service.FileProcessor;

import org.json.JSONObject;

import javax.inject.Inject;

/**
 * @author Ripon
 */

public class MainActivityPresenter implements MainActivityPresenterInterface {

    @Inject
    FileProcessor fileProcessor;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    CurrencyConverterService service;

    @Inject
    MainActivityViewInterface mainActivity;

    Context context;

    public MainActivityPresenter(Context context) {
        this.context = context;
    }

    @Override
    public boolean setCurrencyRatesClicked() {
        mainActivity.replaceFragment(new CurrencyValueSaverFragment());
        mainActivity.setMenus(new CurrencyValueSaverFragment());
        return true;

    }

    @Override
    public boolean loadCurrencyRatesClicked() {

        service.retreiveData(Constants.URL, null, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Constants.SUCCESS) {
                    JSONObject response = (JSONObject) msg.obj;
                    mainActivity.showToastMessage("Successfully Loaded ");
                    fileProcessor.writeToFile(response.toString());
                    mainActivity.updateUI();
                } else {
                    mainActivity.showToastMessage("Failed Loading Currency Rates");
                }
            }
        });

        return true;
    }
}
