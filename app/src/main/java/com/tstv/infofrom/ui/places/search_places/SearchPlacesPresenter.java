package com.tstv.infofrom.ui.places.search_places;

import android.graphics.Bitmap;

import com.arellomobile.mvp.InjectViewState;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.PlacePrediction;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.ui.base.BasePresenter;

import io.reactivex.Observable;

/**
 * Created by tstv on 24.10.2017.
 */

@InjectViewState
public class SearchPlacesPresenter extends BasePresenter<SearchPlacesView> {

    private static final String WEB_PLACES_API = "AIzaSyCMlZedC-qCI3FrVEovQ49Qg3qlD0BhKFs";

    NearbyPlacesApi mNearbyPlacesApi;

    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    public void loadVariables(NearbyPlacesApi nearbyPlacesApi, GooglePlacesServicesHelper googlePlacesServicesHelper) {
        mNearbyPlacesApi = nearbyPlacesApi;
        mGooglePlacesServicesHelper = googlePlacesServicesHelper;
    }


    @Override
    public void loadStart() {

    }

    @Override
    public void loadRefresh() {

    }

    @Override
    public void onLoadingStart(ProgressType progressType) {

    }

    @Override
    public void onLoadingFinish(ProgressType progressType) {

    }

    @Override
    public void onLoadingFailed(Throwable throwable) {

    }

    @Override
    public void showProgress(ProgressType progressType) {

    }

    @Override
    public void hideProgress(ProgressType progressType) {

    }

    private Observable<PlacePrediction> createNearbyPlacesDataObservable() {
        String currentLatLng = Utils.getStringLatLngFromDouble(MyApplication.getCurrentLtdLng());
        int radius = 5000;
        String placesType = "cafe";
        return mNearbyPlacesApi.get(currentLatLng, radius, placesType, WEB_PLACES_API)
                .flatMap(full -> Observable.fromIterable(full.getResults()))
                .map((item) -> new PlacePrediction(
                        item.getPlaceId(),
                        item.getName(),
                        item.getVicinity(),
                        getPlacePhoto(item.getPlaceId())
                ));
    }

    private Bitmap getPlacePhoto(String id) {
        Bitmap image = null;
        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGooglePlacesServicesHelper.getApiClient(), id).await();
        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0) {
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                image = photo.getScaledPhoto(mGooglePlacesServicesHelper.getApiClient(), 150, 150).await().getBitmap();
            }
            photoMetadataBuffer.release();
        }
        return image;
    }
}
