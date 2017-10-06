package com.tstv.infofrom.rest.api;

import com.tstv.infofrom.model.places.PlaceNearby;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tstv on 30.09.2017.
 */

public interface NearbyPlacesApi {
    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlaceNearby> get(@Query("location") String latlnt,
                                @Query("radius") int radius,
                                @Query("types") String types,
                                @Query("key") String api_key
    );

}
