package com.tstv.infofrom.ui.base;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.common.manager.MyFragmentManager;

import javax.inject.Inject;

/**
 * Created by tstv on 15.09.2017.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    @Inject
    MyFragmentManager mMyFragmentManager;


    public MainPresenter(){
        MyApplication.getApplicationComponent().inject(this);
    }



}
