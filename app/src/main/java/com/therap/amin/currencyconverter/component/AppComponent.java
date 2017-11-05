package com.therap.amin.currencyconverter.component;

import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.activity.MainActivity;
import com.therap.amin.currencyconverter.module.ActivityModule;
import com.therap.amin.currencyconverter.module.ApplicationModule;
import com.therap.amin.currencyconverter.module.FragmentModule;
import com.therap.amin.currencyconverter.presenter.CurrencyConversionPresenter;
import com.therap.amin.currencyconverter.presenter.CurrencyValueSaverPresenter;
import com.therap.amin.currencyconverter.presenter.MainActivityPresenter;
import com.therap.amin.currencyconverter.service.FileProcessor;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ripon
 */
@Singleton
@Component(modules = {ApplicationModule.class, ActivityModule.class, FragmentModule.class})
public interface AppComponent {

    void inject(CurrencyConversionFragment currencyConversionFragment);

    void inject(CurrencyValueSaverFragment currencyValueSaverFragment);

    void inject(MainActivity mainActivity);

    void inject(CurrencyConversionPresenter currencyConversionPresenter);

    void inject(CurrencyValueSaverPresenter currencyValueSaverPresenter);

    void inject(MainActivityPresenter mainActivityPresenter);

    void inject(FileProcessor fileProcessor);
}
