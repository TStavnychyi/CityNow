package com.tstv.infofrom;

import android.app.Activity;
import android.app.Application;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.di.component.ActivityComponent;
import com.tstv.infofrom.di.component.ApplicationComponent;
import com.tstv.infofrom.di.component.DaggerActivityComponent;
import com.tstv.infofrom.di.component.DaggerApplicationComponent;
import com.tstv.infofrom.di.component.DaggerRoadTrafficComponent;
import com.tstv.infofrom.di.component.PlaceComponent;
import com.tstv.infofrom.di.component.RoadTrafficComponent;
import com.tstv.infofrom.di.module.ActivityModule;
import com.tstv.infofrom.di.module.ApplicationModule;
import com.tstv.infofrom.di.module.GoogleLocationServicesModule;
import com.tstv.infofrom.di.module.GooglePlacesServicesModule;
import com.tstv.infofrom.ui.base.BaseActivity;
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
    private PlaceComponent mPlaceComponent;
    private ApplicationComponent sApplicationComponent;
    private RoadTrafficComponent mRoadTrafficComponent;
    private ActivityComponent mActivityComponent;
    //  private SearchPlacesComponent mSearchPlacesComponent;


    private static Double[] currentLtdLng;

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

    public ApplicationComponent getApplicationComponent() {
        return sApplicationComponent;
    }

    private void initComponent() {
        sApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
    }

    public ActivityComponent plusActivityComponent(BaseActivity activity, MvpAppCompatActivity listener) {
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

    public RoadTrafficComponent getRoadTrafficComponent() {
        return mRoadTrafficComponent;
    }

    /* public SearchPlacesComponent plusSearchPlacesComponent(SearchPlacesFragment fragment, AppCompatActivity activity) {
         if (mSearchPlacesComponent == null) {
             mSearchPlacesComponent = DaggerSearchPlacesComponent.builder()
                     .googlePlacesServicesModule(new GooglePlacesServicesModule(fragment, activity))
                     .build();
         }
         return mSearchPlacesComponent;
     }

     public SearchPlacesComponent getSearchPlacesComponent() {
         return mSearchPlacesComponent;
     }

     public void clearSearchPlacesComponent() {
         mSearchPlacesComponent = null;
     }
 */
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

