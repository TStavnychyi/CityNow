package com.tstv.infofrom;

import android.app.Activity;
import android.app.Application;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.di.component.ActivityComponent;
import com.tstv.infofrom.di.component.ApplicationComponent;
import com.tstv.infofrom.di.component.DaggerActivityComponent;
import com.tstv.infofrom.di.component.DaggerApplicationComponent;
import com.tstv.infofrom.di.component.DaggerRoadTrafficComponent;
import com.tstv.infofrom.di.component.RoadTrafficComponent;
import com.tstv.infofrom.di.module.ActivityModule;
import com.tstv.infofrom.di.module.ApplicationModule;
import com.tstv.infofrom.di.module.GoogleLocationServicesModule;
import com.tstv.infofrom.di.module.GooglePlacesServicesModule;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.places.PlacesPresenter;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by tstv on 15.09.2017.
 */

public class MyApplication extends Application {

    protected static MyApplication instance;

    //Dagger 2 components
    private ApplicationComponent sApplicationComponent;
    private RoadTrafficComponent mRoadTrafficComponent;
    private ActivityComponent mActivityComponent;

    private static Double[] currentLtdLng;

    private static String currentCity;

    private static String currentCountryCode;

    private static String currentCountry;

    //if application was started by current location or by user's input
    private static boolean isFromCurrentLocation;

    private static boolean isGooglePlacesServicesConnected;


    @Inject
    PlacesPresenter mPlacesPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initComponent();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static MyApplication get() {
        return instance;
    }

    public ApplicationComponent getApplicationComponent() {
        return sApplicationComponent;
    }

    private void initComponent() {
        sApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
    }

    public ActivityComponent plusActivityComponent(MvpAppCompatActivity activity, MvpAppCompatActivity listener) {
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(activity))
                .googlePlacesServicesModule(new GooglePlacesServicesModule(activity, listener))
                .applicationComponent(getApplicationComponent())
                .build();
        return mActivityComponent;
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    public RoadTrafficComponent plusRoadTrafficComponent(BaseFragment fragment, Activity activity) {
        if (mRoadTrafficComponent == null) {
            mRoadTrafficComponent = DaggerRoadTrafficComponent.builder()
                    .googleLocationServicesModule(new GoogleLocationServicesModule(fragment, activity))
                    .applicationComponent(sApplicationComponent)
                    .build();
        }
        return mRoadTrafficComponent;
    }

    public static Double[] getCurrentLtdLng() {
        return currentLtdLng;
    }

    public static void setCurrentLtdLng(Double[] currentLtdLng) {
        MyApplication.currentLtdLng = currentLtdLng;
    }

    public static String getCurrentCity() {
        return currentCity;
    }

    public static void setCurrentCity(String currentCity) {
        MyApplication.currentCity = currentCity;
    }

    public static String getCurrentCountry() {
        return currentCountry;
    }

    public static void setCurrentCountry(String currentCountry) {
        MyApplication.currentCountry = currentCountry;
    }

    public static String getCurrentCountryCode() {
        return currentCountryCode;
    }

    public static void setCurrentCountryCode(String currentCountryCode) {
        MyApplication.currentCountryCode = currentCountryCode;
    }

    public static boolean isGooglePlacesServicesConnected() {
        return isGooglePlacesServicesConnected;
    }

    public static void setIsGooglePlacesServicesConnected(boolean isGooglePlacesServicesConnected) {
        MyApplication.isGooglePlacesServicesConnected = isGooglePlacesServicesConnected;
    }

    public static boolean isFromCurrentLocation() {
        return isFromCurrentLocation;
    }

    public static void setIsFromCurrentLocation(boolean isFromCurrentLocation) {
        MyApplication.isFromCurrentLocation = isFromCurrentLocation;
    }
}

