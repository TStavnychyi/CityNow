package com.tstv.infofrom.ui.places.detail_places;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.common.utils.NetworkUtils;
import com.tstv.infofrom.common.utils.Utils;
import com.tstv.infofrom.model.places.detail_places.Result;
import com.tstv.infofrom.rest.api.DetailPlacesApi;
import com.tstv.infofrom.rest.api.PlacesPhotoFromReferenceApi;
import com.tstv.infofrom.ui.base.BaseFragment;
import com.tstv.infofrom.ui.base.BasePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by tstv on 13.10.2017.
 */

public class PlacesDetailFragment extends BaseFragment implements PlacesDetailView, AppBarLayout.OnOffsetChangedListener {

    private static final String ARG_PLACE_ID = "place_id";
    private static final String ARG_PLACE_PHOTO = "photo_url";

    @BindView(R.id.iv_places_detail_image)
    ImageView iv_places_detail_blur_image;

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

    @BindView(R.id.rl_time_opening)
    RelativeLayout rl_time_opening;

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

    @BindView(R.id.civ_place_image)
    CircleImageView civ_place_image;

    @BindView(R.id.iv_places_detail_phone_call)
    ImageView iv_phone_call;

    @BindView(R.id.places_detail_collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsing_toolbar_layout;

    @BindView(R.id.ll_places_reviews)
    LinearLayout ll_places_reviews;

    @BindView(R.id.places_detail_appbar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.ll_places_photos)
    LinearLayout ll_places_photos;

    @BindView(R.id.rv_expandable_time_opening)
    RecyclerView rv_expandable_time_opening;

    @BindView(R.id.ll_places_detail_title_container)
    LinearLayout ll_places_title_container;

    @OnClick(R.id.iv_places_detail_phone_call)
    public void onClickCall() {
        call();
    }

    @OnClick(R.id.places_detail_block_call)
    public void onClickBlockCall() {
        call();
    }

    @OnClick(R.id.places_detail_block_web_site)
    public void onClickWebsite() {
        openWebsite();
    }

    @OnClick(R.id.iv_places_detail_location_map_icon)
    public void onClickMap() {
        showPlaceLocationOnTheMap();
    }

    @OnClick(R.id.rl_time_opening)
    public void onClickExpandWorkTime() {
        expandTimeOpeningView();
    }

    @Inject
    DetailPlacesApi mDetailPlacesApi;

    @Inject
    PlacesPhotoFromReferenceApi mPhotoApi;

    @Inject
    LinearLayoutManager mPhotosLayoutManager;

    @Inject
    LinearLayoutManager mReviewsLayoutManager;

    @Inject
    LinearLayoutManager mOpeningHoursLayoutManager;

    @InjectPresenter
    PlacesDetailPresenter mPresenter;

    private PlacesDetailPhotosAdapter mPhotosAdapter;

    private PlacesDetailReviewsAdapter mReviewsAdapter;

    private Result mResult;

    private ProgressBar mProgressBar;

    private String placeId;

    private Bitmap photoBackgroundUrl;

    private byte[] byteArray;

    private PlacesOpeningHoursAdapter mHoursAdapter;

    private boolean isExpanded = false;

    private final int startTimeOpeningViewHeight = 150;

    private final int timeOpeningExpandHeight = 750;

    private boolean isCollapsingToolbarShow = false;

    private int scrollRange = -1;

    private boolean isNetworkAvailable;

    public static PlacesDetailFragment newInstance(String id, byte[] bytes) {
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_ID, id);
        args.putByteArray(ARG_PLACE_PHOTO, bytes);

        PlacesDetailFragment fragment = new PlacesDetailFragment();
        fragment.setArguments(args);
        return fragment;

    }

    public static PlacesDetailFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_ID, id);

        PlacesDetailFragment fragment = new PlacesDetailFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNetworkAvailable = NetworkUtils.isNetworkConnected(getContext());
        mProgressBar = getPlacesDetailActivity().getPlacesProgressBar();

        if (isNetworkAvailable) {
            placeId = getArguments().getString(ARG_PLACE_ID);
            byteArray = getArguments().getByteArray(ARG_PLACE_PHOTO);

            if (byteArray != null) {
                photoBackgroundUrl = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }

            MyApplication.get().getActivityComponent().inject(this);

            if (placeId != null && !placeId.isEmpty()) {
                mPresenter.setDetailPlacesApi(mDetailPlacesApi, mPhotoApi, placeId);

            } else {
                Toast.makeText(getContext(), "Place Id is null", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } else {
            getBaseActivity().showError(getString(R.string.internet_turned_off_error));
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_detail, container, false);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        initToolbar();

        initRecyclerViews();

        if (isNetworkAvailable) {
            mPresenter.loadStart();
        }
        return view;
    }

    @Override
    protected BasePresenter getBasePresenter() {
        return mPresenter;
    }

    @Override
    public String TAG() {
        return null;
    }

    @Override
    public Fragment getFragmentInstance() {
        return null;
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
    public void showSnackBar(SnackBarType snackBarType) {

    }

    @Override
    public void setData(Result result) {
        if (isResultObjectAvailable(result)) {
            mResult = result;

            if (result.getOpeningHours() != null) {


                if (result.getOpeningHours().getPeriods().get(0).getClose() != null &&
                        result.getOpeningHours().getPeriods().get(0).getOpen() != null) {
                    if (result.getOpeningHours().getOpenNow()) {
                        tv_places_detail_hour_opening.setText(getString(R.string.places_detail_opening_time_open)
                                + Utils.formatPlaceOpenCloseTime(result.getOpeningHours().getPeriods().get(0).getOpen().getTime())
                                + " - " + Utils.formatPlaceOpenCloseTime(result.getOpeningHours().getPeriods().get(0).getClose().getTime()));
                    } else {
                        tv_places_detail_hour_opening.setText(R.string.places_detail_opening_time_close);
                    }
                }

                if (result.getOpeningHours().getWeekdayText() != null) {
                    mHoursAdapter.setItems(mResult.getOpeningHours().getWeekdayText());
                }
            }

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
            if (photoBackgroundUrl != null) {
                civ_place_image.setImageBitmap(photoBackgroundUrl);
            } else {
                Glide.with(getActivity()).load(result.getIcon()).into(civ_place_image);
            }
            if (result.getPhotosUrls() != null) {
                Glide.with(getActivity()).load(result.getPhotosUrls().get(0)).apply(bitmapTransform(new BlurTransformation(5))).into(iv_places_detail_blur_image);

                mPhotosAdapter = new PlacesDetailPhotosAdapter(this.getContext(), result.getPhotosUrls());
                rv_photos.setAdapter(mPhotosAdapter);
            } else {
                ll_places_photos.setVisibility(View.GONE);
            }
            if (result.getReviews() != null) {
                mReviewsAdapter = new PlacesDetailReviewsAdapter(this.getContext(), result.getReviews());
                rv_reviews.setAdapter(mReviewsAdapter);
            } else {
                ll_places_reviews.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getActivity(), "Can't get any information about this place", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mResult.getFormattedPhoneNumber()));
        startActivity(intent);
    }

    private void openWebsite() {
        if (isResultObjectAvailable(mResult)) {
            String url = mResult.getWebsite();
            if (url != null) {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "This place has no website", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPlaceLocationOnTheMap() {
        if (isResultObjectAvailable(mResult)) {
            String url = mResult.getUrl();
            if (url != null) {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Can't show this place on the map", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean isResultObjectAvailable(Object result) {
        return result != null;
    }

    private void expandTimeOpeningView() {
        if (!isExpanded) {
            ValueAnimator slideAnimator = ValueAnimator
                    .ofInt(startTimeOpeningViewHeight, timeOpeningExpandHeight)
                    .setDuration(300);

            slideAnimator.addUpdateListener(animation -> {
                Integer value = (Integer) animation.getAnimatedValue();
                rl_time_opening.getLayoutParams().height = value.intValue();
                rl_time_opening.requestLayout();
                rv_expandable_time_opening.setVisibility(View.VISIBLE);
                tv_places_detail_hour_opening.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_black_24dp, 0);
            });

            AnimatorSet set = new AnimatorSet();
            set.play(slideAnimator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
            isExpanded = true;
        } else {
            ValueAnimator slideAnimator = ValueAnimator
                    .ofInt(timeOpeningExpandHeight, startTimeOpeningViewHeight)
                    .setDuration(300);

            slideAnimator.addUpdateListener(animation -> {
                Integer value = (Integer) animation.getAnimatedValue();
                rl_time_opening.getLayoutParams().height = value.intValue();
                rl_time_opening.requestLayout();
                rv_expandable_time_opening.setVisibility(View.GONE);
                tv_places_detail_hour_opening.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
            });

            AnimatorSet set = new AnimatorSet();
            set.play(slideAnimator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
            isExpanded = false;
        }
    }

    private void initRecyclerViews() {
        mPhotosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_photos.setLayoutManager(mPhotosLayoutManager);

        mReviewsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_reviews.setLayoutManager(mReviewsLayoutManager);

        mOpeningHoursLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_expandable_time_opening.setLayoutManager(mOpeningHoursLayoutManager);
        mHoursAdapter = new PlacesOpeningHoursAdapter();
        rv_expandable_time_opening.setAdapter(mHoursAdapter);
    }


    private void initToolbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
        ((MvpAppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(null);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
            if (mResult != null) {
                collapsing_toolbar_layout.setTitle(mResult.getName());
            }
            isCollapsingToolbarShow = true;
        } else if (isCollapsingToolbarShow) {
            collapsing_toolbar_layout.setTitle(" ");
            isCollapsingToolbarShow = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
