package com.tstv.infofrom.ui.places.detail_places;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.tstv.infofrom.ui.base.BaseActivity;

public class PlacesDetailActivity extends BaseActivity {

    private static final String EXTRA_PLACE_ID = "place_id";
    private static final String EXTRA_PLACE_PHOTO_URL = "place_photo";

    public static Intent newIntent(Context packageContext, String id, String photoUrl) {
        Intent intent = new Intent(packageContext, PlacesDetailActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, id);
        intent.putExtra(EXTRA_PLACE_PHOTO_URL, photoUrl);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String id = getIntent().getStringExtra(EXTRA_PLACE_ID);
        String photoUrl = getIntent().getStringExtra(EXTRA_PLACE_PHOTO_URL);
        return PlacesDetailFragment.newInstance(id, photoUrl);
    }
}
