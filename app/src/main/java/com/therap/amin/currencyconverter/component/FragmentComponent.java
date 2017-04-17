package com.therap.amin.currencyconverter.component;

import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.module.FragmentModule;

import dagger.Component;

/**
 * @author Ripon
 */
@Component(dependencies = ApplicationComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(CurrencyConversionFragment currencyConversionFragment);
    void inject(CurrencyValueSaverFragment currencyValueSaverFragment);
}
