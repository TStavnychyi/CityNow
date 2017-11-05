package com.tstv.infofrom.di.component;

import com.tstv.infofrom.di.module.ActivityModule;
import com.tstv.infofrom.di.module.GooglePlacesServicesModule;
import com.tstv.infofrom.di.module.RestModule;
import com.tstv.infofrom.di.scopes.PerActivity;
import com.tstv.infofrom.ui.base.BaseActivity;
import com.tstv.infofrom.ui.base.MainActivity;
import com.tstv.infofrom.ui.places.PlacesFragment;
import com.tstv.infofrom.ui.places.detail_places.PlacesDetailFragment;
import com.tstv.infofrom.ui.weather.WeatherFragment;

import dagger.Component;

/**
 * Created by tstv on 04.11.2017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, RestModule.class, GooglePlacesServicesModule.class})
public interface ActivityComponent {

    void inject(BaseActivity activity);

    void inject(MainActivity activity);

    void inject(WeatherFragment fragment);

    void inject(PlacesFragment fragment);

    void inject(PlacesDetailFragment fragment);
}
