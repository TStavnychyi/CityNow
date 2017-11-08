package com.tstv.infofrom.di.module;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.ui.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 25.09.2017.
 */

@Module
public class GooglePlacesServicesModule {

    private BaseActivity googleServicesListener;

    private MvpAppCompatActivity mAppCompatActivity;

    public GooglePlacesServicesModule(BaseActivity fragment, MvpAppCompatActivity appCompatActivity) {
        googleServicesListener = fragment;
        mAppCompatActivity = appCompatActivity;
    }

    /* @Provides
   //  @PlacesScope
     MvpAppCompatActivity provideAppCompatActivity() {
         return mAppCompatActivity;
     }
 */
    @Provides
    //  @PlacesScope
    GooglePlacesServicesHelper provideGoogleServicesHelper() {
        return new GooglePlacesServicesHelper(mAppCompatActivity, googleServicesListener);
    }
}
