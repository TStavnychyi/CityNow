package com.tstv.infofrom.di.module;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 15.09.2017.
 */

@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Singleton
    @Provides
    public Context provideContext(){
        return mApplication;
    }

    @Singleton
    @Provides
    LayoutInflater provideLayoutInflater(){
        return (LayoutInflater) mApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


}
