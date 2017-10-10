package com.tstv.infofrom.di.component;

import com.tstv.infofrom.di.module.ActivityModule;
import com.tstv.infofrom.di.module.GoogleServicesModule;
import com.tstv.infofrom.di.module.RestModule;
import com.tstv.infofrom.di.scopes.PlacesScope;
import com.tstv.infofrom.ui.places.PlacesFragment;

import dagger.Component;

/**
 * Created by tstv on 10.10.2017.
 */

@Component(dependencies = ApplicationComponent.class, modules = {GoogleServicesModule.class, ActivityModule.class, RestModule.class})
@PlacesScope
public interface PlaceComponent {

    //Targets
    void inject(PlacesFragment fragment);
}
