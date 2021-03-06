package com.tstv.infofrom.di.module;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.common.utils.CommonUtils;
import com.tstv.infofrom.di.scopes.ActivityContext;
import com.tstv.infofrom.ui.places.PlacesAdapter;
import com.tstv.infofrom.ui.places.search_places.SearchPlacesAdapter;

import dagger.Module;
import dagger.Provides;


/**
 * Created by tstv on 22.09.2017.
 */

@Module
public class ActivityModule {

    private MvpAppCompatActivity mActivity;

    public ActivityModule(MvpAppCompatActivity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    MvpAppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    PlacesAdapter providePlacesAdapter() {
        return new PlacesAdapter();
    }

    @Provides
    SearchPlacesAdapter provideSearchPlacesAdapter() {
        return new SearchPlacesAdapter();
    }

    @Provides
    ProgressDialog provideProgressDialog(Context context) {
        return CommonUtils.showLoadingDialog(context);
    }

    @Provides
    GridLayoutManager provideGridLayoutManager(MvpAppCompatActivity activity) {
        return new GridLayoutManager(activity, 2);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(MvpAppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }


}
