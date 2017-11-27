package com.tstv.infofrom.ui.places;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.model.places.PlacePrediction;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;
import com.tstv.infofrom.ui.places.categories.CategoriesActivity;
import com.tstv.infofrom.ui.places.search_places.SearchPlacesActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tstv on 22.09.2017.
 */

public class PlacesFragment extends BaseFragment implements PlacesView, AppBarLayout.OnOffsetChangedListener {

    private final static String TAG = PlacesFragment.class.getSimpleName();

    private static final int REQUEST_CODE_CATEGORIES = 1001;

    @BindView(R.id.iv_places_image_title)
    ImageView iv_places_image_title;

    @BindView(R.id.tv_places_title)
    TextView tv_places_title;

    @BindView(R.id.appbar_places)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.rv_places)
    RecyclerView mRecyclerView;

    @BindView(R.id.progress_bar_recy_view)
    ProgressBar pb_recycler_view;

    @BindView(R.id.toolbar_places)
    Toolbar mToolbar;

    @BindView(R.id.places_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @InjectPresenter
    PlacesPresenter mPlacesPresenter;

    @Inject
    PlacesAdapter mPlacesAdapter;

    @Inject
    NearbyPlacesApi mNearbyPlacesApi;

    @Inject
    LinearLayoutManager mLayoutManager;

    @Inject
    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    private boolean isInternetIsAvailable;

    boolean mIsGooglePlayServicesConnected;

    private boolean isLocationDataAlreadyUploaded;

    private String currentCity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getBaseActivity().showDataProgress();

        MyApplication.get().getActivityComponent().inject(this);

        mGooglePlacesServicesHelper.connect();
        mIsGooglePlayServicesConnected = MyApplication.isGooglePlacesServicesConnected();
        isInternetIsAvailable = getBaseActivity().isNetworkConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        initToolbar();

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mPlacesAdapter);
        tv_places_title.setTypeface(getBoldItalicFont());

        if (isInternetIsAvailable) {
            mPlacesPresenter.loadVariables(mPlacesAdapter, mGooglePlacesServicesHelper, mNearbyPlacesApi);
            mPlacesPresenter.loadData(isLocationDataAlreadyUploaded, "establishment");
        } else {
            showMessage(getString(R.string.internet_turned_off_error));
        }

        mPlacesPresenter.loadStart();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_places, menu);
        // super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_places_categories:
                Intent intentCategories = new Intent(getContext(), CategoriesActivity.class);
                startActivityForResult(intentCategories, REQUEST_CODE_CATEGORIES);
                return true;
            case R.id.item_places_search:
                Intent intentSearch = new Intent(getContext(), SearchPlacesActivity.class);
                startActivity(intentSearch);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CATEGORIES:
                    String placesType = data.getStringExtra(CategoriesActivity.SEARCH_TYPE_EXTRA);
                    clearRecyclerViewData();
                    mPlacesPresenter.loadData(true,
                            placesType);
                    break;
            }
        }
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPlacesPresenter;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public Fragment getFragmentInstance() {
        Bundle args = new Bundle();
        PlacesFragment fragment = new PlacesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void showDataProgress() {
        getBaseActivity().showDataProgress();
        iv_places_image_title.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        mCollapsingToolbarLayout.setVisibility(View.GONE);
        tv_places_title.setVisibility(View.GONE);
    }

    @Override
    public void hideDataProgress() {
        getBaseActivity().hideDataProgress();
        iv_places_image_title.setVisibility(View.VISIBLE);
        mCollapsingToolbarLayout.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);
        tv_places_title.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRecyclerViewProgress() {
        pb_recycler_view.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hiderRecyclerViewProgress() {
        pb_recycler_view.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackBar(SnackBarType snackBarType) {

    }

    @Override
    public void setLocationData(PlacePrediction data) {
        currentCity = data.getPlaceName();
        tv_places_title.setText(data.getPlaceName());

        Glide.with(getActivity())
                .load(data.getImageUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("TAG", "onLoadFailed " + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.e("TAG", "onResourceReady");
                        return false;
                    }
                })
                .into(iv_places_image_title);
        isLocationDataAlreadyUploaded = true;
    }

    private void initToolbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
        ((MvpAppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        getActivity().setTitle(null);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    private Typeface getBoldItalicFont() {
        return Typeface.createFromAsset(getActivity().getAssets(), "Roboto_BoldItalic.ttf");
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            // Collapsed
            mToolbar.setTitle(currentCity);
        } else if (verticalOffset == 0) {
            // Expanded
            mToolbar.setTitle("");
        } else {
            mToolbar.setTitle("");
            // Somewhere in between
        }
    }

    public static PlacesFragment newInstance() {
        Bundle args = new Bundle();
        PlacesFragment fragment = new PlacesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void clearRecyclerViewData() {
        mPlacesPresenter.clearPlacesAdapterData();
    }

}