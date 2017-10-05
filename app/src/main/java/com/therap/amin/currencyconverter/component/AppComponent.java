package com.therap.amin.currencyconverter.component;

import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.activity.MainActivity;
import com.therap.amin.currencyconverter.interfaces.ConversionPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.ValueSaverPresenterInterface;
import com.therap.amin.currencyconverter.module.ActivityModule;
import com.therap.amin.currencyconverter.module.FragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ripon
 */
@Singleton
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, FragmentModule.class})
public interface AppComponent {

    void inject(CurrencyConversionFragment currencyConversionFragment);

    void inject(CurrencyValueSaverFragment currencyValueSaverFragment);

    void inject(MainActivity mainActivity);

    void inject(ConversionPresenterInterface currencyConversionPresenter);

    void inject(ValueSaverPresenterInterface currencyValueSaverPresenter);

    void inject(MainActivityPresenterInterface mainActivityPresenter);
}
