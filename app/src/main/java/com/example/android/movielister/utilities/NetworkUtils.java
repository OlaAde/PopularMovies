package com.example.android.movielister.utilities;

import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.example.android.movielister.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Adeogo on 4/5/2017.
 */

public final class NetworkUtils {
    final static String KEY_PARAM = "api_key";
    final static String PAGE_PARAM = "page";
    final static String SCHEME_URI = "https";
    final static String AUTHORITY_URI= "api.themoviedb.org";
    final static String PATH_1 = "3";
    final static String PATH_2 = "movie";

    public static URL buildUrl(String sortPref, String pagePref, String keyPref){
        Uri.Builder builder = new Uri .Builder();
                builder.scheme(SCHEME_URI)
                .authority(AUTHORITY_URI)
                .appendPath(PATH_1)
                .appendPath(PATH_2)
                .appendPath(sortPref)
                .appendQueryParameter(KEY_PARAM, keyPref)
                .appendQueryParameter(PAGE_PARAM,pagePref);
        Uri builtUri = builder.build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static JSONArray resultArray(String movieJsonStr) throws JSONException{
        JSONObject jsonObject = new JSONObject(movieJsonStr);
        JSONArray movieArray = jsonObject.getJSONArray("results");
        return movieArray;
    }

    public static int getTotalNumberOfPages(String movieJsonStr){
        JSONObject jsonObject = null;
        int totalNumPages = 0;
        try {
            jsonObject = new JSONObject(movieJsonStr);
            totalNumPages = (int)jsonObject.getDouble("total_pages");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return totalNumPages;
    }


}
