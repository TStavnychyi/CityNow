package com.tstv.infofrom.ui.places;

import com.tstv.infofrom.model.places.PlacePrediction;
import com.tstv.infofrom.ui.base.BaseView;

/**
 * Created by tstv on 20.09.2017.
 */

public interface PlacesView extends BaseView {

    void setLocationData(PlacePrediction data);

    void showRecyclerViewProgress();

    void hiderRecyclerViewProgress();
}