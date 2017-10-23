package com.tstv.infofrom.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.manager.MyFragmentManager;
import com.tstv.infofrom.ui.places.PlacesFragment;
import com.tstv.infofrom.ui.temperature.TemperatureFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 15.09.2017.
 */

public abstract class BaseActivity extends MvpAppCompatActivity {

    public static final int PLACE_PICKER_REQUEST = 1;
  //  private static final int REQUEST_LOCATION_PERMISSIONS = 2;

    @BindView(R.id.progress_bar)
    protected ProgressBar mProgressBar;

    @BindView(R.id.bottom_navigation_menu)
    protected BottomNavigationView mBottomNavigationMenu;

    @Inject
    MyFragmentManager myFragmentManager;

/*
    protected abstract Fragment createFragment();
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        MyApplication.getApplicationComponent().inject(this);

        ButterKnife.bind(this);

        mBottomNavigationMenu.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_nav_weather:
                    setContent(new TemperatureFragment());
                    Log.e("TAG", "Weather");
                    break;
                case R.id.item_nav_places:
                    setContent(new PlacesFragment());
                    Log.e("TAG", "Place");
                    break;
                case R.id.item_nav_road_traffic:
                    //TODO RoadTrafficFragment
                    break;
            }
            return true;
        });

        FrameLayout parent = (FrameLayout) findViewById(R.id.main_wrapper);
        getLayoutInflater().inflate(R.layout.activity_main, parent);

    }
    public ProgressBar getProgressBar() {
        return mProgressBar;
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

    @Override
    public void onBackPressed() {
        removeCurrentFragment();
    }

    public Activity getBaseActivity() {
        return this;
    }
}
