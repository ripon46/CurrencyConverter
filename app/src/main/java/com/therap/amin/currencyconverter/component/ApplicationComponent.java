package com.therap.amin.currencyconverter.component;

import android.content.Context;
import android.content.SharedPreferences;

import com.therap.amin.currencyconverter.CurrencyConversionApplication;
import com.therap.amin.currencyconverter.module.ApplicationModule;
import com.therap.amin.currencyconverter.presenter.CurrencyConversionPresenter;
import com.therap.amin.currencyconverter.service.FileProcessor;

import dagger.Component;

/**
 * @author Ripon
 */

@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(CurrencyConversionApplication currencyConversionApplication);

    Context getContext();

    SharedPreferences getSharedPreferences();
}
