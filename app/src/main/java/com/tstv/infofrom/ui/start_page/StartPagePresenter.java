package com.tstv.infofrom.ui.start_page;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.auto_complete.CityPrediction;
import com.tstv.infofrom.ui.base.BasePresenter;
import com.tstv.infofrom.ui.base.BaseView;
import com.tstv.infofrom.ui.base.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by tstv on 17.11.2017.
 */

@InjectViewState
public class StartPagePresenter extends BasePresenter<BaseView> {

    private static final String TAG = StartPagePresenter.class.getSimpleName();

    private String API_KEY = "AIzaSyBegZt-KrWSxlhBpvvGbRyR8u0bPn7Xahc";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    void loadVariables(GooglePlacesServicesHelper googlePlacesServicesHelper) {
        mGooglePlacesServicesHelper = googlePlacesServicesHelper;
    }

    void getAutoCompleteLocationData(CityPrediction cityPrediction, BehaviorSubject<Place> placeSubject) {
        Places.GeoDataApi.getPlaceById(mGooglePlacesServicesHelper.getApiClient(), cityPrediction.getPlaceId())
                .setResultCallback(places -> {
                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                        final Place place = places.get(0);
                        if (cityPrediction.getCityName() != null) {
                            MyApplication.setCurrentCity(cityPrediction.getCityName());
                        }
                        if (cityPrediction.getCountry() != null) {
                            MyApplication.setCurrentCountry(cityPrediction.getCountry());
                        }
                        placeSubject.onNext(place);
                    }
                    places.release();
                });
    }

    void rxGetCurrentLocation(Location location, Context context) {
        Observable.just(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((disposable -> onLoadingStart()))
                .doFinally(() -> {
                    onLoadingFinish();
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    getViewState().hideDataProgress();
                })
                .subscribe((data -> getCurrentLocationData(location, context)), error -> {
                    onLoadingFinish();
                    onLoadingFailed(error);
                });
    }

    private void getCurrentLocationData(Location location, Context context) {
        Double[] coordinates = {location.getLatitude(), location.getLongitude()};
        MyApplication.setCurrentLtdLng(coordinates);
        String city = Utils.getCityFromLatLng(coordinates, context);
        MyApplication.setCurrentCity(city);
        String country = Utils.getCountryCodeFromLatLng(coordinates, context);
        MyApplication.setCurrentCountryCode(country);
    }

    ArrayList<CityPrediction> autocomplete(String input) {
        ArrayList<CityPrediction> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=").append(API_KEY);
            sb.append("&types=(cities)");
            sb.append("&input=").append(URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {

            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject objStructuredFormatting = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting");
                CityPrediction cityPrediction = new CityPrediction();
                cityPrediction.setPlaceId(predsJsonArray.getJSONObject(i).getString("place_id"));
                cityPrediction.setDescription(predsJsonArray.getJSONObject(i).getString("description"));
                cityPrediction.setCityName(objStructuredFormatting.getString("main_text"));
                cityPrediction.setCountry(objStructuredFormatting.getString("secondary_text"));
                resultList.add(cityPrediction);

            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    void getCurrentLocation(Context context, MyLocationListener locationListener) throws SecurityException {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        locationListener.locationIsReady(location);
                    } else {
                        getViewState().showMessage("Can't find current location!");
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    getViewState().showMessage("Make sure to enable Internet on your phone");
                }
            }, null);
        } else {
            getViewState().showSnackBar(BaseView.SnackBarType.LocationDisabled);
            hideProgress();
        }
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
        hideProgress();
    }

    @Override
    public void onLoadingFailed(Throwable throwable) {
        getViewState().hideDataProgress();
        getViewState().showError(throwable.getMessage());
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
