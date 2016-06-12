package com.therap.amin.currencyconverter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;

import roboguice.RoboGuice;

/**
 * Created by amin on 5/30/16.
 */

public class ShowConversionRate {

    @Inject
    static FileProcessor fileProcessor;
    Context context;

    @Inject
    public ShowConversionRate(Context context) {
        this.context = context;
        RoboGuice.getInjector(context).injectMembers(this);
    }

    public void showRate(String from,String to) {
        Toast.makeText(context,fileProcessor.calculateConversionRate(from,to)+"",Toast.LENGTH_SHORT).show();
    }

}
