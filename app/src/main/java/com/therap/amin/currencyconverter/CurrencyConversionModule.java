package com.therap.amin.currencyconverter;

import com.google.inject.AbstractModule;

/**
 * Created by amin on 5/30/16.
 */
public class CurrencyConversionModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(ShowConversionRate.class);
    }
}
