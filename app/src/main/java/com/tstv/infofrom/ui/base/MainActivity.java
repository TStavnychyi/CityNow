package com.tstv.infofrom.ui.base;

import android.os.Bundle;
import android.util.Log;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.ui.places.PlacesFragment;

public class MainActivity extends BaseActivity implements MainView {

    @InjectPresenter
    MainPresenter mMainPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "MainActivity");

        MyApplication.getApplicationComponent().inject(this);

        //setContent(new TemperatureFragment());
        setContent(new PlacesFragment());

    }

    @Override
    protected int getMainContentLayout() {
        return R.layout.activity_main;
    }
}
