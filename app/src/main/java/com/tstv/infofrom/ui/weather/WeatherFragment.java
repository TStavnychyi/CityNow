package com.tstv.infofrom.ui.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.weather.Weather;
import com.tstv.infofrom.rest.api.WeatherApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 15.09.2017.
 */

public class WeatherFragment extends BaseFragment implements WeatherView {

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;

    public static final String TAG = WeatherFragment.class.getSimpleName();

    @BindView(R.id.tv_temp_city)
    protected TextView tv_temp_city;

    @BindView(R.id.tv_temp_humidity)
    protected TextView tv_temp_humidity;

    @BindView(R.id.tv_temp_wind_speed)
    protected TextView tv_temp_wind_speed;

    @BindView(R.id.iv_temp_background_image)
    protected ImageView iv_background_image;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_temp_description)
    protected TextView tv_temp_description;

    @BindView(R.id.tv_temp_feels_like)
    protected TextView tv_temp_feels_like;

    @BindView(R.id.tv_temp_temp)
    protected TextView tv_temp;

    @BindView(R.id.tv_temp_time)
    protected TextView tv_temp_time;

    @InjectPresenter
    WeatherPresenter mPresenter;

    @Inject
    WeatherApi mWeatherApi;

    private final String[] locationPermission = {
            Manifest.permission.ACCESS_FINE_LOCATION};

    private boolean isNetworkConnected;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBaseActivity().showDataProgress();

        isNetworkConnected = getBaseActivity().isNetworkConnected();

        MyApplication.get().getActivityComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        if (isNetworkConnected) {
            if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(locationPermission, REQUEST_LOCATION_PERMISSIONS);
            } else {
                requestSingleUpdate();
            }
        } else {
            getBaseActivity().showMessage(getString(R.string.no_internet_connection_message));
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupSwipeToRefreshLayout(view);
    }

    public static WeatherFragment newInstance() {
        Bundle args = new Bundle();
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupSwipeToRefreshLayout(View rootView) {
        mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.loadRefresh());

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public Fragment getFragmentInstance() {
        Bundle args = new Bundle();
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getMainContentLayout() {
        return R.layout.fragment_temperature;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPresenter;
    }

    @Override
    public int onCreateToolbarTitle() {
        return 0;
    }

    @Override
    public void showDataProgress() {
        getBaseActivity().showDataProgress();
    }

    @Override
    public void hideDataProgress() {
        getBaseActivity().hideDataProgress();
    }

    @Override
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(Weather data) {
        tv_temp.setText(data.getCurrent().getTempC().intValue() + " \u2103");
        tv_temp_description.setText(data.getCurrent().getCondition().getText());
        tv_temp_time.setText("Local time: " + Utils.convertDateToTime(data.getLocation().getLocaltime()));
        tv_temp_feels_like.setText("Feels like " + data.getCurrent().getFeelslikeC().intValue() + " \u2103");
        tv_temp_city.setText(data.getLocation().getName() + ", " + data.getLocation().getCountry());
        tv_temp_humidity.setText("Humidity " + data.getCurrent().getHumidity() + " \u0025");
        tv_temp_wind_speed.setText("Wind speed " + data.getCurrent().getWindKph() + " Kph");
        Utils.backgroundImage(data.getCurrent().getIsDay(), iv_background_image);

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

    private void requestSingleUpdate() throws SecurityException {
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e("TAG", "Location : " + location.getLatitude());
                    getLocationData(location);
                    if (MyApplication.getCurrentCity() != null && !MyApplication.getCurrentCity().isEmpty()) {
                        mPresenter.loadVariables(mWeatherApi, MyApplication.getCurrentCity());
                        mPresenter.loadStart();
                    } else {
                        Toast.makeText(getActivity(), "Can't find current location", Toast.LENGTH_SHORT).show();
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
                    getBaseActivity().hideDataProgress();
                    Toast.makeText(getBaseActivity(), "Make sure to enable Internet on your phone", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }
    }

    private void getLocationData(Location location) {
        Double[] coordinates = {location.getLatitude(), location.getLongitude()};
        MyApplication.setCurrentLtdLng(coordinates);
        String city = Utils.getCityFromLatLng(coordinates, getContext());
        MyApplication.setCurrentCity(city);
       /* String country = Utils.getCountryCodeFromLatLng(coordinates, getContext());
        MyApplication.setCurrentCountry(country);*/
    }

}
