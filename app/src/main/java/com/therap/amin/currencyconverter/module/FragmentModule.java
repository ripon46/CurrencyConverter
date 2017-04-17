package com.therap.amin.currencyconverter.module;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.therap.amin.currencyconverter.R;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class FragmentModule {

    Context context;

    public FragmentModule(Context context) {
        this.context = context;
    }

    @Provides
    ArrayAdapter<String> provideArrayAdapter(@Named("provideCurrencies") String[] currencies) {
        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, currencies);
    }

    @Named("provideCurrencies")
    @Provides
    String[] provideCurrencies() {
        return context.getResources().getStringArray(R.array.currencies);
    }
}
