package com.tstv.infofrom.ui.places;

import android.graphics.Bitmap;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tstv on 22.09.2017.
 */

@InjectViewState
public class PlacesPresenter extends BasePresenter<PlacesView> {

    private static final String TAG = PlacesPresenter.class.getSimpleName();

    private static final String WEB_PLACES_API = "AIzaSyCMlZedC-qCI3FrVEovQ49Qg3qlD0BhKFs";

    private String placeType;

    private boolean isListLoadedEnough;

    private List<PlacePrediction> mNearbyPlaces = new ArrayList<>();

    private PlacesAdapter mPlacesAdapter;


    private GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    private NearbyPlacesApi mNearbyPlacesApi;

    private boolean isLoading;


    void loadVariables(PlacesAdapter adapter, GooglePlacesServicesHelper googlePlacesServicesHelper, NearbyPlacesApi nearbyPlacesApi) {
        mPlacesAdapter = adapter;
        mGooglePlacesServicesHelper = googlePlacesServicesHelper;
        mNearbyPlacesApi = nearbyPlacesApi;
    }

    void loadData(boolean isLocationDataAlreadyLoaded, String searchType) {
        placeType = searchType;
        if (!isLocationDataAlreadyLoaded) {
            createLocationDataObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> onLoadingStart())
                    .doFinally(() -> {
                        onLoadingFinish();
                        if (!isListLoadedEnough) {
                            showRecyclerViewProgressBar();
                        }
                    })
                    .subscribe(locationData -> {
                        getViewState().setLocationData(locationData);
                    }, error -> {
                        error.printStackTrace();
                        onLoadingFailed(error);
                    });
        }
                createNearbyPlacesDataObservable(searchType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                            isLoading = true;
                            if (isLocationDataAlreadyLoaded) {
                                showRecyclerViewProgressBar();
                            }
                        })
                        .doFinally(() -> isLoading = false)
                        .filter(item -> item.getType().equals(placeType))
                        .subscribe(items -> {
                            if (items != null) {
                                mNearbyPlaces.add(items);
                                mPlacesAdapter.setItems(mNearbyPlaces);
                                if (mPlacesAdapter.isListLoadedEnough()) {
                                    isListLoadedEnough = true;
                                    hideRecyclerViewProgressBar();
                                }
                            }
                        }, error -> {
                            Log.e(TAG, "nearbyPlaces onError");
                            isLoading = false;
                        }, () -> isLoading = false);
    }

    private Observable<PlacePrediction> createLocationDataObservable() {
        String currentCity = MyApplication.getCurrentCity();
        if (currentCity == null && currentCity.isEmpty()) {
            return null;
        }

        return Observable.just(currentCity)
                .flatMap(city -> Observable.just(Utils.getPhotoFromBingAPI(city)))
                .map((imageUrl) -> new PlacePrediction(currentCity, imageUrl));
    }

    private Observable<PlacePrediction> createNearbyPlacesDataObservable(String placesType) {
        String currentLatLng = Utils.getStringLatLngFromDouble(MyApplication.getCurrentLtdLng());
        int radius = 10000;
        return mNearbyPlacesApi.get(currentLatLng, radius, placesType, WEB_PLACES_API)
                .flatMap(full -> Observable.fromIterable(full.getResults()))
                .map((item) -> new PlacePrediction(
                        item.getPlaceId(),
                        item.getName(),
                        item.getVicinity(),
                        getPlacePhoto(item.getPlaceId()),
                        placesType
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

    @Override
    public void loadStart() {

    }

    @Override
    public void loadRefresh() {

    }

    @Override
    public void onLoadingStart() {
        showProgress();
    }

    @Override
    public void onLoadingFinish() {
        isLoading = false;
        hideProgress();
    }

    private void hideRecyclerViewProgressBar() {
        getViewState().hiderRecyclerViewProgress();
    }

    private void showRecyclerViewProgressBar() {
        getViewState().showRecyclerViewProgress();
    }

    @Override
    public void onLoadingFailed(Throwable throwable) {
        getViewState().showError(throwable.getMessage());
        throwable.printStackTrace();
        hideProgress();
    }

    @Override
    public void showProgress() {
        getViewState().showDataProgress();
    }

    @Override
    public void hideProgress() {
        getViewState().hideDataProgress();
    }

    void clearPlacesAdapterData() {
        mNearbyPlaces.clear();
        mPlacesAdapter.clearListAndNotifyDataChanged();
    }
}