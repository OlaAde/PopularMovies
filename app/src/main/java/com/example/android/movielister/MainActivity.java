package com.example.android.movielister;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movielister.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnclickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ImageButton mRightButton;
    private ImageButton mLeftButton;
    private TextView mPageTextview;


    private String sort_by_popularity = "popularity.desc";
    private String sort_by_highest_rated = "vote_average.desc";
    private String mSortPref = null;
    int mCurrentPageNo = 1, mTotalPageNo = 0;
    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    private String keyPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortPref = sort_by_popularity;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mRightButton = (ImageButton) findViewById(R.id.right_arrow);
        mPageTextview = (TextView) findViewById(R.id.page_num_tv);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        keyPref = getString((R.string.tmdb_api_key));
        setmLeftButton();
        setmRightButton();
        updateLayout();
    }

    public void updateLayout() {
        if(mCurrentPageNo == 1)
            mLeftButton.setVisibility(View.INVISIBLE);
        else
            mLeftButton.setVisibility(View.VISIBLE);



        if(isNetworkAvailable() == true){
        showWeatherDataView();
        mMovieAdapter.setMovieData(null);
        new LoadMoviesTask().execute(mSortPref);
        mPageTextview.setText("Page " + mCurrentPageNo + " of " + mTotalPageNo );
        }
        else
        showErrorMessage();

        if (mCurrentPageNo == mTotalPageNo)
            mRightButton.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     */
    private void showWeatherDataView() {
        /* First, to make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, to make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }



    @Override
    public void voidMethod(JSONObject param) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
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

    public void setmLeftButton(){
        mLeftButton = (ImageButton) findViewById(R.id.left_arrow);
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPageNo = mCurrentPageNo - 1;
                mPageTextview.setText(Integer.toString(mCurrentPageNo));
                updateLayout();
            }
        });
    }

    public void setmRightButton(){
        mRightButton = (ImageButton) findViewById(R.id.right_arrow);
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPageNo = mCurrentPageNo + 1;
                mPageTextview.setText(Integer.toString(mCurrentPageNo));
                updateLayout();
            }
        });
    }

    public class LoadMoviesTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            if (strings.length == 0)
                return null;
            URL url = NetworkUtils.buildUrl(strings[0], Integer.toString(mCurrentPageNo), keyPref);
            String reply = null;
            try {
                reply = NetworkUtils.getResponseFromHttpUrl(url);
                mTotalPageNo = NetworkUtils.getTotalNumberOfPages(reply);

            } catch (IOException e) {
                e.printStackTrace();
            }


            JSONArray returnArray = null;
            try {
                returnArray = NetworkUtils.resultArray(reply);
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
            } else
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
        } else {
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

    public boolean isNetworkAvailable() {
        boolean status = false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            status = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

}
