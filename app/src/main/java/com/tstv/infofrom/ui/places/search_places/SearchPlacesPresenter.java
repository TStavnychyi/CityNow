package com.tstv.infofrom.ui.places.search_places;

import android.graphics.Bitmap;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
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
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.PlacePrediction;
import com.tstv.infofrom.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tstv on 24.10.2017.
 */

@InjectViewState
public class SearchPlacesPresenter extends BasePresenter<SearchPlacesView> {

    private static final String WEB_PLACES_API = "AIzaSyCMlZedC-qCI3FrVEovQ49Qg3qlD0BhKFs";

    private static final String TAG = SearchPlacesPresenter.class.getSimpleName();


    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    private SearchPlacesAdapter mAdapter;

    private boolean isLoadingFinished = false;

    private List<PlacePrediction> mSearchViewPlaces = new ArrayList<>();


    private AutocompleteFilter filter;

    private LatLngBounds mLatLngBounds;

    void loadVariables(SearchPlacesAdapter adapter, GooglePlacesServicesHelper googlePlacesServicesHelper) {
        mAdapter = adapter;
        mGooglePlacesServicesHelper = googlePlacesServicesHelper;
        if (MyApplication.getCurrentCountryCode() != null) {
            filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .setCountry(MyApplication.getCurrentCountryCode())
                    .build();
        }
    }


    void getResultFromInput(String userInput) {
        createSearchDataObservable(userInput)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable ->
                        getViewState().showDataProgress())
                .doFinally(() -> {
                    getViewState().hideDataProgress();
                    getViewState().showSearchPlacesRecyclerView();
                    mSearchViewPlaces.clear();
                })
                .subscribe(obj -> {
                    mSearchViewPlaces.add(obj);
                    mAdapter.setItems(mSearchViewPlaces);
                });

    }

    private Observable<PlacePrediction> createSearchDataObservable(String input_text) {
        if (MyApplication.getCurrentLtdLng() != null) {
            mLatLngBounds = Utils.getLatLngBoundsFromDouble(MyApplication.getCurrentLtdLng());
        }
        if (mGooglePlacesServicesHelper.getApiClient() != null && mLatLngBounds != null && filter != null) {
            return Observable.create(sub -> {
                PendingResult<AutocompletePredictionBuffer> result =
                        Places.GeoDataApi.getAutocompletePredictions(mGooglePlacesServicesHelper.getApiClient(), input_text, mLatLngBounds, filter);
                AutocompletePredictionBuffer autocompletePredictions = result.await();
                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    autocompletePredictions.release();
                    sub.onError(null);
                } else {
                    for (AutocompletePrediction prediction : autocompletePredictions) {
                        Bitmap photoPlace = getPlacePhoto(prediction.getPlaceId());
                        Log.e(TAG, "createSearchData : " + prediction.getPrimaryText(null));
                        sub.onNext(
                                new PlacePrediction(prediction.getPlaceId(), (String) prediction.getPrimaryText(null),
                                        (String) prediction.getSecondaryText(null), photoPlace, "")
                        );
                    }
                }
                autocompletePredictions.release();
                sub.onComplete();
            });
        }
        return null;
    }

    private Bitmap getPlacePhoto(String id) {
        if (id != null) {
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
        return null;
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
}
