package com.tstv.infofrom.ui.weather;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.tstv.infofrom.common.consts.ApiConstants;
import com.tstv.infofrom.model.weather.Weather;
import com.tstv.infofrom.rest.api.WeatherApi;
import com.tstv.infofrom.ui.base.BasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tstv on 15.09.2017.
 */

@InjectViewState
public class WeatherPresenter extends BasePresenter<WeatherView> {

    private WeatherApi mWeatherApi;

    private Double[] mLatLng;

    private boolean mIsLoading;

    void loadVariables(WeatherApi weatherApi, Double[] city) {
        Log.e("TAG", "coordinates : " + city[0] + "," + city[1]);
        mWeatherApi = weatherApi;
        mLatLng = city;
    }

    private void onLoadingSuccess(Weather data) {
        getViewState().setData(data);
    }

    @Override
    public void loadStart() {
        if (mIsLoading){
            return;
        }
        mIsLoading = true;

        mWeatherApi.get(ApiConstants.WEATHER_API_KEY, mLatLng[0] + "," + mLatLng[1])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> onLoadingStart())
                .doFinally(this::onLoadingFinish)
                .subscribe(this::onLoadingSuccess, error -> {
                    error.printStackTrace();
                    onLoadingFailed(error);
                    Log.e("TAG", "Response error" + error.getMessage());
                });
    }

    @Override
    public void loadRefresh() {
        loadStart();
    }

    @Override
    public void onLoadingStart() {
        showProgress();
    }

    @Override
    public void onLoadingFinish() {
        mIsLoading = false;
        getViewState().hideRefreshing();
        hideProgress();
    }

    @Override
    public void onLoadingFailed(Throwable throwable) {
        getViewState().showError(throwable.getMessage());
        throwable.printStackTrace();
    }

    @Override
    public void showProgress() {
        getViewState().showDataProgress();

    }

    @Override
    public void hideProgress() {
        getViewState().hideDataProgress();
    }

}
