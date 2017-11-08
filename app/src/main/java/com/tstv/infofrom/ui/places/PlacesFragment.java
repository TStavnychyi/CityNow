package com.tstv.infofrom.ui.places;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
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
import com.tstv.infofrom.common.utils.Utils;
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

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;

    private static final int REQUEST_CODE_CATEGORIES = 1001;

    private final String[] locationPermission = {
            Manifest.permission.ACCESS_FINE_LOCATION};

    @BindView(R.id.iv_places_image_title)
    ImageView iv_places_image_title;

    @BindView(R.id.tv_places_title)
    TextView tv_places_title;

    /*@BindView(R.id.search_view_places)
    SearchView mSearchView;*/

    @BindView(R.id.appbar_places)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.rv_places)
    RecyclerView mRecyclerView;

  /*  @BindView(R.id.tv_places_recommendations)
    TextView tv_text_above_rv;*/

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

        mGooglePlacesServicesHelper = getGooglePlacesServicesHelper();
        mIsGooglePlayServicesConnected = isGooglePlacesServicesConnected();


        isInternetIsAvailable = getBaseActivity().isNetworkConnected();

        if (isInternetIsAvailable) {
            if (mIsGooglePlayServicesConnected) {
                mPlacesPresenter.loadVariables(mPlacesAdapter, mGooglePlacesServicesHelper, mNearbyPlacesApi);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(locationPermission, REQUEST_LOCATION_PERMISSIONS);
                } else {
                    requestSingleUpdate();
                }
            }
        } else {
            showMessage(getString(R.string.no_internet_connection_message));
        }
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

        mPlacesPresenter.loadStart();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "Destroy" + PlacesFragment.this);
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
                    mPlacesPresenter.loadData(BasePresenter.ProgressType.DataProgress, true,
                            placesType);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        requestSingleUpdate();
                    }
                } else {

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected int getMainContentLayout() {
        return R.layout.fragment_places;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPlacesPresenter;
    }

    @Override
    public int onCreateToolbarTitle() {
        return 0;
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
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {

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

    private void getLocationData(Location location) {
        Double[] coordinates = {location.getLatitude(), location.getLongitude()};
        MyApplication.setCurrentLtdLng(coordinates);
        String city = Utils.getCityFromLatLng(coordinates, getContext());
        String country = Utils.getCountryCodeFromLatLng(coordinates, getContext());
        MyApplication.setCurrentCountry(country);
        MyApplication.setCurrentCity(city);
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
        //    toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        getActivity().setTitle(null);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    private void requestSingleUpdate() throws SecurityException {
        Log.e("TAG", "requestSingleUpdate");
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    getLocationData(location);
                    mPlacesPresenter.loadData(BasePresenter.ProgressType.DataProgress, isLocationDataAlreadyUploaded, "establishment");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(getBaseActivity(), "Make sure to enable Internet on your phone", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }
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