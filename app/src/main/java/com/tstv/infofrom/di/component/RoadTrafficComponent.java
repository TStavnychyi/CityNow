package com.tstv.infofrom.di.component;

import com.tstv.infofrom.di.module.GoogleLocationServicesModule;
import com.tstv.infofrom.di.scopes.RoadTrafficScope;
import com.tstv.infofrom.ui.road_traffic.RoadTrafficFragment;

import dagger.Component;

/**
 * Created by tstv on 24.10.2017.
 */

@Component(dependencies = ApplicationComponent.class, modules = {GoogleLocationServicesModule.class})
@RoadTrafficScope
public interface RoadTrafficComponent {

    void inject(RoadTrafficFragment fragment);
}
