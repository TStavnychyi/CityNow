package com.tstv.infofrom.di.module;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.tstv.infofrom.ui.places.PlacesAdapter;
import com.tstv.infofrom.ui.places.PlacesPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 22.09.2017.
 */

@Module
public class ActivityModule {

    private PlacesPresenter mPlacesPresenter;

    @Provides
    @Singleton
    PlacesAdapter providePlacesAdapter(){
        return new PlacesAdapter();
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity){
        return new LinearLayoutManager(activity);
    }

    @Provides
    PlacesPresenter providePlacesPresenter(){
        return new PlacesPresenter();
    }


}
