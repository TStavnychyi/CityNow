package com.tstv.infofrom.ui.base;

import android.support.v4.app.Fragment;

import com.tstv.infofrom.ui.places.PlacesFragment;

public class MainActivity extends BaseActivity implements MainView {

    @Override
    protected Fragment createFragment() {
        return new PlacesFragment();
    }
}
