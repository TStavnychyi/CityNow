package com.tstv.infofrom.ui.temperature;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.weather.Weather;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 15.09.2017.
 */

public class TemperatureFragment extends BaseFragment implements TemperatureView {

    @BindView(R.id.tv_temp_city)
    protected TextView tv_temp_city;

    @BindView(R.id.tv_temp_humidity)
    protected TextView tv_temp_humidity;

    @BindView(R.id.tv_temp_wind_speed)
    protected TextView tv_temp_wind_speed;

    @BindView(R.id.iv_temp_background_image)
    protected ImageView iv_background_image;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_temp_description)
    protected TextView tv_temp_description;

    @BindView(R.id.tv_temp_feels_like)
    protected TextView tv_temp_feels_like;

    @BindView(R.id.tv_temp_temp)
    protected TextView tv_temp;

    @BindView(R.id.tv_temp_time)
    protected TextView tv_temp_time;

    protected ProgressBar mProgressBar;

    @InjectPresenter
    TemperaturePresenter presenter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupSwipeToRefreshLayout(view);

       // presenter = onCreateTempPresenter();
        presenter.loadStart();
    }

    private void setupSwipeToRefreshLayout(View rootView){
        mSwipeRefreshLayout.setOnRefreshListener(() -> presenter.loadRefresh());

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mProgressBar = getBaseActivity().getProgressBar();

    }

    @Override
    protected int getMainContentLayout() {
        return R.layout.fragment_temperature;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return presenter;
    }

    @Override
    public int onCreateToolbarTitle() {
        return 0;
    }


    @Override
    public void showDataProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDataProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(Weather data) {
        tv_temp.setText(data.getCurrent().getTempC().intValue() + " \u2103");
        tv_temp_description.setText(data.getCurrent().getCondition().getText());
        tv_temp_time.setText("Local time: " + Utils.convertDateToTime(data.getLocation().getLocaltime()));
        tv_temp_feels_like.setText("Feels like " + data.getCurrent().getFeelslikeC().intValue() + " \u2103");
        tv_temp_city.setText(data.getLocation().getName() + ", " + data.getLocation().getCountry());
        tv_temp_humidity.setText("Humidity " + data.getCurrent().getHumidity() + " \u0025");
        tv_temp_wind_speed.setText("Wind speed " + data.getCurrent().getWindKph() + " Kph");
        Utils.backgroundImage(data.getCurrent().getIsDay(), iv_background_image);

    }

   // protected abstract TemperaturePresenter onCreateTempPresenter();


}
