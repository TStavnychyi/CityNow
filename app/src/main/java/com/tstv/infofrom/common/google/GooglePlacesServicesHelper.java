package com.tstv.infofrom.common.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;
import static com.tstv.infofrom.ui.base.BaseActivity.PLACE_PICKER_REQUEST;

/**
 * Created by tstv on 20.09.2017.
 */

public class GooglePlacesServicesHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GooglePlacesServicesHelper.class.getSimpleName();

    public interface GoogleServicesListener {
        void onConnected();

        void onDisconnected();
    }

    public static final int REQUEST_CODE_RESOLUTION = -100;
    public static final int REQUEST_CODE_AVAILABILITY = -101;

    private Activity activity;
    private GoogleServicesListener listener;
    private GoogleApiClient apiClient;

    public GooglePlacesServicesHelper(Activity activity, GoogleServicesListener listener) {
        this.listener = listener;
        this.activity = activity;

        this.apiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

    }

    public void connect() {
        Log.e(TAG, "GooglePlacesServicesHelper connect()");
        if (isGooglePlayServicesAvailable()) {
            apiClient.connect();
        } else {
            listener.onDisconnected();
        }
    }

    public void disconnect() {
        Log.e(TAG, "GooglePlacesServicesHelper disconnect()");
        if (isGooglePlayServicesAvailable()) {
            apiClient.disconnect();
        } else {
            listener.onDisconnected();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int availability = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        switch (availability) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
                GooglePlayServicesUtil.getErrorDialog(availability, activity, REQUEST_CODE_AVAILABILITY).show();
                return false;
            default:
                return false;
        }
    }

    public GoogleApiClient getApiClient() {
        return apiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "GooglePlacesServicesHelper onConnected()");
        listener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        listener.onDisconnected();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                connect();
            }
        }else {
            listener.onDisconnected();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data, Context context){
        if (requestCode == REQUEST_CODE_RESOLUTION || requestCode == REQUEST_CODE_AVAILABILITY){

            if (resultCode == RESULT_OK){
                connect();
            }else {
                listener.onDisconnected();
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK){
            Place place = PlacePicker.getPlace(context, data);
            if (place == null){
                Log.e(TAG, "No place selected");
                return;
            }
            String placeId = place.getId();
            String placeAddress = (String) place.getAddress();
            Float rating  = place.getRating();
            Log.e(TAG, "Place rating : " + rating + ", and place address : " + placeAddress);
        }
    }
}
