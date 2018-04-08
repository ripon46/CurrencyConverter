package com.therap.amin.currencyconverter.interfaces;

import android.app.Activity;

import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;

/**
 * @author Ripon
 */

public interface ConversionPresenterInterface {
    void calculateConversionRate(String from, String to, String selectedSource);
    void saveToPreference(String from, String to);
    void setView(CurrencyConversionFragment currencyConversionFragment, Activity context);
}
