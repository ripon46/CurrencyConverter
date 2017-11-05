package com.therap.amin.currencyconverter.interfaces;

import android.app.Activity;

/**
 * @author Ripon
 */

public interface MainActivityPresenterInterface {
    boolean setCurrencyRatesClicked();

    boolean loadCurrencyRatesClicked();

    void setView(Activity context);
}
