package com.tstv.infofrom.common.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.tstv.infofrom.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by tstv on 15.09.2017.
 */

public class Utils {

    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public static String formatPlaceOpenCloseTime(String timeToFormat) {
        String res = timeToFormat.substring(0, 2) + ":" + timeToFormat.substring(2, timeToFormat.length());
        Log.e("TAG", "Formated Time : " + res);
        return res;
    }


    public static String formatPlacesDetailResponse(String response) {
        return response.substring(46, response.length() - 1);
    }

    public static String convertDateToTime(String dateToConvert) {
        StringBuilder stringBuilder = new StringBuilder();
        String time = dateToConvert.substring(10);
        String result = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date dateStr = format.parse(time);

            int hour = dateStr.getHours();
            int minutes = dateStr.getMinutes();
            stringBuilder.append(hour + ":");
            stringBuilder.append(minutes);
            result = stringBuilder.toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void backgroundImage(int isDay, ImageView imageView) {
        switch (isDay) {
            case 0:
                imageView.setImageResource(R.drawable.night_il);
                break;
            case 1:
                imageView.setImageResource(R.drawable.day_il);
                break;
        }
    }

    public static LatLngBounds getLatLngBoundsFromDouble(Double[] arg) {
        LatLng latLng = new LatLng(arg[0], arg[1]);
        return new LatLngBounds.Builder()
                .include(latLng)
                .build();
    }

    public static String getStringLatLngFromDouble(Double[] arg) {
        return arg[0] + "," + arg[1];
    }

    public static String getCityFromLatLng(Double[] arg, Context context) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        String city = null;
        try {
            List<Address> addresses = gcd.getFromLocation(arg[0], arg[1], 1);
            city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }

    public static String getPhotoFromBingAPI(String city) {
        String url = "https://api.cognitive.microsoft.com/bing/v7.0/images/search?q=" + city +
                "&count=1"
                + "&mkt=en-us"
                + "&size=Large"
                + "&imageType=Photo"
                + "&safeSearch=Off";

        String result = performGetCallBingImageSearch(url);
        return parseJsonBing(result);
    }

    private static String parseJsonBing(String json) {
        String photoUrl = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray images = jsonObject.getJSONArray("value");
            for (int i = 0; i < images.length(); i++) {
                JSONObject imageResult = images.getJSONObject(i);

                photoUrl = imageResult.getString("contentUrl");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return photoUrl;
    }

    private static String performGetCallBingImageSearch(String url) {
        //IF GETTING ERROR,FIRST OF ALL CHECK IS API KEY EXPIRED!!!!
        String aPI_KEY = "c61745c5264e49618c39afce7b75c093";
        StringBuffer response = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setConnectTimeout(25000);
            // optional default is GET
            con.setRequestMethod("GET");
            con.setRequestProperty("Ocp-Apim-Subscription-Key", aPI_KEY);


            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        //print result

        return response.toString(); //this is your response
    }
}
