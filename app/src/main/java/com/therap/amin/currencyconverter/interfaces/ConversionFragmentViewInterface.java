package com.therap.amin.currencyconverter.interfaces;

/**
 * Created by amin on 5/11/17.
 */

public interface ConversionFragmentViewInterface {
    void updateTextViews(Double conversionRate, String from, String to);

    void updateUI();
}
