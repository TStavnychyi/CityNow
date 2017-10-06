package com.tstv.infofrom.ui.places;

import android.graphics.Bitmap;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.common.google.GoogleServicesHelper;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.PlacePrediction;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tstv on 22.09.2017.
 */

public class PlacesPresenter extends BasePresenter<PlacesView> {

    private static final String TAG = PlacesPresenter.class.getSimpleName();

    private static final String WEB_PLACES_API = "AIzaSyCMlZedC-qCI3FrVEovQ49Qg3qlD0BhKFs";

    private String input_text;

    private boolean isLoading;

    private List<PlacePrediction> mSearchViewPlaces = new ArrayList<>();
    private List<PlacePrediction> mNearbyPlaces = new ArrayList<>();

    @Inject
    PlacesAdapter mPlacesAdapter;

    @Inject
    GoogleServicesHelper mGoogleServicesHelper;

    @Inject
    NearbyPlacesApi mNearbyPlacesApi;

    private AutocompleteFilter filter = new AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
            .build();

    private LatLngBounds mLatLngBounds;

    public PlacesPresenter() {
    }

    void loadVariables() {
        PlacesFragment.getGoogleServicesComponent().inject(this);
    }

    void loadData(ProgressType progressType) {

        switch (progressType) {
            case DataProgress:
                getNearbyPlaces()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> {
                            mSearchViewPlaces.add(items);
                            mNearbyPlaces.add(items);
                            mPlacesAdapter.setItems(mSearchViewPlaces);

                        });
                break;
            case TextAutoComplete:
                createLoadObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(obj -> {
                            mSearchViewPlaces.add(obj);
                            mPlacesAdapter.setItems(mSearchViewPlaces);
                        });
                break;

        }

    }

    private Observable<PlacePrediction> getNearbyPlaces() {
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


    private Observable<PlacePrediction> createLoadObservable() {
        isLoading = PlacesFragment.isGoogleServicesAvailable();
        mLatLngBounds = Utils.getLatLngBoundsFromDouble(MyApplication.getCurrentLtdLng());
        return Observable.create(sub -> {
            PendingResult<AutocompletePredictionBuffer> result =
                    Places.GeoDataApi.getAutocompletePredictions(mGoogleServicesHelper.getApiClient(), input_text, mLatLngBounds, filter);
            AutocompletePredictionBuffer autocompletePredictions = result.await();
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                autocompletePredictions.release();
                sub.onError(null);
            } else {
                for (AutocompletePrediction prediction : autocompletePredictions) {
                    Bitmap photoPlace = getPlacePhoto(prediction.getPlaceId());
                    sub.onNext(
                            new PlacePrediction(prediction.getPlaceId(), (String) prediction.getPrimaryText(null),
                                    (String) prediction.getSecondaryText(null), photoPlace)
                    );
                }
            }
            autocompletePredictions.release();
            sub.onComplete();
        });
    }

    private Bitmap getPlacePhoto(String id) {
        Bitmap image = null;
        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleServicesHelper.getApiClient(), id).await();
        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0) {
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                image = photo.getScaledPhoto(mGoogleServicesHelper.getApiClient(), 150, 150).await().getBitmap();
            }
            photoMetadataBuffer.release();
        }
        return image;
    }

    void getInputFromUser(String arg) {
        input_text = arg;
        mSearchViewPlaces.clear();
    }

    void setNearbyPlaces(){
        mPlacesAdapter.setItems(mNearbyPlaces);
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadRefresh() {

    }

    @Override
    public void onLoadingStart(ProgressType progressType) {
        showProgress(progressType);
    }

    @Override
    public void onLoadingFinish(ProgressType progressType) {
        isLoading = false;
        hideProgress(progressType);
    }

    @Override
    public void onLoadingFailed(Throwable throwable) {
        getViewState().showError(throwable.getMessage());
        throwable.printStackTrace();
    }

    @Override
    public void showProgress(ProgressType progressType) {
        switch (progressType) {
            case Refreshing:
                getViewState().showRefreshing();
                break;
            case DataProgress:
                getViewState().showDataProgress();
                break;
        }
    }

    @Override
    public void hideProgress(ProgressType progressType) {
        switch (progressType) {
            case Refreshing:
                getViewState().hideRefreshing();
                break;
            case DataProgress:
                getViewState().hideDataProgress();
                break;
        }
    }
}
