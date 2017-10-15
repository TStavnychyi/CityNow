package com.tstv.infofrom.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tstv on 15.09.2017.
 */

public class RxRestClient {
    private static final String WEATHER_BASE_URL = "https://api.apixu.com/v1/";

    private Retrofit mRetrofit;

    public RxRestClient(String base_url) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(base_url)
                .build();
    }

    public <S> S createService(Class<S> serviceClass){
        return mRetrofit.create(serviceClass);
    }
}
