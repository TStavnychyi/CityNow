package com.tstv.infofrom.common.google;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by tstv on 24.10.2017.
 */

public class GoogleLocationServicesHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLUTION = -103;
    private static final int REQUEST_CODE_AVAILABILITY = -102;

    private Activity activity;
    private GoogleLocationServicesHelper.GoogleLocationServicesListener listener;
    private GoogleApiClient apiClient;

    public interface GoogleLocationServicesListener {
        void onConnected();

        void onDisconnected();
    }

    public GoogleLocationServicesHelper(Activity activity, GoogleLocationServicesHelper.GoogleLocationServicesListener listener) {
        this.listener = listener;
        this.activity = activity;

        this.apiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    public void connect() {
        if (isGooglePlayServicesAvailable()) {
            apiClient.connect();
        } else {
            listener.onDisconnected();
        }
    }

    public void disconnect() {
        if (isGooglePlayServicesAvailable()) {
            apiClient.disconnect();
        } else {
            listener.onDisconnected();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        listener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        listener.onDisconnected();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                connect();
            }
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
}
