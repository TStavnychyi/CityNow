package com.tstv.infofrom.model.places.auto_complete;

/**
 * Created by tstv on 17.11.2017.
 */

public class CityPrediction {

    private String cityName;

    //PlaceId which is getting from Google Api to get information about city
    private String placeId;

    private String country;

    private String description;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
