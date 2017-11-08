package com.tstv.infofrom.ui.places.categories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.tstv.infofrom.MyApplication;
import com.tstv.infofrom.R;
import com.tstv.infofrom.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesActivity extends BaseActivity implements CategoriesView {

    private static final String TAG = CategoriesActivity.class.getSimpleName();

    public static final String SEARCH_TYPE_EXTRA = "search_type";

    @BindView(R.id.search_places_categories_rv)
    RecyclerView mCategoriesRecyclerView;

    @BindView(R.id.toolbar_categories_places)
    Toolbar mToolbar;

    SearchPlacesCategoriesAdapter mCategoriesAdapter;

    @Inject
    GridLayoutManager mGridLayoutManager;

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
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mCategoriesRecyclerView.setLayoutManager(mGridLayoutManager);
        mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);
    }


    class SearchPlacesCategoriesAdapter extends RecyclerView.Adapter<SearchPlacesCategoriesAdapter.CategoriesViewHolder> {

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
                Intent intentResult = new Intent();
                intentResult.putExtra(SEARCH_TYPE_EXTRA,
                        convertPlaceTypeForApi(tvCategoriesName.getText().toString()));
                Log.e(TAG, "convertPlaceTypeForAPi : " + convertPlaceTypeForApi(tvCategoriesName.getText().toString()));
                setResult(RESULT_OK, intentResult);
                finish();
            }

            private String convertPlaceTypeForApi(String typeToConvert) {
                return typeToConvert.toLowerCase().replace(" ", "_");
            }

          /*  private String preparePlaceTypeForApi(String typeBefore){
                switch (typeBefore){
                    case "Food" :
                        return "bakery|bar|cafe|restaurant|food";
                    case "Bank" :
                        return "bank";
                    case "ATM" :
                        return "atm";
                    case "Entertainment" :
                        return "amusement_park|aquarium|"
                }
            }*/
        }
    }

}
