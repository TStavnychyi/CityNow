package com.tstv.infofrom.ui.places.detail_places;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tstv.infofrom.R;

/**
 * Created by tstv on 23.10.2017.
 */

public class PlacesPhotoDialog extends DialogFragment {

    private static final String ARG_PHOTO_URL = "arg_photo_url";
    private static final String DIALOG_PLACE_PHOTO = "dialog_place_photo";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_places_photo, null);

        ImageView iv_dialog_places_photo = (ImageView) v.findViewById(R.id.iv_dialog_places_photo);

        Bundle args = getArguments();
        String url = args.getString(ARG_PHOTO_URL);

        Picasso.with(getContext()).load(url).placeholder(R.drawable.image_placeholder).into(iv_dialog_places_photo);

        return new AlertDialog.Builder(getActivity())
                .setView(iv_dialog_places_photo)
                .create();

    }

    public static void showPhotoDialog(Context context, String photoUrl) {
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
        PlacesPhotoDialog dialog = new PlacesPhotoDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_URL, photoUrl);
        dialog.setArguments(args);
        dialog.show(manager, DIALOG_PLACE_PHOTO);
    }
}
