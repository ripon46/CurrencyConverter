package com.therap.amin.currencyconverter.interfaces;

import android.app.Activity;

import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;

/**
 * @author Ripon
 */

public interface ValueSaverPresenterInterface {
    void calculateConversionRate(String from, String to);
    void saveToPreference(String from, String to);
    void saveButtonClick(String conversionValue, String inputCurrency, String outputCurrency);
    void setView(CurrencyValueSaverFragment view, Activity context);
}
