package com.therap.amin.currencyconverter.module;

import android.os.Handler;

import com.therap.amin.currencyconverter.service.CurrencyConverterService;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class CurrencyConversionModule {

    Handler handler;


    public CurrencyConversionModule(Handler handler) {
        this.handler = handler;
    }

    @Provides
    CurrencyConverterService provideCurrencyConverterService() {
        return new CurrencyConverterService(handler);
    }
}
