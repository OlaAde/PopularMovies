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
import android.widget.ImageView;
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
    private String mSortPref = sort_by_popularity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mTextViewTitle = (TextView) findViewById(R.id.tv_item);
        mImageViewitem = (ImageView) findViewById(R.id.iv_item);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3 ,GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        updateLayout();
    }

    public void updateLayout(){
        mMovieAdapter.setMovieData(null);
        new LoadMoviesTask().execute(mSortPref);
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
        protected JSONArray doInBackground(String... strings) {
            if (strings.length == 0)
                return null;
          URL url =  NetworkUtils.buildUrl(strings[0]);
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
            if (jArray != null) {
                mMovieAdapter.setMovieData(jArray);
            }
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
