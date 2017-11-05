package com.tstv.infofrom.ui.places.search_places;

import com.tstv.infofrom.ui.base.BaseView;

/**
 * Created by tstv on 24.10.2017.
 */

interface SearchPlacesView extends BaseView {

    void showCategoriesRecyclerView();

    void hideCategoriesRecyclerView();

    void showSearchPlacesRecyclerView();

    void hideSearchPlacesRecyclerView();
}
