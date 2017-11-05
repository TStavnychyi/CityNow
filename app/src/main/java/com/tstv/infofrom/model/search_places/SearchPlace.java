package com.tstv.infofrom.model.search_places;

/**
 * Created by tstv on 24.10.2017.
 */

public class SearchPlace {

    private int mImage;
    private String mPlaceText;

    public SearchPlace(String placeText, int image) {
        mPlaceText = placeText;
        mImage = image;
    }

    public int getImage() {
        return mImage;
    }

    public String getPlaceText() {
        return mPlaceText;
    }

    public void setImage(int image) {
        mImage = image;
    }

    public void setPlaceText(String placeText) {
        mPlaceText = placeText;
    }
}
