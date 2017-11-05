package com.therap.amin.currencyconverter.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class ApplicationModule {

    Application mApplication;

    public ApplicationModule() {

    }

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    Context provideContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }
}
