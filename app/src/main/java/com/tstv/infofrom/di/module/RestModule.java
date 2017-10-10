package com.tstv.infofrom.di.module;

import com.tstv.infofrom.di.scopes.PlacesScope;
import com.tstv.infofrom.rest.RestClient;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.rest.api.WeatherApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 15.09.2017.
 */

@Module
public class RestModule {
    private RestClient mRestClient;

    @Provides
    @PlacesScope
    NearbyPlacesApi providePlacesNearbyApi() {
        mRestClient = new RestClient("https://maps.googleapis.com/");
        return mRestClient.createService(NearbyPlacesApi.class);
    }

    @Singleton
    @Provides
    WeatherApi provideWeatherApi() {
        mRestClient = new RestClient("https://api.apixu.com/v1/");
        return mRestClient.createService(WeatherApi.class);
    }

}
