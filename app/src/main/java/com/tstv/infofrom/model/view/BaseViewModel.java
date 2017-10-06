package com.tstv.infofrom.model.view;

import android.support.annotation.LayoutRes;

import com.tstv.infofrom.R;

/**
 * Created by tstv on 21.09.2017.
 */

public abstract class BaseViewModel {

    public abstract LayoutTypes getType();

    public enum LayoutTypes{
        ITEM_RECCOMENDATION_PLACES(R.layout.item_recommendations_places);

        private final int id;

        LayoutTypes(int resIs){
            this.id = resIs;
        }

        @LayoutRes
        public int getValue(){
            return id;
        }
    }

}
