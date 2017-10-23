package com.tstv.infofrom.rest.api;

import com.tstv.infofrom.model.places.detail_places.PlacesDetailInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tstv on 14.10.2017.
 */

public interface DetailPlacesApi {
    @GET("/maps/api/place/details/json")
    Observable<PlacesDetailInfo> get(
            @Query("placeid") String placeId,
            @Query("language") String language,
            @Query("key") String key
    );
}
