package com.tstv.infofrom.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;


/**
 * Created by tstv on 15.09.2017.
 */

public abstract class BaseFragment extends MvpAppCompatFragment {

    protected BasePresenter mBasePresenter;

    @LayoutRes
    protected abstract int getMainContentLayout();

    protected abstract BasePresenter getBasePresenter();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasePresenter = getBasePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getMainContentLayout(), container, false);
    }

    public String createToolbarTitle(Context context) {
       // return context.getString(onCreateToolbarTitle());
        return "Hello";
    }

    @StringRes
    public abstract int onCreateToolbarTitle();

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
