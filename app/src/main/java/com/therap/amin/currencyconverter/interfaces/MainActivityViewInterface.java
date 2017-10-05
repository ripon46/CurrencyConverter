package com.therap.amin.currencyconverter.interfaces;

import android.support.v4.app.Fragment;

/**
 * @author Ripon
 */

public interface MainActivityViewInterface {

    void conversionRateSavedSuccessfully();

    void showToastMessage(String message);

    void updateUI();

    void replaceFragment(Fragment fragment);

    void setMenus(Fragment fragment);
}
