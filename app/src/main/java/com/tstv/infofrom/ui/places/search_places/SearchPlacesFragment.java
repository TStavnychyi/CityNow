package com.tstv.infofrom.ui.places.search_places;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.google.GooglePlacesServicesHelper;
import com.tstv.infofrom.rest.api.NearbyPlacesApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 24.10.2017.
 */

public class SearchPlacesFragment extends BaseFragment implements SearchPlacesView, GooglePlacesServicesHelper.GoogleServicesListener {

    private ProgressBar mProgressBar;

    @BindView(R.id.search_places_categories_rv)
    RecyclerView mCategoriesRecyclerView;

    @BindView(R.id.search_places_rv)
    RecyclerView mSearchPlacesRecyclerView;

    @BindView(R.id.toolbar_search_places)
    Toolbar mToolbar;

    @InjectPresenter
    SearchPlacesPresenter mPresenter;

    // @Inject
    SearchPlacesCategoriesAdapter mAdapter;

    //  @Inject
    GooglePlacesServicesHelper mGooglePlacesServicesHelper;

    //  @Inject
    NearbyPlacesApi mNearbyPlacesApi;

    //  @Inject
    GridLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   MyApplication.get().plusFragmentComponent(SearchPlacesFragment.this,getSearchPlacesActivity()).inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        initToolbar();

        //    mNearbyPlacesApi = getNearbyPlacesApi();

        mGooglePlacesServicesHelper.connect();

        mProgressBar = getSearchPlacesActivity().getProgressBar();

        mLinearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mCategoriesRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCategoriesRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mGooglePlacesServicesHelper.disconnect();
        //    mGooglePlacesServicesHelper = null;
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

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
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
        mCategoriesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideDataProgress() {
        mProgressBar.setVisibility(View.GONE);
        mCategoriesRecyclerView.setVisibility(View.VISIBLE);

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
    public void showCategoriesRecyclerView() {
        mCategoriesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCategoriesRecyclerView() {
        mCategoriesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showSearchPlacesRecyclerView() {
        mSearchPlacesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchPlacesRecyclerView() {
        mSearchPlacesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onConnected() {
        Toast.makeText(getContext(), "Google Services is Available now", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDisconnected() {
        Toast.makeText(getContext(), "Google Services is NOT Available now", Toast.LENGTH_SHORT).show();

    }
}
