package com.therap.amin.currencyconverter;

import android.app.Application;
import android.content.Context;

import com.therap.amin.currencyconverter.component.ApplicationComponent;
import com.therap.amin.currencyconverter.component.DaggerApplicationComponent;
import com.therap.amin.currencyconverter.module.ApplicationModule;

/**
 * @author Ripon
 */

public class CurrencyConversionApplication extends Application {

    private ApplicationComponent applicationComponent;

    public static CurrencyConversionApplication get(Context context) {
        return (CurrencyConversionApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }
}
