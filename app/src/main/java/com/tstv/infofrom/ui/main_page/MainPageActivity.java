package com.tstv.infofrom.ui.main_page;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.common.utils.CommonUtils;
import com.tstv.infofrom.common.utils.NetworkUtils;
import com.tstv.infofrom.common.utils.Utils;
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
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.tstv.infofrom.common.google.GooglePlacesServicesHelper.REQUEST_CODE_AVAILABILITY;
import static com.tstv.infofrom.common.google.GooglePlacesServicesHelper.REQUEST_CODE_RESOLUTION;

public class MainPageActivity extends MvpAppCompatActivity implements GooglePlacesServicesHelper.GoogleServicesListener {

    private static final String TAG = MainPageActivity.class.getSimpleName();

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;

    private final String[] locationPermission = {
            Manifest.permission.ACCESS_FINE_LOCATION};

    @BindView(R.id.et_main_page)
    AutoCompleteTextView mEtByCitySearch;

    @BindView(R.id.btn_main_page_current_location)
    Button mBtnByCurrentLocation;

    @OnClick(R.id.btn_main_page_current_location)
    public void onClick() {
        if (isNetworkConnected) {
            if (ContextCompat.checkSelfPermission(MainPageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainPageActivity.this,
                        locationPermission, REQUEST_LOCATION_PERMISSIONS);
            } else {
                getCurrentLocation();
            }
        } else {
            showMessage(getString(R.string.no_internet_connection_message));
        }
    }

    @Inject
    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    private ProgressDialog mProgressDialog;

    private PlacesAutoCompleteAdapter mAdapter;

    boolean mIsGooglePlayServicesConnected = false;

    private boolean isNetworkConnected;

    private MyLocationListener mLocationListener = this::rxGetCurrentLocation;

    private String API_KEY = "AIzaSyBegZt-KrWSxlhBpvvGbRyR8u0bPn7Xahc";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        ButterKnife.bind(this);

        MyApplication.get().plusActivityComponent(this, this);

        MyApplication.get().getActivityComponent().inject(this);

        mAdapter = new PlacesAutoCompleteAdapter(this, R.layout.item_autocomplete);

        isNetworkConnected = NetworkUtils.isNetworkConnected(this);

        if (isNetworkConnected) {
            mGooglePlacesServicesHelper.connect();
        } else {
            showMessage(getString(R.string.no_internet_connection_message));
        }

        setAutoCompleteComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    showMessage("Make sure you enable location permission");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLUTION || requestCode == REQUEST_CODE_AVAILABILITY) {
            mGooglePlacesServicesHelper.handleActivityResult(requestCode, resultCode, data, this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected() {
        MyApplication.setIsGooglePlacesServicesConnected(true);
        mIsGooglePlayServicesConnected = true;
    }

    @Override
    public void onDisconnected() {
        MyApplication.setIsGooglePlacesServicesConnected(false);
        mIsGooglePlayServicesConnected = false;
    }

    private void showDataProgress() {
        hideDataProgress();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    private void hideDataProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void setAutoCompleteComponents() {
        if (mThreadHandler == null) {
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            mThreadHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        ArrayList<String> results = (ArrayList<String>) mAdapter.resultList;

                        if (results != null && results.size() > 0) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.notifyDataSetInvalidated();
                        }
                    }
                }
            };
        }

        mEtByCitySearch.setThreshold(1);

        mEtByCitySearch.setAdapter(mAdapter);


        mEtByCitySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mThreadHandler.removeCallbacksAndMessages(null);
                mThreadHandler.postDelayed(() -> {
                    mAdapter.resultList = autocomplete(s.toString());
                    if (mAdapter.resultList.size() > 0) {
                        mAdapter.resultList.add("footer");
                        mThreadHandler.sendEmptyMessage(1);
                    }
                }, 500);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtByCitySearch.setOnItemClickListener((parent, view, position, id) -> {
            String chooseCity = (String) parent.getItemAtPosition(position);
            getAutoCompleteLocationData(chooseCity);
            startMainActivity();
        });
    }

    private void getCurrentLocation() throws SecurityException {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        mLocationListener.locationIsReady(location);
                    } else {
                        showMessage("Can't find current location!");
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
                    showMessage("Make sure to enable Internet on your phone");
                }
            }, null);
        }
    }

    private void rxGetCurrentLocation(Location location) {
        Observable.just(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((disposable -> showDataProgress()))
                .doFinally(() -> {
                    hideDataProgress();
                    startMainActivity();
                })
                .subscribe((this::getCurrentLocationData), error -> {
                    hideDataProgress();
                    showMessage(error.getMessage());

                });
    }

    private void getAutoCompleteLocationData(String cityName) {
        Double[] coordinates = Utils.getLatLngFromCityName(cityName, this);
        String country = Utils.getCountryCodeFromLatLng(coordinates, this);
        MyApplication.setCurrentCity(cityName);
        MyApplication.setCurrentCountry(country);
        MyApplication.setCurrentLtdLng(coordinates);
    }


    private void getCurrentLocationData(Location location) {
        Double[] coordinates = {location.getLatitude(), location.getLongitude()};
        MyApplication.setCurrentLtdLng(coordinates);
        String city = Utils.getCityFromLatLng(coordinates, this);
        MyApplication.setCurrentCity(city);
        String country = Utils.getCountryCodeFromLatLng(coordinates, this);
        MyApplication.setCurrentCountry(country);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

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
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

        List<String> resultList;

        Context mContext;

        int mResource;

        PlacesAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);

            mContext = context;
            mResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (position != (resultList.size() - 1)) {
                view = inflater.inflate(R.layout.item_autocomplete, null);
            } else {
                view = inflater.inflate(R.layout.item_powered_by_google, null);
            }

            if (position != (resultList.size() - 1)) {
                TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteTextView);
                autocompleteTextView.setText(resultList.get(position));
            } else {
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_powered_by_google);
            }

            return view;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return resultList.get(position);
        }
    }
}
