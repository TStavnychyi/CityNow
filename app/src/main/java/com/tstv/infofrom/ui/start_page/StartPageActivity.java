package com.tstv.infofrom.ui.start_page;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.common.utils.CommonUtils;
import com.tstv.infofrom.common.utils.NetworkUtils;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.auto_complete.CityPrediction;
import com.tstv.infofrom.ui.base.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.subjects.BehaviorSubject;

import static com.tstv.infofrom.common.google.GooglePlacesServicesHelper.REQUEST_CODE_AVAILABILITY;
import static com.tstv.infofrom.common.google.GooglePlacesServicesHelper.REQUEST_CODE_RESOLUTION;

public class StartPageActivity extends MvpAppCompatActivity implements StartPageView, GooglePlacesServicesHelper.GoogleServicesListener {

    private static final String TAG = StartPageActivity.class.getSimpleName();

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;

    private final String[] locationPermission = {
            Manifest.permission.ACCESS_FINE_LOCATION};

    @BindView(R.id.et_main_page)
    AutoCompleteTextView mEtByCitySearch;

    @BindView(R.id.btn_main_page_current_location)
    Button mBtnByCurrentLocation;

    @BindView(R.id.activity_main_page)
    RelativeLayout mParentLayout;

    @BindView(R.id.btn_main_page_autocomplete_clear)
    Button mBtnAutoCompleteClear;

    @OnClick(R.id.btn_main_page_autocomplete_clear)
    public void onAutoCompleteClear() {
        mEtByCitySearch.setText("");
    }

    @OnClick(R.id.btn_main_page_current_location)
    public void onCurrentLocationClick() {
        if (Utils.isNetworkAvailableAndConnected(this)) {
            showDataProgress();
            if (ContextCompat.checkSelfPermission(StartPageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartPageActivity.this,
                        locationPermission, REQUEST_LOCATION_PERMISSIONS);
            } else {
                mPresenter.getCurrentLocation(this, mLocationListener);
            }
        } else {
            showSnackBarProblem(ProblemType.NetworkDisabled);
        }
    }

    @Inject
    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    @InjectPresenter
    StartPagePresenter mPresenter;

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;

    private PlacesAutoCompleteAdapter mAdapter;

    boolean mIsGooglePlayServicesConnected = false;

    private boolean isNetworkConnected;

    private MyLocationListener mLocationListener = new MyLocationListener() {
        @Override
        public void locationIsReady(Location location) {
            mPresenter.rxGetCurrentLocation(location, StartPageActivity.this);
        }
    };

    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;

    private BehaviorSubject<Place> placeSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        ButterKnife.bind(this);

        MyApplication.get().plusActivityComponent(this, this);

        MyApplication.get().getActivityComponent().inject(this);

        mGoogleApiClient = mGooglePlacesServicesHelper.getApiClient();

        mPresenter.loadVariables(mGooglePlacesServicesHelper);

        mAdapter = new PlacesAutoCompleteAdapter(this, R.layout.item_autocomplete);

        isNetworkConnected = NetworkUtils.isNetworkConnected(this);

        if (isNetworkConnected) {
            mGooglePlacesServicesHelper.connect();

        } else {
            showSnackBarProblem(ProblemType.NetworkDisabled);
        }

        setAutoCompleteTextViewComponents();

        createAutoCompleteSubject();
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
                    mPresenter.getCurrentLocation(this, mLocationListener);
                } else {
                    showSnackBarProblem(ProblemType.Location);
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

    @Override
    public void showDataProgress() {
        hideDataProgress();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    @Override
    public void hideDataProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showNetworkSnackBar() {
        Snackbar snackbar = Snackbar.make(mParentLayout, getString(R.string.internet_turned_off_error)
                , Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.internet_turned_off_action, v -> {
            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
        });
        snackbar.show();
    }

    private void showLocationSnackBar() {
        Snackbar snackbar = Snackbar.make(mParentLayout, getString(R.string.location_turned_off_error)
                , Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.location_turned_off_action, v -> {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        });
        snackbar.show();
    }

    private void setAutoCompleteTextViewComponents() {
        if (mThreadHandler == null) {
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            mThreadHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        ArrayList<CityPrediction> results = (ArrayList<CityPrediction>) mAdapter.resultList;

                        if (results != null && results.size() > 0) {
                            runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                        } else {
                            runOnUiThread(() -> mAdapter.notifyDataSetInvalidated());
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
                if (Utils.isNetworkAvailableAndConnected(StartPageActivity.this)) {
                    mThreadHandler.removeCallbacksAndMessages(null);
                    mThreadHandler.postDelayed(() -> {
                        mAdapter.resultList = mPresenter.autocomplete(s.toString());
                        if (mAdapter.resultList.size() > 0) {
                            mAdapter.resultList.add(null);
                            mThreadHandler.sendEmptyMessage(1);
                        }

                    }, 500);
                } else {
                    showSnackBarProblem(ProblemType.NetworkDisabled);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtByCitySearch.setOnItemClickListener((parent, view, position, id) -> {
            CityPrediction chooseCity = (CityPrediction) parent.getItemAtPosition(position);
            mEtByCitySearch.setText(chooseCity.getDescription());
            showDataProgress();
            if (isNetworkConnected) {
                mPresenter.getAutoCompleteLocationData(chooseCity, placeSubject);
            } else {
                showSnackBarProblem(ProblemType.NetworkDisabled);
            }
        });

    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void createAutoCompleteSubject() {
        placeSubject
                .subscribe(placeObj -> {
                    LatLng latLng = placeObj.getLatLng();
                    Double[] coordinates = {latLng.latitude, latLng.longitude};
                    if (coordinates != null) {
                        MyApplication.setCurrentLtdLng(coordinates);
                        MyApplication.setCurrentCountryCode(Utils.getCountryCodeFromLatLng(coordinates, this));
                    }
                    hideDataProgress();
                    startMainActivity();
                });
    }

    @Override
    public void showSnackBarProblem(StartPageView.ProblemType problemType) {
        switch (problemType) {
            case NetworkDisabled:
                showNetworkSnackBar();
                break;
            case Location:
                showLocationSnackBar();
                break;
        }
    }

    public class PlacesAutoCompleteAdapter extends ArrayAdapter<CityPrediction> implements Filterable {

        List<CityPrediction> resultList;

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
                autocompleteTextView.setText(resultList.get(position).getDescription());
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
        public CityPrediction getItem(int position) {
            return resultList.get(position);
        }
    }
}
