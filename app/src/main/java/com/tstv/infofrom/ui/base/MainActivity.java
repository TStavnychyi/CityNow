package com.tstv.infofrom.ui.base;

import android.os.Bundle;

import com.tstv.infofrom.ui.temperature.TemperatureFragment;

public class MainActivity extends BaseActivity implements MainView {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(new TemperatureFragment());
    }

   /* @Override
    protected Fragment createFragment() {
        return new PlacesFragment();
    }*/
}
