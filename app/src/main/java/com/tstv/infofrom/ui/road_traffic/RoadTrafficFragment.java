package com.tstv.infofrom.ui.road_traffic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GoogleLocationServicesHelper;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by tstv on 23.10.2017.
 */

public class RoadTrafficFragment extends BaseFragment implements RoadTrafficView,
        GoogleLocationServicesHelper.GoogleLocationServicesListener, OnMapReadyCallback, LocationListener {

    public static final String TAG = "RoadTrafficFragment";

    private final String[] locationPermission = {
            Manifest.permission.ACCESS_FINE_LOCATION};

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Location mLastLocation;
    private Marker mCurrentLocationMarker;
    private GoogleApiClient mGoogleApiClient;

    //   private ProgressBar mProgressBar;

    //if application was started by current location or by user's input
    private boolean mIsFromCurrentLocation;

    @Inject
    GoogleLocationServicesHelper mGoogleLocationServicesHelper;

    @Inject
    LocationRequest mLocationRequest;

    @InjectPresenter
    RoadTrafficPresenter mPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.get().plusRoadTrafficComponent(RoadTrafficFragment.this, getActivity()).inject(this);

        mIsFromCurrentLocation = MyApplication.isFromCurrentLocation();

        mGoogleApiClient = mGoogleLocationServicesHelper.getApiClient();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_road_traffic, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  mProgressBar = getBaseActivity().getProgressBar();

        mMapView = (MapView) view.findViewById(R.id.map);

        showDataProgress();

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        setCurrentLocationButtonPosition();
    }

    public static RoadTrafficFragment newInstance() {
        Bundle args = new Bundle();
        RoadTrafficFragment fragment = new RoadTrafficFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleLocationServicesHelper.disconnect();
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public Fragment getFragmentInstance() {
        Bundle args = new Bundle();
        RoadTrafficFragment fragment = new RoadTrafficFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPresenter;
    }

    @Override
    public void showDataProgress() {
        //   mProgressBar.setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.GONE);
    }

    @Override
    public void hideDataProgress() {
        //  mProgressBar.setVisibility(View.GONE);
        mMapView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackBar(SnackBarType snackBarType) {

    }

    @Override
    public void onConnected() {
        /*if (mIsFromCurrentLocation) {
            //  mLocationRequest.setInterval(1000);
            //mLocationRequest.setFastestInterval(1000);
            if (mLocationRequest != null) {
                mLocationRequest.setSmallestDisplacement(10);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
                //   Toast.makeText(getBaseActivity(), "Google Services is Available now", Toast.LENGTH_SHORT).show();
            }
        }else {*/
        Double[] coordinates = MyApplication.getCurrentLtdLng();
        LatLng latLng = createLatLng(coordinates[0], coordinates[1]);
        setMarkerOnTheMap(latLng);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // }

    }

    @Override
    public void onDisconnected() {
        Log.e("TAG", "Not available now");
        mLocationRequest = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.e("TAG", "onLocationChanged");
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }

        LatLng latLng = createLatLng(location.getLatitude(), location.getLongitude());
        setMarkerOnTheMap(latLng);

        //moving map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private LatLng createLatLng(double lat, double lon) {
        return new LatLng(lat, lon);
    }

    private void setMarkerOnTheMap(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("current position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());
        mGoogleMap = googleMap;
        hideDataProgress();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setTrafficEnabled(true);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient.connect();
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            mGoogleLocationServicesHelper.connect();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the location permissions")
                        .setPositiveButton("OK", (dialog, which) -> {
                            requestPermissions(locationPermission, MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                requestPermissions(locationPermission, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private void setCurrentLocationButtonPosition() {
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }
}

