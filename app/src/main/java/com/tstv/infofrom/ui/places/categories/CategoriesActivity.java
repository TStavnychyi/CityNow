package com.tstv.infofrom.ui.places.categories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.ui.base.BaseActivity;
import com.tstv.infofrom.ui.base.BaseView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesActivity extends BaseActivity implements BaseView {

    private static final String TAG = CategoriesActivity.class.getSimpleName();

    public static final String SEARCH_TYPE_EXTRA = "search_type";

    @BindView(R.id.search_places_categories_rv)
    RecyclerView mCategoriesRecyclerView;

    @BindView(R.id.toolbar_categories_places)
    Toolbar mToolbar;

    SearchPlacesCategoriesAdapter mCategoriesAdapter;

    @Inject
    LinearLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ButterKnife.bind(this);

        MyApplication.get().getActivityComponent().inject(this);

        initToolbar();

        setRecyclerView();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_item_categories));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setRecyclerView() {
        mCategoriesAdapter = new SearchPlacesCategoriesAdapter();
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCategoriesRecyclerView.setLayoutManager(mGridLayoutManager);
        mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);
    }

    @Override
    public void showSnackBar(SnackBarType snackBarType) {

    }

    class SearchPlacesCategoriesAdapter extends RecyclerView.Adapter<SearchPlacesCategoriesAdapter.CategoriesViewHolder> {

        private int[] imagesArray = {R.drawable.icon_food, R.drawable.icon_bank, R.drawable.icon_atm, R.drawable.icon_entertainment, R.drawable.icon_night_club, R.drawable.icon_gas_station,
                R.drawable.icon_hospital, R.drawable.icon_hotel, R.drawable.icon_post_office, R.drawable.icon_parking, R.drawable.icon_pharmacy,
                R.drawable.icon_police, R.drawable.icon_public_transport, R.drawable.icon_showplace, R.drawable.icon_store};

        private String[] categories = {"Food", "Bank", "ATM", "Entertainment", "Night club", "Gas station", "Hospital", "Hotel", "Post office", "Parking", "Drugstore",
                "Police", "Public transport", "Showplace", "Store"};

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
                iv_image.setImageResource(image);
                tvCategoriesName.setText(name);
            }

            @Override
            public void onClick(View v) {
                Intent intentResult = new Intent();
                intentResult.putExtra(SEARCH_TYPE_EXTRA,
                        preparePlaceTypeForApi(tvCategoriesName.getText().toString()));
                Log.e(TAG, "convertPlaceTypeForAPi : " + preparePlaceTypeForApi(tvCategoriesName.getText().toString()));
                setResult(RESULT_OK, intentResult);
                finish();
            }

            private String preparePlaceTypeForApi(String typeBefore) {
                switch (typeBefore) {
                    case "Food":
                        return "bakery|bar|cafe|restaurant|food";
                    case "Bank":
                        return "bank";
                    case "ATM":
                        return "atm";
                    case "Entertainment":
                        return "amusement_park|aquarium|bowling_alley|movie_theater|casino|shopping_mall|zoo";
                    case "Night club":
                        return "night_club";
                    case "Gas station":
                        return "gas_station";
                    case "Hospital":
                        return "hospital";
                    case "Hotel":
                        return "hotel|lodging";
                    case "Post office":
                        return "post_office";
                    case "Parking":
                        return "parking";
                    case "Drugstore":
                        return "pharmacy";
                    case "Police":
                        return "police";
                    case "Public transport":
                        return "bus_station|subway_station|train_station|taxi_stand";
                    case "Showplace":
                        return "mosque|museum|park|church|stadium|synagogue|city_hall";
                    case "Store":
                        return "jewelry_store|liquor_store|bicycle_store|book_store|pet_store|clothing_store|" +
                                "convenience_store|electronics_store|florist|store|home_goods_store";
                }
                return "all";
            }
        }
    }


}
