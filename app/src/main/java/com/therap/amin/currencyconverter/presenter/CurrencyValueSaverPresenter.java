package com.therap.amin.currencyconverter.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.CurrencyConversionApplication;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.activity.MainActivity;
import com.therap.amin.currencyconverter.component.AppComponent;
import com.therap.amin.currencyconverter.component.DaggerAppComponent;
import com.therap.amin.currencyconverter.interfaces.MainActivityViewInterface;
import com.therap.amin.currencyconverter.interfaces.ValueSaverPresenterInterface;
import com.therap.amin.currencyconverter.module.ActivityModule;
import com.therap.amin.currencyconverter.module.FragmentModule;
import com.therap.amin.currencyconverter.service.FileProcessor;

import javax.inject.Inject;

/**
 * @author Ripon
 */

public class CurrencyValueSaverPresenter implements ValueSaverPresenterInterface {

    Context context;

    @Inject
    FileProcessor fileProcessor;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    MainActivityViewInterface mainActivityViewInterface;

    CurrencyValueSaverFragment valueSaverFragmentViewInterface;

    private AppComponent fragmentComponent;

    public AppComponent getFragmentComponent() {
        if (fragmentComponent == null) {
            fragmentComponent = DaggerAppComponent.builder()
                    .applicationComponent(CurrencyConversionApplication.get(context).getComponent())
                    .fragmentModule(new FragmentModule((MainActivity) context))
                    .activityModule(new ActivityModule((MainActivity) context))
                    .build();
        }
        return fragmentComponent;
    }

    public CurrencyValueSaverPresenter() {

    }

    @Override
    public void saveButtonClick(String conversionValue, String inputCurrency, String outputCurrency) {
        if (!conversionValue.equals("")) {
            if (!inputCurrency.equals(outputCurrency)) {
                sharedPreferences.edit().putString(inputCurrency + outputCurrency, conversionValue).apply();
                sharedPreferences.edit().putString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, inputCurrency).apply();
                sharedPreferences.edit().putString(Constants.LAST_SAVED_TO_CURRENCY_KEY, outputCurrency).apply();

                mainActivityViewInterface.conversionRateSavedSuccessfully();
            } else {
                mainActivityViewInterface.showToastMessage("You cant set " + inputCurrency + "->" + outputCurrency + " conversion value");
            }
        } else {
            mainActivityViewInterface.showToastMessage("Please give input correctly");
        }
    }

    @Override
    public void setView(CurrencyValueSaverFragment view, Activity context) {
        valueSaverFragmentViewInterface = view;
        this.context = context;
    }

    @Override
    public void calculateConversionRate(String from, String to) {
        Double conversionRate = fileProcessor.calculateConversionRate(from, to);

        valueSaverFragmentViewInterface.setPresentCurrencyRelationText(conversionRate, from, to);
        valueSaverFragmentViewInterface.setConversionValue(sharedPreferences.getString(from + to, ""));
    }

    @Override
    public void saveToPreference(String from, String to) {
        boolean canPreferenceBeEditedFromThisFragment = sharedPreferences.contains(Constants.LAST_SAVED_FROM_CURRENCY_KEY) || !fileProcessor.values.isEmpty();
        if (canPreferenceBeEditedFromThisFragment) {
            sharedPreferences.edit().putString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, from).apply();
            sharedPreferences.edit().putString(Constants.LAST_SAVED_TO_CURRENCY_KEY, to).apply();
        }
    }
}
