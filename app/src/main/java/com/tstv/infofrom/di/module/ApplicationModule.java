package com.tstv.infofrom.di.module;

import android.app.Application;
import android.content.Context;

import com.tstv.infofrom.common.manager.MyFragmentManager;
import com.tstv.infofrom.di.scopes.ApplicationContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 15.09.2017.
 */

@Module
public class ApplicationModule {


    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @ApplicationContext
    @Provides
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    MyFragmentManager provideMyFragmentManager() {
        return new MyFragmentManager();
    }

}
