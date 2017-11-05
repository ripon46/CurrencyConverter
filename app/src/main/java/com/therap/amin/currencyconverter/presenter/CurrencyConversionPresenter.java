package com.therap.amin.currencyconverter.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.CurrencyConversionApplication;
import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.interfaces.ConversionPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityViewInterface;
import com.therap.amin.currencyconverter.service.FileProcessor;

import javax.inject.Inject;

/**
 * @author Ripon
 */

public class CurrencyConversionPresenter implements ConversionPresenterInterface {

    MainActivityViewInterface mainActivityViewInterface;

    CurrencyConversionFragment conversionFragmentViewInterface;

    @Inject
    SharedPreferences sharedPreferences;

    Context context;

    @Inject
    FileProcessor fileProcessor;

    private String ownSavedValue = "Own";
    private String webFetchedValue = "Web";

    public CurrencyConversionPresenter() {
        CurrencyConversionApplication.getComponent().inject(this);
    }

    @Override
    public void setView(CurrencyConversionFragment view, Activity context) {
        this.context = context;
        this.conversionFragmentViewInterface = view;
        this.mainActivityViewInterface = (MainActivityViewInterface) context;
    }

    @Override
    public void calculateConversionRate(String from, String to, String selectedSource) {
        Double conversionRate = null;
        if (selectedSource.equals(ownSavedValue)) {
            String valueFromPreference = sharedPreferences.getString(from + to, null);
            if (valueFromPreference != null)
                conversionRate = Double.parseDouble(valueFromPreference);
        } else if (selectedSource.equals(webFetchedValue)) {
            if (fileProcessor.calculateConversionRate(from, to) != -1) {
                conversionRate = fileProcessor.calculateConversionRate(from, to);
            }
        }
        conversionFragmentViewInterface.updateTextViews(conversionRate, from, to);
    }

    @Override
    public void saveToPreference(String from, String to) {
        sharedPreferences.edit().putString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, from).apply();
        sharedPreferences.edit().putString(Constants.LAST_SAVED_TO_CURRENCY_KEY, to).apply();
    }
}
