package com.example.android.movielister;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movielister.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnclickHandler {

    private RecyclerView mRecyclerView;
    private TextView mTextViewTitle;
    private ImageView mImageViewitem;
    private MovieAdapter mMovieAdapter;


    private String sort_by_popularity = "popularity.desc";
    private String sort_by_highest_rated = "vote_average.desc";
    private String mSortPref = null;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    private String keyPref = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortPref = sort_by_popularity;
       mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mTextViewTitle = (TextView) findViewById(R.id.tv_item);
        mImageViewitem = (ImageView) findViewById(R.id.iv_item);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
/* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 3 ,GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        keyPref = getString((R.string.tmdb_api_key));
        updateLayout();
    }

    public void updateLayout(){
        showWeatherDataView();
        mMovieAdapter.setMovieData(null);
        new LoadMoviesTask().execute(mSortPref);
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void voidMethod(JSONObject param) {
        Intent intent = new Intent(MainActivity.this, MovieDetail.class);
        String movieTitle = null;
        String description = null;
        String usersRating = null;
        String releaseDate = null;
        String thumbnail = null;
        try {
            movieTitle = param.getString("original_title");
            description = param.getString("overview");
            usersRating = param.getString("vote_average");
            releaseDate = param.getString("release_date");
            thumbnail = param.getString("backdrop_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra("movieTitle", movieTitle);
        intent.putExtra("description", description);
        intent.putExtra("usersRating", usersRating);
        intent.putExtra("releaseDate", releaseDate);
        intent.putExtra("thumbnail", thumbnail);
        startActivity(intent);
    }


    public class LoadMoviesTask extends AsyncTask<String,Void , JSONArray>{
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            if (strings.length == 0)
                return null;
          URL url =  NetworkUtils.buildUrl(strings[0], keyPref);
            String reply = null;
            try {
              reply =   NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONArray returnArray = null;
            try {
               returnArray =   NetworkUtils.resultArray(reply) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnArray;
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (jArray != null) {
                showWeatherDataView();
                mMovieAdapter.setMovieData(jArray);
            }
            else
                showErrorMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        if (mSortPref.contentEquals(sort_by_popularity)) {
            if (!action_sort_by_popularity.isChecked())
                action_sort_by_popularity.setChecked(true);
        }
        else {
            if (!action_sort_by_rating.isChecked())
                action_sort_by_rating.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idMenuSelected = item.getItemId();
        switch (idMenuSelected) {
            case R.id.action_sort_by_popularity:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                mSortPref = sort_by_popularity;
                updateLayout();
                return true;
            case R.id.action_sort_by_rating:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                mSortPref = sort_by_highest_rated;
                updateLayout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
