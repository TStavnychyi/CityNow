package com.tstv.infofrom.di.module;

import com.tstv.infofrom.di.scopes.PlacesScope;
import com.tstv.infofrom.rest.RxRestClient;
import com.tstv.infofrom.rest.api.DetailPlacesApi;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.rest.api.PlacesPhotoFromReferenceApi;
import com.tstv.infofrom.rest.api.WeatherApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 15.09.2017.
 */

@Module
public class RestModule {
    private RxRestClient mRxRestClient;

    @Provides
    @PlacesScope
    NearbyPlacesApi providePlacesNearbyApi() {
        mRxRestClient = new RxRestClient("https://maps.googleapis.com/");
        return mRxRestClient.createService(NearbyPlacesApi.class);
    }

    @Singleton
    @Provides
    WeatherApi provideWeatherApi() {
        mRxRestClient = new RxRestClient("https://api.apixu.com/v1/");
        return mRxRestClient.createService(WeatherApi.class);
    }

    @Provides
    @PlacesScope
    DetailPlacesApi provideDetailPlacesApi() {
        mRxRestClient = new RxRestClient("https://maps.googleapis.com/");
        return mRxRestClient.createService(DetailPlacesApi.class);
    }

    @Provides
    @PlacesScope
    PlacesPhotoFromReferenceApi providePlacesPhotoApi() {
        mRxRestClient = new RxRestClient("https://maps.googleapis.com/maps/api/place/photo/");
        return mRxRestClient.createService(PlacesPhotoFromReferenceApi.class);
    }

}
