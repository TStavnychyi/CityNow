package com.tstv.infofrom.ui.places.search_places;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;
import com.tstv.infofrom.ui.places.detail_places.PlacesDetailActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tstv on 24.10.2017.
 */

public class SearchPlacesFragment extends BaseFragment implements SearchPlacesView, GooglePlacesServicesHelper.GoogleServicesListener {

    private static final String TAG = SearchPlacesFragment.class.getSimpleName();

    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

    private ProgressBar mProgressBar;

    @BindView(R.id.search_places_rv)
    RecyclerView mSearchPlacesRecyclerView;

    @BindView(R.id.toolbar_search_places)
    Toolbar mToolbar;

    @InjectPresenter
    SearchPlacesPresenter mPresenter;

    @Inject
    SearchPlacesAdapter mSearchPlacesAdapter;

    @Inject
    NearbyPlacesApi mNearbyPlacesApi;

    @Inject
    LinearLayoutManager mLinearLayoutManager;

    // @Inject
    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    boolean mIsGooglePlayServicesConnected = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.get().getActivityComponent().inject(this);
        mGooglePlacesServicesHelper = new GooglePlacesServicesHelper(getActivity(), this);
        mGooglePlacesServicesHelper.connect();

      /*  try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_places, container, false);

        ButterKnife.bind(this, view);

        mPresenter.loadVariables(mSearchPlacesAdapter, mGooglePlacesServicesHelper);

        setHasOptionsMenu(true);
        initToolbar();

        mProgressBar = getSearchPlacesActivity().getProgressBar();

        setRecyclerViews();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGooglePlacesServicesHelper.handleActivityResult(requestCode, resultCode, data, getBaseActivity());

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String placeId = place.getId();
                Intent intent = PlacesDetailActivity.newIntent(getActivity(), placeId);
                getActivity().startActivity(intent);

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_search_toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_toolbar_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;


        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setIconified(false);
            searchView.setQueryHint("Search");
            View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            v.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mPresenter.getResultFromInput(query);
                    closeKeyboard();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            searchView.setOnCloseListener(() -> {
                closeKeyboard();
                return true;
            });

        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getMainContentLayout() {
        return R.layout.fragment_search_places;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPresenter;
    }

    @Override
    public int onCreateToolbarTitle() {
        return 0;
    }

    @Override
    public String TAG() {
        return null;
    }

    @Override
    public Fragment getFragmentInstance() {
        return null;
    }

    @Override
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {

    }

    @Override
    public void showDataProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        hideSearchPlacesRecyclerView();
    }

    @Override
    public void hideDataProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void initToolbar() {
        ((MvpAppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        getActivity().setTitle(null);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void showSearchPlacesRecyclerView() {
        mSearchPlacesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchPlacesRecyclerView() {
        mSearchPlacesRecyclerView.setVisibility(View.GONE);
    }

    private void setRecyclerViews() {
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchPlacesRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSearchPlacesRecyclerView.setAdapter(mSearchPlacesAdapter);
    }

    @Override
    public void onConnected() {
        mIsGooglePlayServicesConnected = true;
    }

    @Override
    public void onDisconnected() {
        mIsGooglePlayServicesConnected = false;
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
