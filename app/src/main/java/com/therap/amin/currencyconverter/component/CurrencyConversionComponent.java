package com.therap.amin.currencyconverter.component;

import com.therap.amin.currencyconverter.MainActivity;
import com.therap.amin.currencyconverter.module.CurrencyConversionModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ripon
 */
@Singleton
@Component(modules = {CurrencyConversionModule.class})
public interface CurrencyConversionComponent {
    void inject(MainActivity mainActivity);
}
