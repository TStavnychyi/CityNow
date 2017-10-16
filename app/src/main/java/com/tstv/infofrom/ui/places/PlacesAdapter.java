package com.tstv.infofrom.ui.places;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstv.infofrom.R;
import com.tstv.infofrom.model.places.PlacePrediction;
import com.tstv.infofrom.ui.base.BaseActivity;
import com.tstv.infofrom.ui.places.detail_places.PlacesDetailActivity;

import java.io.ByteArrayOutputStream;
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

    BaseActivity mBaseActivity;

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
        holder.bind(placePrediction);
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

    boolean isListLoadedEnough() {
        return isListLoadedEnough;
    }

    private void setListLoadedEnough(boolean listLoadedEnough) {
        isListLoadedEnough = listLoadedEnough;
    }

    void setBaseActivity(BaseActivity activity) {
        mBaseActivity = activity;
    }

    private void clearList() {
        mPlacePredictions.clear();
    }

    class PlacesItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.civ_place_image)
        CircleImageView civ_place_image;

        @BindView(R.id.tv_place_name)
        TextView tv_place_name;

        @BindView(R.id.tv_place_address)
        TextView tv_place_address;

        PlacePrediction mPlacePrediction;

        PlacesItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(PlacePrediction placePrediction) {
            mPlacePrediction = placePrediction;
        }

        @Override
        public void onClick(View v) {
            byte[] byteArray = null;
            if (mPlacePrediction.getBitmap() != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mPlacePrediction.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            Intent intent = PlacesDetailActivity.newIntent(v.getContext(), mPlacePrediction.getId(), byteArray);
            v.getContext().startActivity(intent);
        }
    }
}