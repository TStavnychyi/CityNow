package com.tstv.infofrom.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.manager.MyFragmentManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 15.09.2017.
 */

public abstract class BaseActivity extends MvpAppCompatActivity {

    public static final int PLACE_PICKER_REQUEST = 1;
  //  private static final int REQUEST_LOCATION_PERMISSIONS = 2;

    @BindView(R.id.progress_bar)
    protected ProgressBar mProgressBar;

    @Inject
    MyFragmentManager mMyFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Log.e("TAG", "BaseActivity");

        ButterKnife.bind(this);

        MyApplication.getApplicationComponent().inject(this);

        FrameLayout parent = (FrameLayout) findViewById(R.id.main_wrapper);
        getLayoutInflater().inflate(getMainContentLayout(), parent);

    }
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @LayoutRes
    protected abstract int getMainContentLayout();

    public void fragmentOnScreen(BaseFragment baseFragment) {
        setToolbarTitle(baseFragment.createToolbarTitle(this));
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setContent(BaseFragment fragment) {
        mMyFragmentManager.setFragment(this, fragment, R.id.main_wrapper);
    }

    public void addContent(BaseFragment fragment) {
        mMyFragmentManager.addFragment(this, fragment, R.id.main_wrapper);
    }

    public boolean removeCurrentFragment() {
        return mMyFragmentManager.removeCurrentFragment(this);
    }

    public boolean removeFragment(BaseFragment fragment) {
        return mMyFragmentManager.removeFragment(this, fragment);
    }

    @Override
    public void onBackPressed() {
        removeCurrentFragment();
    }

    public Activity getBaseActivity() {
        return this;
    }
}
