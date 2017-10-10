package com.tstv.infofrom;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.tstv.infofrom.di.component.ApplicationComponent;
import com.tstv.infofrom.di.component.DaggerApplicationComponent;
import com.tstv.infofrom.di.component.DaggerPlaceComponent;
import com.tstv.infofrom.di.component.PlaceComponent;
import com.tstv.infofrom.di.module.ApplicationModule;
import com.tstv.infofrom.di.module.GoogleServicesModule;
import com.tstv.infofrom.ui.places.PlacesFragment;
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
    private PlaceComponent fragmentComponent;
    private static ApplicationComponent sApplicationComponent;


    private static Double[] currentLtdLng;
    private static String currentCity;

    private static String currentCity;


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

    public static ApplicationComponent getApplicationComponent() {
        return sApplicationComponent;
    }

    private void initComponent() {
        sApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
    }

    public PlaceComponent plusFragmentComponent(PlacesFragment fragment, AppCompatActivity activity) {
        if (fragmentComponent == null) {
            fragmentComponent = DaggerPlaceComponent.builder()
                    .googleServicesModule(new GoogleServicesModule(fragment, activity))
                    .applicationComponent(sApplicationComponent)
                    .build();
        }
        return fragmentComponent;
    }

    public void clearFragmentComponent() {
        fragmentComponent = null;
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
}

