package com.therap.amin.currencyconverter.module;

import android.app.Application;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.R;
import com.therap.amin.currencyconverter.interfaces.ConversionPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.ValueSaverPresenterInterface;
import com.therap.amin.currencyconverter.presenter.CurrencyConversionPresenter;
import com.therap.amin.currencyconverter.presenter.CurrencyValueSaverPresenter;
import com.therap.amin.currencyconverter.service.FileProcessor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class FragmentModule {

    Application mApplication;

    public FragmentModule() {

    }

    public FragmentModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    ArrayAdapter<String> provideArrayAdapter(@Named("provideCurrencies") String[] currencies) {
        return new ArrayAdapter<String>(mApplication, android.R.layout.simple_spinner_item, currencies);
    }

    @Named("provideCurrencies")
    @Provides
    String[] provideCurrencies() {
        return mApplication.getResources().getStringArray(R.array.currencies);
    }

    @Provides
    @Singleton
    ValueSaverPresenterInterface provideValueSaverPresenter() {
        return new CurrencyValueSaverPresenter();
    }

    @Provides
    @Singleton
    ConversionPresenterInterface provideConversionPresenterInterface() {
        return new CurrencyConversionPresenter();
    }

    @Provides
    @Singleton
    FileProcessor providesFileProcessor(Context context) {
        return new FileProcessor(context);
    }

    @Provides
    @Singleton
    CurrencyConversionFragment provideCurrencyConversionFragment() {
        return new CurrencyConversionFragment();
    }

    @Provides
    @Singleton
    CurrencyValueSaverFragment provideCurrencyValueSaverFragment() {
        return new CurrencyValueSaverFragment();
    }
}
