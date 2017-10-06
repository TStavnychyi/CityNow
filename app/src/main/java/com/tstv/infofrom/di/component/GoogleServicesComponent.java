package com.tstv.infofrom.di.component;

import com.tstv.infofrom.di.module.ActivityModule;
import com.tstv.infofrom.di.module.GoogleServicesModule;
import com.tstv.infofrom.di.module.RestModule;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.places.PlacesFragment;
import com.tstv.infofrom.ui.places.PlacesPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by tstv on 25.09.2017.
 */

@Singleton
@Component (
        modules = {GoogleServicesModule.class, ActivityModule.class, RestModule.class})

public interface GoogleServicesComponent {

    void inject(PlacesPresenter presenter);
    void inject(PlacesFragment fragment);

    void inject(BaseFragment activity);
}
