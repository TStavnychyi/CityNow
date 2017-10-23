package com.tstv.infofrom.ui.weather;

import com.tstv.infofrom.model.weather.Weather;
import com.tstv.infofrom.ui.base.BaseView;

/**
 * Created by tstv on 21.09.2017.
 */

public interface WeatherView extends BaseView {

    void setData(Weather data);

}
