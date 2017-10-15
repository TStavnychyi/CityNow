package com.tstv.infofrom.ui.places.detail_places;

import com.tstv.infofrom.model.places.detail_places.Result;
import com.tstv.infofrom.ui.base.BaseView;

/**
 * Created by tstv on 14.10.2017.
 */

public interface PlacesDetailView extends BaseView {

    void setData(Result result);

    void setPhotoUrl(String url);
}
