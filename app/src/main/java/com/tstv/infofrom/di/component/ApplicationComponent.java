package com.tstv.infofrom.di.component;

import android.content.Context;

import com.tstv.infofrom.di.module.ApplicationModule;
import com.tstv.infofrom.di.module.RestModule;
import com.tstv.infofrom.ui.base.BaseActivity;
import com.tstv.infofrom.ui.base.MainPresenter;
import com.tstv.infofrom.ui.weather.WeatherFragment;
import com.tstv.infofrom.ui.weather.WeatherPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by tstv on 15.09.2017.
 */

@Singleton
@Component(
        modules = {ApplicationModule.class, RestModule.class})
public interface ApplicationComponent {

    //  @ApplicationContext
    Context context();

    //Targets
    //application
    //   void inject(MyApplication application);

    //activities
    void inject(BaseActivity activity);

    void inject(WeatherFragment fragment);
//    void inject(MainActivity activity);

    //fragments
    //   void inject(BaseFragment fragment);
    //presenters
    void inject(MainPresenter presenter);

    void inject(WeatherPresenter presenter);
    //  void inject(PlacesPresenter presenter);

    //holders


}
