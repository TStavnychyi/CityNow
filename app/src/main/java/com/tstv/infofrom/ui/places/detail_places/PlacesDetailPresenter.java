package com.tstv.infofrom.ui.places.detail_places;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.detail_places.PlacesDetailInfo;
import com.tstv.infofrom.model.places.detail_places.Result;
import com.tstv.infofrom.rest.api.DetailPlacesApi;
import com.tstv.infofrom.rest.api.PlacesPhotoFromReferenceApi;
import com.tstv.infofrom.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tstv on 13.10.2017.
 */

@InjectViewState
public class PlacesDetailPresenter extends BasePresenter<PlacesDetailView> {

    private static final String WEB_PLACES_API = "AIzaSyCMlZedC-qCI3FrVEovQ49Qg3qlD0BhKFs";

    private DetailPlacesApi mDetailPlacesApi;

    private PlacesPhotoFromReferenceApi mPhotoApi;

    private String mPlaceId;

    private boolean isLoading;

    private List<String> photoUrls = new ArrayList<>();
    private ProgressType progressType = ProgressType.DataProgress;


    @Override
    public void loadStart() {
        photoUrls.clear();
        createDetailInfoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> onLoadingStart(progressType))
                .subscribe(this::getPlacePhotos, error -> {
                    error.printStackTrace();
                    onLoadingFailed(error);
                });

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

    void setDetailPlacesApi(DetailPlacesApi detailPlacesApi, PlacesPhotoFromReferenceApi photoApi, String placeId) {
        mDetailPlacesApi = detailPlacesApi;
        mPhotoApi = photoApi;
        mPlaceId = placeId;
    }

    private Observable<Result> createDetailInfoObservable() {
        return mDetailPlacesApi.get(mPlaceId, "eng", WEB_PLACES_API)
                .map(PlacesDetailInfo::getResult);
    }

    private void getPlacePhotos(Result resultObj) {
        List<String> photoReferences = new ArrayList<>();
        int photosSize;
        final int[] elementsLoadedCount = new int[1];
        if (resultObj.getPhotos() != null) {
            if (resultObj.getPhotos().size() > 10) {
                photosSize = 10;
            } else {
                photosSize = resultObj.getPhotos().size();
            }
            for (int i = 0; i < photosSize; i++) {
                photoReferences.add(resultObj.getPhotos().get(i).getPhotoReference());
            }
            for (int i = 0; i < photoReferences.size(); i++) {
                createPhotoFromReferenceObservable(photoReferences.get(i))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(value -> {
                            elementsLoadedCount[0]++;
                            photoUrls.add(value);
                            if (elementsLoadedCount[0] == photosSize) {
                                resultObj.setPhotosUrls(photoUrls);
                                updateObjectInView(resultObj);
                            }
                        });

            }
        } else {
            updateObjectInView(resultObj);
            Log.e("TAG", "No photos in this place");
        }
    }

    private Observable<String> createPhotoFromReferenceObservable(String reference) {
        return Observable.create(sub -> mPhotoApi.get(400, reference, WEB_PLACES_API).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                sub.onNext(Utils.formatPlacesDetailResponse(response.toString()));
                sub.onComplete();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                sub.onError(t);
                sub.onComplete();
            }
        }));
    }

    private void updateObjectInView(Result resultObj) {
        Log.e("TAG", "updateObjInView");
        getViewState().setData(resultObj);
        onLoadingFinish(progressType);
    }
}
