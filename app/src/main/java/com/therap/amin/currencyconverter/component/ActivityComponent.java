package com.therap.amin.currencyconverter.component;

import com.loopj.android.http.AsyncHttpClient;
import com.therap.amin.currencyconverter.activity.MainActivity;
import com.therap.amin.currencyconverter.module.ActivityModule;

import dagger.Component;

/**
 * Created by amin on 4/16/17.
 */
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);
}
