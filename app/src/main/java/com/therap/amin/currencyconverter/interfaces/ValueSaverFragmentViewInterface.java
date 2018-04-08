package com.therap.amin.currencyconverter.interfaces;

/**
 * @author Ripon
 */

public interface ValueSaverFragmentViewInterface {

    void setPresentCurrencyRelationText(Double conversionRate, String from, String to);
    void setConversionValue(String string);
    void updateUI();
}
