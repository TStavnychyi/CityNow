package com.tstv.infofrom.ui.temperature;

import com.tstv.infofrom.model.weather.Weather;
import com.tstv.infofrom.ui.base.BaseView;

/**
 * Created by tstv on 21.09.2017.
 */

public interface TemperatureView extends BaseView {

    void setData(Weather data);

}
