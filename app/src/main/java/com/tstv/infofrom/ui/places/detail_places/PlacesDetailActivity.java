package com.tstv.infofrom.ui.places.detail_places;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.utils.NetworkUtils;
import com.tstv.infofrom.ui.base.MainView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesDetailActivity extends MvpAppCompatActivity implements MainView {

    private static final String EXTRA_PLACE_ID = "place_id";
    private static final String EXTRA_PLACE_PHOTO_URL = "place_photo";

    @BindView(R.id.progress_bar_places)
    ProgressBar mProgressBar;

    private boolean isNetworkConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_detail);

        isNetworkConnected = NetworkUtils.isNetworkConnected(this);
        ButterKnife.bind(this);

        if (isNetworkConnected) {

            String id = getIntent().getStringExtra(EXTRA_PLACE_ID);
            byte[] byteArray = getIntent().getByteArrayExtra(EXTRA_PLACE_PHOTO_URL);
            launchFragment(PlacesDetailFragment.newInstance(id, byteArray));
        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
        }
    }

    public ProgressBar getPlacesProgressBar() {
        return mProgressBar;
    }


    public static Intent newIntent(Context packageContext, String id, byte[] byteArray) {
        Intent intent = new Intent(packageContext, PlacesDetailActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, id);
        intent.putExtra(EXTRA_PLACE_PHOTO_URL, byteArray);
        return intent;
    }

    private void launchFragment(Fragment target) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.places_detail_container);

        if (fragment == null) {
            fragment = target;
            fm.beginTransaction()
                    .add(R.id.places_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void showWeatherFragment() {

    }

    @Override
    public void showPlacesFragment() {

    }

    @Override
    public void showMapFragment() {

    }

}
