package com.tstv.infofrom.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.tstv.infofrom.ui.places.detail_places.PlacesDetailActivity;
import com.tstv.infofrom.ui.places.search_places.SearchPlacesActivity;


/**
 * Created by tstv on 15.09.2017.
 */

public abstract class BaseFragment extends MvpAppCompatFragment implements BaseView {

    protected BasePresenter mBasePresenter;

    private BaseActivity mActivity;
    private ProgressDialog mProgressDialog;

   /* @Inject
    WeatherApi mWeatherApi;

    @Inject
    DetailPlacesApi mDetailPlacesApi;

    @Inject
    PlacesPhotoFromReferenceApi mPlacesPhotoFromReferenceApi;

    @Inject
    NearbyPlacesApi mNearbyPlacesApi;*/

    @LayoutRes
    protected abstract int getMainContentLayout();

    protected abstract BasePresenter getBasePresenter();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setRetainInstance(true);
        setHasOptionsMenu(false);

        Log.e("TAG", "BaseFragment onCreate");

        //  MyApplication.getApplicationComponent().inject(this);
        mBasePresenter = getBasePresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "BaseFragment onDestroy");
    }

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getMainContentLayout(), container, false);
    }*/

    public String createToolbarTitle(Context context) {
       // return context.getString(onCreateToolbarTitle());
        return "Hello";
    }

    @StringRes
    public abstract int onCreateToolbarTitle();

    public abstract String TAG();

    public abstract Fragment getFragmentInstance();

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public PlacesDetailActivity getPlacesDetailActivity() {
        return (PlacesDetailActivity) getActivity();
    }

    public SearchPlacesActivity getSearchPlacesActivity() {
        return (SearchPlacesActivity) getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {

    }

    @Override
    public void showDataProgress() {

    }

    @Override
    public void hideDataProgress() {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showMessage(String message) {
        if (mActivity != null) {
            mActivity.showMessage(message);
        }
    }

   /* @Override
    public boolean isNetworkConnected() {
        return false;
    }*/

    @Override
    public void hideKeyboard() {
        mActivity.hideKeyboard();
    }

    public interface Callback {
        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }

    /* public WeatherApi getWeatherApi() {
        return mWeatherApi;
    }

    public DetailPlacesApi getDetailPlacesApi() {
        return mDetailPlacesApi;
    }

    public PlacesPhotoFromReferenceApi getPlacesPhotoFromReferenceApi() {
        return mPlacesPhotoFromReferenceApi;
    }

    public NearbyPlacesApi getNearbyPlacesApi() {
        return mNearbyPlacesApi;
    }*/
}
