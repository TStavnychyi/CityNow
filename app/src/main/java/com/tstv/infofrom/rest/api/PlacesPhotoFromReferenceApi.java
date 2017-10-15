package com.tstv.infofrom.rest.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tstv on 14.10.2017.
 */

public interface PlacesPhotoFromReferenceApi {
    @GET("/maps/api/place/photo?maxwidth=400")
    Call<String> get(
            @Query("maxwidth") int maxWidth,
            @Query("photoreference") String photoReference,
            @Query("key") String apiKey
    );
}
