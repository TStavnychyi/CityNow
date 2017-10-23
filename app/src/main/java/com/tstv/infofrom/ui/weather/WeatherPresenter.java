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

    private String mCity;

    private boolean mIsLoading;

    void loadVariables(WeatherApi weatherApi, String city) {
        mWeatherApi = weatherApi;
        mCity = city;
    }

    private void loadData(ProgressType progressType, String key, String city) {
        if (mIsLoading){
            return;
        }
        mIsLoading = true;

        mWeatherApi.get(key, city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> onLoadingStart(progressType))
                .doFinally(() -> onLoadingFinish(progressType))
                .subscribe(response -> {
                    Log.e("TAG", "Response Success " + response.getCurrent().getTempC());
                    onLoadingSuccess(progressType, response);
                },error -> {
                    error.printStackTrace();
                    onLoadingFailed(error);
                    Log.e("TAG", "Response error" + error.getMessage());
                });
    }

    private void onLoadingSuccess(ProgressType progressType, Weather data) {
        getViewState().setData(data);
    }

    @Override
    public void loadStart() {
        loadData(ProgressType.DataProgress, ApiConstants.WEATHER_API_KEY, mCity);
    }

    @Override
    public void loadRefresh() {
        loadData(ProgressType.Refreshing, ApiConstants.WEATHER_API_KEY, mCity);
    }

    @Override
    public void onLoadingStart(ProgressType progressType) {
        showProgress(progressType);
    }

    @Override
    public void onLoadingFinish(ProgressType progressType) {
        mIsLoading = false;
        hideProgress(progressType);
    }

    @Override
    public void onLoadingFailed(Throwable throwable) {
        getViewState().showError(throwable.getMessage());
        throwable.printStackTrace();
    }

    @Override
    public void showProgress(ProgressType progressType) {
        switch (progressType){
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
        switch (progressType){
            case Refreshing:
                getViewState().hideRefreshing();
                break;
            case DataProgress:
                getViewState().hideDataProgress();
                break;
        }
    }

}
