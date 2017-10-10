package com.tstv.infofrom.di.module;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.tstv.infofrom.di.scopes.PlacesScope;
import com.tstv.infofrom.ui.places.PlacesAdapter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tstv on 22.09.2017.
 */

@Module
public class ActivityModule {

    @Provides
    @PlacesScope
    PlacesAdapter providePlacesAdapter(){
        return new PlacesAdapter();
    }

    @Provides
        // @PlacesScope
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity){
        return new LinearLayoutManager(activity);
    }
}
