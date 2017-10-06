package com.tstv.infofrom.ui.base;

import com.arellomobile.mvp.MvpView;

/**
 * Created by tstv on 15.09.2017.
 */

public interface BaseView extends MvpView {

    void showRefreshing();

    void hideRefreshing();

    void showDataProgress();

    void hideDataProgress();

    void showError(String message);
}
