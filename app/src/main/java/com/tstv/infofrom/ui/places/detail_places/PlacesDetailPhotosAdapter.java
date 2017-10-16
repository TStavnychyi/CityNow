package com.tstv.infofrom.ui.places.detail_places;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tstv.infofrom.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 16.10.2017.
 */

public class PlacesDetailPhotosAdapter extends RecyclerView.Adapter<PlacesDetailPhotosAdapter.PhotosHolder> {

    private List<String> mListPhotos;

    private Context mContext;

    public PlacesDetailPhotosAdapter(Context context, List<String> listPhotos) {
        mContext = context;
        mListPhotos = listPhotos;
    }

    @Override
    public PhotosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_places_detail_photos, parent, false);
        return new PhotosHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotosHolder holder, int position) {
        Glide.with(mContext).load(mListPhotos.get(position)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mListPhotos.size();
    }

    class PhotosHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_item_photos)
        ImageView mImageView;

        PhotosHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
