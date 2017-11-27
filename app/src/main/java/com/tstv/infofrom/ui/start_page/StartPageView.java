package com.tstv.infofrom.ui.start_page;

import com.tstv.infofrom.ui.base.BaseView;

/**
 * Created by tstv on 27.11.2017.
 */

public interface StartPageView extends BaseView {

    enum ProblemType {
        Location, NetworkDisabled
    }

    void showSnackBarProblem(ProblemType problemType);

}
