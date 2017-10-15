package com.tstv.infofrom.ui.places;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GoogleServicesHelper;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.PlacePrediction;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 22.09.2017.
 */

public class PlacesFragment extends BaseFragment implements PlacesView, GoogleServicesHelper.GoogleServicesListener {

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    @BindView(R.id.iv_places_image_title)
    ImageView iv_places_image_title;

    @BindView(R.id.tv_places_title)
    TextView tv_places_title;

    @BindView(R.id.search_view_places)
    SearchView mSearchView;

    @BindView(R.id.rv_places)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_places_recommendations)
    TextView tv_text_above_rv;

    @BindView(R.id.progress_bar_recy_view)
    ProgressBar pb_recycler_view;

    @InjectPresenter
    PlacesPresenter mPlacesPresenter;

    @Inject
    PlacesAdapter mPlacesAdapter;

    @Inject
    NearbyPlacesApi mNearbyPlacesApi;

    @Inject
    GoogleServicesHelper mGoogleServicesHelper;

    @Inject
    LinearLayoutManager mLayoutManager;

    private Typeface mBoldItalicFont;

    protected ProgressBar mProgressBar;

    private static boolean isGooglePlayServicesAvailable;

    private final String[] locationPermission = {
            Manifest.permission.ACCESS_FINE_LOCATION};

    private boolean isInternetIsAvailable;

    boolean isGooglePlayServicesConnected;

    private boolean isLocationDataAlreadyUploaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.get().plusFragmentComponent(this, getBaseActivity()).inject(this);

        isInternetIsAvailable = Utils.isNetworkAvailableAndConnected(getContext());

        if (isInternetIsAvailable) {
            mGoogleServicesHelper.connect();
            mPlacesPresenter.loadVariables(mPlacesAdapter, mGoogleServicesHelper, mNearbyPlacesApi);
            mPlacesAdapter.setBaseActivity(getBaseActivity());
        } else {
            Toast.makeText(getContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isGooglePlayServicesAvailable = false;
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mPlacesAdapter);
        tv_places_title.setTypeface(getBoldItalicFont());
        mProgressBar = getBaseActivity().getProgressBar();

        mPlacesPresenter.loadStart();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPlacesPresenter.getInputFromUser(query);
                mPlacesPresenter.loadData(BasePresenter.ProgressType.TextAutoComplete, isLocationDataAlreadyUploaded);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        mSearchView.setOnSearchClickListener(v -> tv_text_above_rv.setText(R.string.text_above_rv_search_res));
        mSearchView.setOnCloseListener(() -> {
            tv_text_above_rv.setText(R.string.text_above_rv_default_data);
            mPlacesPresenter.setNearbyPlaces();
            mSearchView.clearFocus();
            return true;
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "Destroy");
        mGoogleServicesHelper.disconnect();
        MyApplication.get().clearFragmentComponent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleServicesHelper.handleActivityResult(requestCode, resultCode, data, getBaseActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        requestSingleUpdate();
                    }
                } else {

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnected() {
        isGooglePlayServicesAvailable = true;
        isGooglePlayServicesConnected = true;
        Toast.makeText(getBaseActivity(), "Google Services is Available now", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(locationPermission, REQUEST_LOCATION_PERMISSIONS);
        } else {
            requestSingleUpdate();
        }

       /* PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent i = builder.build(getBaseActivity());
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onDisconnected() {
        isGooglePlayServicesAvailable = false;
        isGooglePlayServicesConnected = false;
        isLocationDataAlreadyUploaded = false;
        Toast.makeText(getBaseActivity(), "Google Services is not Available now", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getMainContentLayout() {
        return R.layout.fragment_places;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPlacesPresenter;
    }

    @Override
    public int onCreateToolbarTitle() {
        return 0;
    }

    @Override
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {

    }

    @Override
    public void showDataProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        iv_places_image_title.setVisibility(View.GONE);
        tv_text_above_rv.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);
        tv_places_title.setVisibility(View.GONE);
    }

    @Override
    public void hideDataProgress() {
        mProgressBar.setVisibility(View.GONE);
        iv_places_image_title.setVisibility(View.VISIBLE);
        tv_text_above_rv.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.VISIBLE);
        tv_places_title.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRecyclerViewProgress() {
        pb_recycler_view.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hiderRecyclerViewProgress() {
        pb_recycler_view.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void getLocationData(Location location) {
        Double[] coordinates = {location.getLatitude(), location.getLongitude()};
        MyApplication.setCurrentLtdLng(coordinates);
        String city = Utils.getCityFromLatLng(coordinates, getContext());
        MyApplication.setCurrentCity(city);
    }

    @Override
    public void setLocationData(PlacePrediction data) {
        tv_places_title.setText(data.getPlaceName());
        Glide.with(getContext())
                .load(data.getImageUrl())
                .into(iv_places_image_title);
        isLocationDataAlreadyUploaded = true;
    }

    public static boolean isGoogleServicesAvailable() {
        return isGooglePlayServicesAvailable;
    }


    private void requestSingleUpdate() throws SecurityException {
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    getLocationData(location);
                    mPlacesPresenter.loadData(BasePresenter.ProgressType.DataProgress, isLocationDataAlreadyUploaded);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(getBaseActivity(), "Make sure to enable Internet on your phone", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }
    }

    private Typeface getBoldItalicFont() {
        return Typeface.createFromAsset(getActivity().getAssets(), "Roboto_BoldItalic.ttf");
    }
}