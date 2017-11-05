package com.tstv.infofrom.di.module;

import android.app.Activity;

import com.google.android.gms.location.LocationRequest;
import com.tstv.infofrom.common.google.GoogleLocationServicesHelper;
import com.tstv.infofrom.di.scopes.RoadTrafficScope;
import com.tstv.infofrom.ui.base.BaseFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 24.10.2017.
 */

@Module
public class GoogleLocationServicesModule {

    private BaseFragment mFragmentListener;

    private Activity mActivity;

    public GoogleLocationServicesModule(BaseFragment fragmentListener, Activity activity) {
        mFragmentListener = fragmentListener;
        mActivity = activity;
    }

    @Provides
    @RoadTrafficScope
    GoogleLocationServicesHelper provideGoogleLocationServicesHelper() {
        return new GoogleLocationServicesHelper(mActivity, (GoogleLocationServicesHelper.GoogleLocationServicesListener) mFragmentListener);
    }

    @Provides
    @RoadTrafficScope
    LocationRequest provideLocationRequest() {
        return new LocationRequest();
    }


}
