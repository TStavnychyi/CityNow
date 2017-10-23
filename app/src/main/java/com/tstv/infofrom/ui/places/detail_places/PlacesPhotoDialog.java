package com.tstv.infofrom.ui.places.detail_places;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.bumptech.glide.Glide;
import com.tstv.infofrom.R;

/**
 * Created by tstv on 23.10.2017.
 */

public class PlacesPhotoDialog extends DialogFragment {

    private static final String ARG_PHOTO_URL = "arg_photo_url";
    private static final String DIALOG_PLACE_PHOTO = "dialog_place_photo";

    //@BindView(R.id.iv_dialog_places_photo)
    ImageView iv_dialog_places_photo;

    private String url;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.e("TAG", "onCreateDialog");

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_places_photo, null);

        // ButterKnife.bind(this, v);

        iv_dialog_places_photo = (ImageView) v.findViewById(R.id.iv_dialog_places_photo);


        Bundle args = getArguments();
        url = args.getString(ARG_PHOTO_URL);

        Glide.with(getActivity()).load(url).into(iv_dialog_places_photo);

        Log.e("TAG", "URL1 : " + url);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setCancelable(true)
                .create();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "onResume");
    }

    public static void showoPhotoDialog(Context context, String photoUrl) {
        FragmentManager manager = ((MvpAppCompatActivity) context).getSupportFragmentManager();
        PlacesPhotoDialog dialog = new PlacesPhotoDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_URL, photoUrl);
        dialog.setArguments(args);
        dialog.show(manager, DIALOG_PLACE_PHOTO);
    }
}
