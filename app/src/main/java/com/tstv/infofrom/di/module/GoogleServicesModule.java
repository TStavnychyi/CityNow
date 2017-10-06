package com.tstv.infofrom.di.module;

import android.support.v7.app.AppCompatActivity;

import com.tstv.infofrom.common.google.GoogleServicesHelper;
import com.tstv.infofrom.ui.places.PlacesFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 25.09.2017.
 */

@Module
public class GoogleServicesModule {

    private GoogleServicesHelper mGoogleServicesHelper;

    private PlacesFragment googleServicesListener;

    private AppCompatActivity mActivity;

    public GoogleServicesModule(PlacesFragment fragment, AppCompatActivity activity) {
        googleServicesListener = fragment;
        mActivity = activity;
    }

    @Singleton
    @Provides
    GoogleServicesHelper provideGoogleServicesHelper(){
        return mGoogleServicesHelper = new GoogleServicesHelper(mActivity, googleServicesListener);
    }
}
