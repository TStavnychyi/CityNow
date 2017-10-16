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
import java.util.concurrent.TimeUnit;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by tstv on 15.09.2017.
 */

public class Utils {

    public static boolean isNetworkAvailableAndConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public static String updateTime(Date updatedTime) {

        String result;
        int updated_hour = updatedTime.getHours();
        int updated_minutes = updatedTime.getMinutes();

        Date current_time = new Date();
        int current_hour = current_time.getHours();
        int current_minutes = current_time.getMinutes();

        Log.e("TAG", "updatedTime : " + updatedTime + " , current time" + current_time);

        long diff = current_time.getTime() - updatedTime.getTime();
        long minutes_diff = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
        if (minutes_diff < 60) {
            return result = minutes_diff + " min ago";
        } else {
            long hours_diff = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
            if (hours_diff > 24) {
                long days_diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if (days_diff == 1) {
                    return result = days_diff + " day ago";
                } else {
                    return result = days_diff + " days ago";
                }
            }
            if (hours_diff == 1) {
                return result = hours_diff + " hour ago";
            } else {
                return result = hours_diff + " hours ago";
            }
        }
    }

    public static String formatUnixTime(String unixTime) {
        long time = Long.parseLong(unixTime);
        Date date = new Date(time * 1000L);
        return updateTime(date);
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

    public static void backgroundImage(int isDay, ImageView imageView){
        switch (isDay){
            case 0:
                imageView.setImageResource(R.drawable.night_il);
                break;
            case 1:
                imageView.setImageResource(R.drawable.day_il);
                break;
        }
    }

    public static LatLngBounds getLatLngBoundsFromDouble(Double[] arg){
        LatLng latLng = new LatLng(arg[0], arg[1]);
        return new LatLngBounds.Builder()
                .include(latLng)
                .build();
    }

    public static String getStringLatLngFromDouble(Double[] arg){
        return arg[0] + "," + arg[1];
    }

    public static String getCityFromLatLng(Double[] arg, Context context){
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

    public static String getPhotoFromBingAPI(String city){
        String url = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q=" + city +
                "&count=1"
                +"&mkt=en-us"
                + "&size=Large"
                +"&imageType=Photo"
                + "&safeSearch=Off";

        String result = performGetCallBingImageSearch(url);
        return parseJsonBing(result);
    }

    private static String parseJsonBing(String json){
        String photoUrl = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray images = jsonObject.getJSONArray("value");
            for (int i = 0; i < images.length(); i++){
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
        String aPI_KEY = "2f0f58921f614cf4bf2aa2ccb47f6b89";
        StringBuffer response = null;
        try
        {
            URL               obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setConnectTimeout(25000);
            // optional default is GET
            con.setRequestMethod("GET");
            con.setRequestProperty("Ocp-Apim-Subscription-Key", aPI_KEY);


            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String         inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
        //print result

        return response.toString(); //this is your response
    }
}
