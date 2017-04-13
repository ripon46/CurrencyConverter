package com.therap.amin.currencyconverter.module;

import android.content.Context;

import com.therap.amin.currencyconverter.service.FileProcessor;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ripon
 */
@Module
public class FileProcessorModule {

    Context context;

    public FileProcessorModule(Context context) {
        this.context = context;
    }

    @Provides
    FileProcessor provideFileProcessor() {
        return new FileProcessor(context);
    }
}
