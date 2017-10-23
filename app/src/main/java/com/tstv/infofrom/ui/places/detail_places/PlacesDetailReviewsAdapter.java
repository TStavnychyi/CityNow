package com.tstv.infofrom.ui.places.detail_places;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.tstv.infofrom.model.places.detail_places.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        if (reviewObj != null) {
            holder.bindViews(reviewObj);
        } else {
            Log.e("TAG", "Review Object is null");
        }

    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    class ReviewsViewHolder extends RecyclerView.ViewHolder {

        Review mReview;

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

        @OnClick(R.id.iv_profile_icon)
        public void onIconClick() {
            openProfile(mReview.getAuthorUrl());
        }

        ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        void bindViews(Review reviewObj) {
            mReview = reviewObj;
            String authorName = reviewObj.getAuthorName();
            String authorIconUrl = reviewObj.getProfilePhotoUrl();

            if (authorIconUrl != null) {
                Glide.with(mContext).load(authorIconUrl).apply(bitmapTransform(new CircleCrop())).into(iv_profile_icon);
            } else {
                ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
                Drawable roundAuthorIcon = TextDrawable.builder().buildRound(String.valueOf(authorName.charAt(0)), colorGenerator.getRandomColor());
                iv_profile_icon.setImageDrawable(roundAuthorIcon);
            }

            tv_profile_name.setText(authorName);
            tv_profile_review.setText(reviewObj.getText());
            rb_profile_rating.setRating(reviewObj.getRating());
            tv_time_description.setText(reviewObj.getRelativeTimeDescription());

        }

        void openProfile(String url) {
            if (url != null) {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                itemView.getContext().startActivity(intent);
            }
        }
    }
}
