package com.tstv.infofrom.ui.base;

import android.os.Bundle;

import com.tstv.infofrom.ui.weather.WeatherFragment;

public class MainActivity extends BaseActivity implements MainView {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(new WeatherFragment());
    }

   /* @Override
    protected Fragment createFragment() {
        return new PlacesFragment();
    }*/
}
