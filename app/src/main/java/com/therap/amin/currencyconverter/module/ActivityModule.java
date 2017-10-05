package com.therap.amin.currencyconverter.module;

import com.loopj.android.http.AsyncHttpClient;
import com.therap.amin.currencyconverter.activity.MainActivity;
import com.therap.amin.currencyconverter.interfaces.MainActivityPresenterInterface;
import com.therap.amin.currencyconverter.interfaces.MainActivityViewInterface;
import com.therap.amin.currencyconverter.presenter.MainActivityPresenter;
import com.therap.amin.currencyconverter.service.CurrencyConverterService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class ActivityModule {

    MainActivity mainActivity;

    public ActivityModule(MainActivity mainActivityViewInterface) {
        this.mainActivity = mainActivityViewInterface;
    }

    @Provides
    @Singleton
    CurrencyConverterService provideCurrencyConverterService(@Named("asyncClient") AsyncHttpClient client) {
        return new CurrencyConverterService(client);
    }


    @Provides
    MainActivityViewInterface provideMainActivityViewInterface() {
        return mainActivity;
    }

    @Provides
    @Singleton
    MainActivityPresenterInterface provideMainActivityPresenter() {
        return new MainActivityPresenter(mainActivity);
    }

    @Named("asyncClient")
    @Provides
    AsyncHttpClient provideAsyncHttpClient() {
        return new AsyncHttpClient();
    }
}
