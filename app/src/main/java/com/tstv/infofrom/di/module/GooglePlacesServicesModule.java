package com.tstv.infofrom.di.module;

import android.support.v7.app.AppCompatActivity;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 25.09.2017.
 */

@Module
public class GooglePlacesServicesModule {

    private AppCompatActivity googleServicesListener;

    private MvpAppCompatActivity mAppCompatActivity;

    public GooglePlacesServicesModule(AppCompatActivity fragment, MvpAppCompatActivity appCompatActivity) {
        googleServicesListener = fragment;
        mAppCompatActivity = appCompatActivity;
    }

    @Provides
    GooglePlacesServicesHelper provideGoogleServicesHelper() {
        return new GooglePlacesServicesHelper(mAppCompatActivity, (GooglePlacesServicesHelper.GoogleServicesListener) googleServicesListener);
    }
}
