package com.tstv.infofrom.ui.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.common.manager.MyFragmentManager;
import com.tstv.infofrom.common.utils.CommonUtils;
import com.tstv.infofrom.common.utils.NetworkUtils;

import javax.inject.Inject;

import static com.tstv.infofrom.common.google.GooglePlacesServicesHelper.REQUEST_CODE_AVAILABILITY;
import static com.tstv.infofrom.common.google.GooglePlacesServicesHelper.REQUEST_CODE_RESOLUTION;

/**
 * Created by tstv on 15.09.2017.
 */

public abstract class BaseActivity extends MvpAppCompatActivity implements BaseView, BaseFragment.Callback, GooglePlacesServicesHelper.GoogleServicesListener {

    public static final int PLACE_PICKER_REQUEST = 1;
    //  private static final int REQUEST_LOCATION_PERMISSIONS = 2;

   /* @BindView(R.id.progress_bar)
    protected ProgressBar mProgressBar;*/

    private ProgressDialog mProgressDialog;

    /*@BindView(R.id.bottom_navigation_menu)
    protected BottomNavigationView mBottomNavigationMenu;*/

    // @Inject
    MyFragmentManager myFragmentManager;

    @Inject
    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    boolean mIsGooglePlayServicesConnected = false;

/*
    protected abstract Fragment createFragment();
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("BaseActivity", "isNetworkConnected");
        MyApplication.get().plusActivityComponent(this, this);

        MyApplication.get().getActivityComponent().inject(this);

        mGooglePlacesServicesHelper.connect();

      /*  mBottomNavigationMenu.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_nav_weather:
                    setContent(new WeatherFragment());
                    Log.e("TAG", "Weather");
                    break;
                case R.id.item_nav_places:
                    setContent(new PlacesFragment());
                    Log.e("TAG", "Place");
                    break;
                case R.id.item_nav_road_traffic:
                    setContent(new RoadTrafficFragment());
                    break;
            }
            return true;
        });*/

        FrameLayout parent = (FrameLayout) findViewById(R.id.main_wrapper);
        getLayoutInflater().inflate(R.layout.activity_main, parent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "BaseActivity onDestroy");
        mGooglePlacesServicesHelper.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLUTION || requestCode == REQUEST_CODE_AVAILABILITY) {
            mGooglePlacesServicesHelper.handleActivityResult(requestCode, resultCode, data, getBaseActivity());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected() {
        mIsGooglePlayServicesConnected = true;
    }

    @Override
    public void onDisconnected() {
        mIsGooglePlayServicesConnected = false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {

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
        if (message != null) {
            showSnackBar(message);
        } else {
            showSnackBar(getString(R.string.some_error));
        }
    }

    @Override
    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_LONG).show();
        }
    }

    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }


    public void setFullscreenLayout() {
        View decor = getWindow().getDecorView();
        int flags = decor.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decor.setSystemUiVisibility(flags);
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

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    public GooglePlacesServicesHelper getGooglePlacesServicesHelper() {
        return mGooglePlacesServicesHelper;
    }

    public void fragmentOnScreen(BaseFragment baseFragment) {
        setToolbarTitle(baseFragment.createToolbarTitle(this));
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

   /* private void launchFragment(Fragment target){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main_wrapper);

        if (fragment == null) {
            fragment = target;
            fm.beginTransaction()
                    .add(R.id.main_wrapper, fragment)
                    .commit();
        }
    }*/

    public void setContent(BaseFragment fragment) {
        myFragmentManager.setFragment(this, fragment, R.id.main_wrapper);
    }

    public void addContent(BaseFragment fragment) {
        myFragmentManager.addFragment(this, fragment, R.id.main_wrapper);
    }

    public boolean removeCurrentFragment() {
        return myFragmentManager.removeCurrentFragment(this);
    }

    public boolean removeFragment(BaseFragment fragment) {
        return myFragmentManager.removeFragment(this, fragment);
    }

   /* @Override
    public void onBackPressed() {
        removeCurrentFragment();
    }*/

    public Activity getBaseActivity() {
        return this;
    }

    public boolean isGooglePlayServicesConnected() {
        return mIsGooglePlayServicesConnected;
    }
}
