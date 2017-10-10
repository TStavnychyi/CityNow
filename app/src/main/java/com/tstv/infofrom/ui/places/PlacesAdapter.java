package com.tstv.infofrom.ui.places;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstv.infofrom.R;
import com.tstv.infofrom.model.places.PlacePrediction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tstv on 20.09.2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesItemViewHolder> {

    private List<PlacePrediction> mPlacePredictions = new ArrayList<>();

    private boolean isListLoadedEnough;

    public PlacesAdapter() {

    }

    @Override
    public PlacesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendations_places, parent, false);
        return new PlacesItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesItemViewHolder holder, int position) {
        PlacePrediction placePrediction = mPlacePredictions.get(position);
        holder.tv_place_name.setText(placePrediction.getId());
        holder.tv_place_name.setText(placePrediction.getPlaceName());
        holder.tv_place_address.setText(placePrediction.getPlaceDescription());
        Bitmap placePhoto = placePrediction.getBitmap();
        if (placePhoto != null) {
            holder.civ_place_image.setImageBitmap(placePrediction.getBitmap());
        } else {
            holder.civ_place_image.setImageResource(R.drawable.no_image_available);
        }
    }

    @Override
    public int getItemCount() {
        return mPlacePredictions.size();
    }

    void setItems(List<PlacePrediction> items) {
        clearList();
        mPlacePredictions.addAll(items);
        if (mPlacePredictions.size() >= 3) {
            setListLoadedEnough(true);
        }
        notifyDataSetChanged();

    }

    public boolean isListLoadedEnough() {
        return isListLoadedEnough;
    }

    public void setListLoadedEnough(boolean listLoadedEnough) {
        isListLoadedEnough = listLoadedEnough;
    }

    private void clearList() {
        mPlacePredictions.clear();
    }

    class PlacesItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.civ_place_image)
        CircleImageView civ_place_image;

        @BindView(R.id.tv_place_name)
        TextView tv_place_name;

        @BindView(R.id.tv_place_address)
        TextView tv_place_address;

        PlacesItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}