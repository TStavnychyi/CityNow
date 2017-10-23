package com.tstv.infofrom.ui.places.detail_places;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstv.infofrom.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 18.10.2017.
 */

public class PlacesOpeningHoursAdapter extends RecyclerView.Adapter<PlacesOpeningHoursAdapter.OpeningHoursViewHolder> {

    private List<String> mHoursList = new ArrayList<>();

    @Override
    public OpeningHoursViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_opening_hours, parent, false);
        return new OpeningHoursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OpeningHoursViewHolder holder, int position) {
        holder.tv_item_opening_hours.setText(mHoursList.get(position));
    }

    @Override
    public int getItemCount() {
        return mHoursList.size();
    }

    void setItems(List<String> hoursList) {
        clearList();
        mHoursList.addAll(hoursList);
        notifyDataSetChanged();
    }

    private void clearList() {
        mHoursList.clear();
    }

    class OpeningHoursViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_places_opening_hours)
        TextView tv_item_opening_hours;

        public OpeningHoursViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
