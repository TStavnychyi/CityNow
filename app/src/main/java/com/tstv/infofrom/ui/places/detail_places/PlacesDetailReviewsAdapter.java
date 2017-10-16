package com.tstv.infofrom.ui.places.detail_places;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.detail_places.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by tstv on 16.10.2017.
 */

public class PlacesDetailReviewsAdapter extends RecyclerView.Adapter<PlacesDetailReviewsAdapter.ReviewsViewHolder> {

    private List<Review> mReviewList;

    private Context mContext;

    public PlacesDetailReviewsAdapter(Context context, List<Review> reviewList) {
        mContext = context;
        mReviewList = reviewList;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_places_details_review, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        Review reviewObj = mReviewList.get(position);

        String authorName = reviewObj.getAuthorName();
        String authorIconUrl = reviewObj.getProfilePhotoUrl();

        if (authorIconUrl != null) {
            Glide.with(mContext).load(authorIconUrl).apply(bitmapTransform(new CircleCrop())).into(holder.iv_profile_icon);
        } else {
            ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
            Drawable roundAuthorIcon = TextDrawable.builder().buildRound(String.valueOf(authorName.charAt(0)), colorGenerator.getRandomColor());
            holder.iv_profile_icon.setImageDrawable(roundAuthorIcon);
        }

        holder.tv_profile_name.setText(authorName);
        holder.tv_profile_review.setText(reviewObj.getText());
        holder.rb_profile_rating.setRating(reviewObj.getRating());
        holder.tv_time_description.setText(Utils.formatUnixTime(String.valueOf(reviewObj.getTime())));


    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    class ReviewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_profile_icon)
        ImageView iv_profile_icon;

        @BindView(R.id.rb_profile_rating)
        RatingBar rb_profile_rating;

        @BindView(R.id.tv_profile_name)
        TextView tv_profile_name;

        @BindView(R.id.tv_profile_review)
        TextView tv_profile_review;

        @BindView(R.id.tv_profile_time_description)
        TextView tv_time_description;

        ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
