package com.tstv.infofrom.ui.places.search_places;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchPlacesActivity extends MvpAppCompatActivity {

    @BindView(R.id.progress_bar_places)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_detail);

        ButterKnife.bind(this);

        launchFragment(new SearchPlacesFragment());
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
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
}
