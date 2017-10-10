package com.tstv.infofrom.model.places;

import android.graphics.Bitmap;

/**
 * Created by tstv on 25.09.2017.
 */

public class PlacePrediction {
    private String id;

    private String placeName;

    private String placeDescription;

    private Bitmap bitmap;

    private String imageUrl;

    public PlacePrediction(){}

    public PlacePrediction(String placeName, String imageUrl) {
        this.placeName = placeName;
        this.imageUrl = imageUrl;
    }

    public PlacePrediction(String id, String placeName, String placeDescription, Bitmap bitmap) {
        this.id = id;
        this.placeName = placeName;
        this.placeDescription = placeDescription;
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}