package com.tstv.infofrom.rest.api;

import com.tstv.infofrom.model.search_places.GoogleAutocomplete;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tstv on 13.11.2017.
 */

public interface GoogleAutocompleteSearchApi {
    @GET("/maps/api/place/autocomplete/json")
    Call<GoogleAutocomplete> get
            (
                    @Query("input") String input,
                    @Query("types") String types,
                    @Query("key") String key
            );

}
