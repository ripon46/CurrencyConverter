package com.therap.amin.currencyconverter.module;

import com.loopj.android.http.AsyncHttpClient;
import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.service.CurrencyConverterService;

import java.util.ArrayList;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class ActivityModule {

    @Provides
    CurrencyConverterService provideCurrencyConverterService(@Named("asyncClient") AsyncHttpClient client) {
        return new CurrencyConverterService(client);
    }

    @Provides
    CurrencyConversionFragment provideCurrencyConversionFragment() {
        return new CurrencyConversionFragment();
    }

    @Provides
    CurrencyValueSaverFragment provideCurrencyValueSaverFragment() {
        return new CurrencyValueSaverFragment();
    }

    @Named("asyncClient")
    @Provides
    AsyncHttpClient provideAsyncHttpClient( ) {
        return new AsyncHttpClient();
    }

    @Provides
    ArrayList<Integer> provideArrayList() {
        return new ArrayList<>();
    }
}
