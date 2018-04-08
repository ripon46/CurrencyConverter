package com.therap.amin.currencyconverter.module;

import android.app.ProgressDialog;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.therap.amin.currencyconverter.interfaces.MainActivityPresenterInterface;
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

    Context context;

    public ActivityModule() {

    }

    public ActivityModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    CurrencyConverterService provideCurrencyConverterService(@Named("asyncClient") AsyncHttpClient client) {
        return new CurrencyConverterService(client);
    }

    @Provides
    @Singleton
    MainActivityPresenterInterface provideMainActivityPresenter() {
        return new MainActivityPresenter();
    }

    @Named("asyncClient")
    @Provides
    @Singleton
    AsyncHttpClient provideAsyncHttpClient() {
        return new AsyncHttpClient();
    }

    @Provides
    ProgressDialog provideProgressDialog() {
        return new ProgressDialog(context);
    }
}
