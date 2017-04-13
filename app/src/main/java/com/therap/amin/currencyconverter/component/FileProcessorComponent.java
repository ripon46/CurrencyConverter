package com.therap.amin.currencyconverter.component;

import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.MainActivity;
import com.therap.amin.currencyconverter.module.FileProcessorModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ripon
 */
@Singleton
@Component(modules = {FileProcessorModule.class})
public interface FileProcessorComponent {
    void inject(MainActivity mainActivity);

    void inject(CurrencyConversionFragment currencyConversionFragment);

    void inject(CurrencyValueSaverFragment currencyValueSaverFragment);
}
