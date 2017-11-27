package com.tstv.infofrom.ui.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.weather.Weather;
import com.tstv.infofrom.rest.api.WeatherApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 15.09.2017.
 */

public class WeatherFragment extends BaseFragment implements WeatherView {

    public static final String TAG = WeatherFragment.class.getSimpleName();

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

    @BindView(R.id.weather_parent_view)
    RelativeLayout mParentView;

    @BindView(R.id.tv_temp_time)
    protected TextView tv_temp_time;

    @InjectPresenter
    WeatherPresenter mPresenter;

    @Inject
    WeatherApi mWeatherApi;

    private boolean isNetworkConnected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBaseActivity().showDataProgress();

        isNetworkConnected = getBaseActivity().isNetworkConnected();

        MyApplication.get().getActivityComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        if (isNetworkConnected) {
            if (MyApplication.getCurrentLtdLng() != null && MyApplication.getCurrentLtdLng().length != 0) {
                Log.e(TAG, "Current City" + MyApplication.getCurrentCity());
                mPresenter.loadVariables(mWeatherApi, MyApplication.getCurrentLtdLng());
                mPresenter.loadStart();
            } else {
                showSnackBar(SnackBarType.NetworkDisabled);
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupSwipeToRefreshLayout(view);
    }

    public static WeatherFragment newInstance() {
        Bundle args = new Bundle();
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setBackgroundImage(int isDay, ImageView imageView) {
        switch (isDay) {
            case 0:
                imageView.setImageResource(R.drawable.picture_weather_night);
                break;
            case 1:
                imageView.setImageResource(R.drawable.picture_weather_day);
                break;
        }
    }

    private void setupSwipeToRefreshLayout(View rootView) {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
                    if (Utils.isNetworkAvailableAndConnected(getContext())) {
                        mPresenter.loadRefresh();
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        showSnackBar(SnackBarType.NetworkDisabled);
                    }
                }
        );

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public Fragment getFragmentInstance() {
        Bundle args = new Bundle();
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPresenter;
    }

    @Override
    protected View getParentLayout() {
        return mParentView;
    }

    @Override
    public void showDataProgress() {
        getBaseActivity().showDataProgress();
    }

    @Override
    public void hideDataProgress() {
        getBaseActivity().hideDataProgress();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(Weather data) {
        if (data != null) {
            tv_temp.setText(data.getCurrent().getTempC().intValue() + " \u2103");
            tv_temp_description.setText(data.getCurrent().getCondition().getText());
            tv_temp_time.setText("Local time: " + Utils.convertDateToTime(data.getLocation().getLocaltimeEpoch()));
            tv_temp_feels_like.setText("Feels like " + data.getCurrent().getFeelslikeC().intValue() + " \u2103");
            tv_temp_city.setText(MyApplication.getCurrentCity() + ", " + data.getLocation().getCountry());
            tv_temp_humidity.setText("Humidity " + data.getCurrent().getHumidity() + " \u0025");
            tv_temp_wind_speed.setText("Wind speed " + data.getCurrent().getWindKph() + " Kph");
            setBackgroundImage(data.getCurrent().getIsDay(), iv_background_image);
        }

    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
