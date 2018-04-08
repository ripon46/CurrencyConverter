package com.therap.amin.currencyconverter;

import android.app.Application;
import android.content.Context;

import com.therap.amin.currencyconverter.component.AppComponent;
import com.therap.amin.currencyconverter.component.DaggerAppComponent;
import com.therap.amin.currencyconverter.module.ApplicationModule;
import com.therap.amin.currencyconverter.module.FragmentModule;

/**
 * @author Ripon
 */

public class CurrencyConversionApplication extends Application {

    public static AppComponent appComponent;

    public static CurrencyConversionApplication get(Context context) {
        return (CurrencyConversionApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    public static AppComponent getComponent() {
        return appComponent;
    }
}
