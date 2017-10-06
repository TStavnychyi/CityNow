package com.tstv.infofrom.ui.base;

import com.arellomobile.mvp.MvpPresenter;

/**
 * Created by tstv on 21.09.2017.
 */

public abstract class BasePresenter <V extends BaseView> extends MvpPresenter<V> {

    public abstract void loadStart();

    public abstract void loadRefresh();

    public abstract void onLoadingStart(ProgressType progressType);

    public abstract void onLoadingFinish(ProgressType progressType);

    public abstract void onLoadingFailed(Throwable throwable);

    public abstract void showProgress(ProgressType progressType);

    public abstract void hideProgress(ProgressType progressType);

    public enum ProgressType{
        Refreshing, DataProgress, TextAutoComplete
    }

}
