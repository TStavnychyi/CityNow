package com.tstv.infofrom.ui.places.detail_places;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.model.places.detail_places.Result;
import com.tstv.infofrom.rest.api.DetailPlacesApi;
import com.tstv.infofrom.rest.api.PlacesPhotoFromReferenceApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tstv on 13.10.2017.
 */

public class PlacesDetailFragment extends BaseFragment implements PlacesDetailView {

    private static final String ARG_PLACE_ID = "place_id";
    private static final String ARG_PLACE_PHOTO = "photo_url";

    @BindView(R.id.iv_places_detail_image)
    ImageView iv_places_detail_blur_image;

    @BindView(R.id.civ_places_detail)
    CircleImageView civ_places_detail;

    @BindView(R.id.rating_bar_detail_places)
    RatingBar ratingBar;

    @BindView(R.id.detail_places_toolbar)
    Toolbar toolbar;

    @BindView(R.id.places_detail_block_web_site)
    LinearLayout ll_block_website;

    @BindView(R.id.places_detail_block_call)
    LinearLayout ll_block_call;

    @BindView(R.id.tv_places_detail_address)
    TextView tv_places_detail_address;

    @BindView(R.id.iv_places_detail_location_map_icon)
    ImageView iv_places_detail_location_icon;

    @BindView(R.id.ll_places_detail_call)
    LinearLayout ll_places_detail_call;

    @BindView(R.id.tv_places_detail_phone_number)
    TextView tv_places_detail_phone_number;

    @BindView(R.id.ll_time_opening)
    LinearLayout ll_time_opening;

    @BindView(R.id.tv_places_detail_hour_opening)
    TextView tv_places_detail_hour_opening;

    @BindView(R.id.places_detail_photos_rv)
    RecyclerView rv_photos;

    @BindView(R.id.places_detail_reviews_rv)
    RecyclerView rv_reviews;

    @BindView(R.id.parent_layout_detail_places)
    CoordinatorLayout mParentView;

    @BindView(R.id.tv_places_detail_name)
    TextView tv_places_detail_name;

    @Inject
    DetailPlacesApi mDetailPlacesApi;

    @Inject
    PlacesPhotoFromReferenceApi mPhotoApi;

    @InjectPresenter
    PlacesDetailPresenter mPresenter;

    private ProgressBar mProgressBar;

    private String placeId;

    private String photoBackgroundUrl;

    public static PlacesDetailFragment newInstance(String id, String photoUrl) {
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_ID, id);
        args.putString(ARG_PLACE_PHOTO, photoUrl);

        PlacesDetailFragment fragment = new PlacesDetailFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeId = getArguments().getString(ARG_PLACE_ID);
        photoBackgroundUrl = getArguments().getString(ARG_PLACE_PHOTO);

        MyApplication.get().getPlaceComponent().inject(this);
        if (placeId != null && !placeId.isEmpty()) {
            mPresenter.setDetailPlacesApi(mDetailPlacesApi, mPhotoApi, placeId);
        } else {
            Toast.makeText(getContext(), "Place Id is null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mProgressBar = getBaseActivity().getProgressBar();

        mPresenter.loadStart();
    }

    @Override
    protected int getMainContentLayout() {
        return R.layout.fragment_places_detail;
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
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {

    }

    @Override
    public void showDataProgress() {
        mParentView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDataProgress() {
        mParentView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(Result result) {
        if (result.getRating() != null) {
            ratingBar.setRating(result.getRating());
        }
        if (result.getVicinity() != null) {
            tv_places_detail_address.setText(result.getVicinity());
        }
        if (result.getName() != null) {
            tv_places_detail_name.setText(result.getName());
        }
        if (result.getInternationalPhoneNumber() != null) {
            tv_places_detail_phone_number.setText(result.getInternationalPhoneNumber());
        }
        Glide.with(getActivity()).load(photoBackgroundUrl).into(iv_places_detail_blur_image);

    }

    @Override
    public void setPhotoUrl(String url) {

    }
}