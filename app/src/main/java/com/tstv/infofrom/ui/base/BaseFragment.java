package com.tstv.infofrom.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.tstv.infofrom.ui.places.detail_places.PlacesDetailActivity;
import com.tstv.infofrom.ui.places.search_places.SearchPlacesActivity;


/**
 * Created by tstv on 15.09.2017.
 */

public abstract class BaseFragment extends MvpAppCompatFragment implements BaseView {

    protected BasePresenter mBasePresenter;

    private BaseActivity mActivity;

    protected abstract BasePresenter getBasePresenter();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        mBasePresenter = getBasePresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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

    @Override
    public void hideKeyboard() {
        mActivity.hideKeyboard();
    }

    public interface Callback {
        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }
}
