package com.therap.amin.currencyconverter.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.therap.amin.currencyconverter.service.FileProcessor;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class ApplicationModule {

    Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    FileProcessor provideFileProcessor() {
        return new FileProcessor(mApplication);
    }

    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }

}
