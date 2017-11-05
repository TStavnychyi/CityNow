package com.tstv.infofrom.ui.places.search_places;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tstv on 25.10.2017.
 */

public class SearchPlacesAdapter extends RecyclerView.Adapter<SearchPlacesAdapter.SearchPlacesViewHolder> {

    @Override
    public SearchPlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(SearchPlacesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SearchPlacesViewHolder extends RecyclerView.ViewHolder {

        public SearchPlacesViewHolder(View itemView) {
            super(itemView);
        }
    }
}
