package com.tstv.infofrom.rest.api;


import com.tstv.infofrom.model.weather.Weather;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tstv on 15.09.2017.
 */

public interface WeatherApi {

    @GET("/v1/current.json")
    Observable<Weather> get(@Query("key") String api_key,
                            @Query("q") String city
    );

}
