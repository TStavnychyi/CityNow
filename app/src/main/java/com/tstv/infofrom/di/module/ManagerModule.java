package com.tstv.infofrom.di.module;

import com.tstv.infofrom.common.manager.MyFragmentManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 15.09.2017.
 */

@Module
public class ManagerModule {
    @Provides
    @Singleton
    MyFragmentManager provideMyFragmentManaer() {
        return new MyFragmentManager();
    }
}
