package com.tstv.infofrom.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tstv.infofrom.R;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

/**
 * Created by tstv on 06.11.2017.
 */

public class NoInternetFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_no_internet, container, false);

        return view;
    }

    @Override
    protected int getMainContentLayout() {
        return 0;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return null;
    }

    @Override
    public int onCreateToolbarTitle() {
        return 0;
    }

    @Override
    public String TAG() {
        return "NoInternetFragment";
    }

    @Override
    public Fragment getFragmentInstance() {
        return null;
    }

    public static NoInternetFragment newInstance() {
        return new NoInternetFragment();
    }
}
