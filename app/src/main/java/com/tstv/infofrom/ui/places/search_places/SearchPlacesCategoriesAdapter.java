package com.tstv.infofrom.ui.places.search_places;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.tstv.infofrom.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tstv on 24.10.2017.
 */

public class SearchPlacesCategoriesAdapter extends RecyclerView.Adapter<SearchPlacesCategoriesAdapter.CategoriesViewHolder> {

    private int[] imagesArray = {R.drawable.icon_restaurant, R.drawable.icon_bank, R.drawable.icon_credit_card, R.drawable.icon_disco_ball_filled, R.drawable.icon_gas_station,
            R.drawable.icon_hospital, R.drawable.icon_hotel_bed, R.drawable.icon_new_post, R.drawable.icon_parking, R.drawable.icon_pill,
            R.drawable.icon_police, R.drawable.icon_public_transport, R.drawable.icon_showplace, R.drawable.icon_store,
            R.drawable.icon_wc};

    private String[] categories = {"Food", "Bank", "ATM", "Entertainment", "Gas station", "Hospital", "Hotel", "Post office", "Parking", "Drugstore",
            "Police", "Public transport", "Showplace", "Store", "Toilet"};

    @Override
    public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_places_categories, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriesViewHolder holder, int position) {
        holder.setData(imagesArray[position], categories[position]);
    }

    @Override
    public int getItemCount() {
        return imagesArray.length;
    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_categories_image)
        ImageView iv_image;

        @BindView(R.id.tv_categories_name)
        TextView tvCategoriesName;

        CategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void setData(int image, String name) {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();
            iv_image.setBackgroundColor(randomColor);
            iv_image.setImageResource(image);
            tvCategoriesName.setText(name);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
