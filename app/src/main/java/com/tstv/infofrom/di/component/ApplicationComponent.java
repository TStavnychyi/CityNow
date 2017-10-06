package com.tstv.infofrom.di.component;

import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.di.module.ActivityModule;
import com.tstv.infofrom.di.module.ApplicationModule;
import com.tstv.infofrom.di.module.ManagerModule;
import com.tstv.infofrom.di.module.RestModule;
import com.tstv.infofrom.ui.base.BaseActivity;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.MainActivity;
import com.tstv.infofrom.ui.base.MainPresenter;
import com.tstv.infofrom.ui.temperature.TemperaturePresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by tstv on 15.09.2017.
 */

@Singleton
@Component (
        modules = {ApplicationModule.class, ManagerModule.class, RestModule.class, ActivityModule.class})
public interface ApplicationComponent {

    //application
    void inject(MyApplication application);

    //activities
    void inject(BaseActivity activity);
    void inject(MainActivity activity);

    //fragments
    void inject(BaseFragment fragment);
    //presenters
    void inject(MainPresenter presenter);
    void inject(TemperaturePresenter presenter);
  //  void inject(PlacesPresenter presenter);

    //holders



}
