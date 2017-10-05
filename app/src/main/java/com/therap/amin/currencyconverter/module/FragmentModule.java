package com.therap.amin.currencyconverter.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.therap.amin.currencyconverter.Fragments.CurrencyConversionFragment;
import com.therap.amin.currencyconverter.Fragments.CurrencyValueSaverFragment;
import com.therap.amin.currencyconverter.R;
import com.therap.amin.currencyconverter.activity.MainActivity;
import com.therap.amin.currencyconverter.interfaces.ConversionFragmentViewInterface;
import com.therap.amin.currencyconverter.interfaces.ConversionPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityViewInterface;
import com.therap.amin.currencyconverter.interfaces.ValueSaverFragmentViewInterface;
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

    MainActivity mainActivity;

    public FragmentModule(MainActivity context) {
        this.mainActivity = context;
    }

    @Provides
    ArrayAdapter<String> provideArrayAdapter(@Named("provideCurrencies") String[] currencies) {
        return new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item, currencies);
    }

    @Named("provideCurrencies")
    @Provides
    String[] provideCurrencies() {
        return mainActivity.getResources().getStringArray(R.array.currencies);
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
    FileProcessor providesFileProcessor(Context context) {
        return new FileProcessor(context);
    }
}
