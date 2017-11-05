package com.tstv.infofrom.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.tstv.infofrom.R;
import com.tstv.infofrom.ui.places.PlacesFragment;
import com.tstv.infofrom.ui.road_traffic.RoadTrafficFragment;
import com.tstv.infofrom.ui.weather.WeatherFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainView {

    @BindView(R.id.bottom_navigation_menu)
    protected BottomNavigationView mBottomNavigationMenu;

    private BaseFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        ButterKnife.bind(this);

        showWeatherFragment();

        mBottomNavigationMenu.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.item_nav_weather:
                    showWeatherFragment();
                    break;
                case R.id.item_nav_places:
                    showPlacesFragment();
                    break;
                case R.id.item_nav_road_traffic:
                    showMapFragment();
                    break;
            }
            return true;
        });
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentAttached() {
        super.onFragmentAttached();
    }

    @Override
    public void onFragmentDetached(String tag) {
        Log.e("TAG", "onFragmentDetached");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .remove(fragment)
                    .commitNow();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(mCurrentFragment.TAG());
        if (fragment == null) {
            super.onBackPressed();
        } else {
            onFragmentDetached(mCurrentFragment.TAG());
        }
    }

    private void showFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(fragment.TAG()) != null) {
            Log.e("TAG", "showFragment ()" + fragment.TAG());
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(fragment.TAG())).commit();
        } else {
            Log.e("TAG", "add ()" + fragment.TAG());
            fragmentManager.beginTransaction().add(R.id.main_wrapper, fragment.getFragmentInstance(), fragment.TAG()).commit();
        }
    }

    private void hideFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(fragment.TAG()) != null) {
            Log.e("TAG", "hideFragment ()" + fragment.TAG());
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(fragment.TAG()))
                    .commit();
        }
    }

    @Override
    public void showWeatherFragment() {
        if (mCurrentFragment instanceof WeatherFragment) {

        } else {
            mCurrentFragment = WeatherFragment.newInstance();
            showFragment(mCurrentFragment);
            hideFragment(PlacesFragment.newInstance());
            hideFragment(RoadTrafficFragment.newInstance());
        }
    }

    @Override
    public void showPlacesFragment() {
        if (mCurrentFragment instanceof PlacesFragment) {

        } else {
            mCurrentFragment = PlacesFragment.newInstance();
            showFragment(mCurrentFragment);
            hideFragment(WeatherFragment.newInstance());
            hideFragment(RoadTrafficFragment.newInstance());

        }
    }

    @Override
    public void showMapFragment() {
        if (mCurrentFragment instanceof RoadTrafficFragment) {

        } else {
            mCurrentFragment = RoadTrafficFragment.newInstance();
            showFragment(mCurrentFragment);
            hideFragment(WeatherFragment.newInstance());
            hideFragment(PlacesFragment.newInstance());
        }
    }
}
