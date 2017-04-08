package com.example.android.movielister;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mUsersRatingTextView;
    private TextView mRealeaseDateTextView;
    private ImageView mThumbnailImageView;
    private String basePicasso = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitleTextView = (TextView) findViewById(R.id.detail_tv);
        mDescriptionTextView = (TextView) findViewById(R.id.description_tv);
        mUsersRatingTextView = (TextView) findViewById(R.id.users_rating_tv);
        mRealeaseDateTextView = (TextView) findViewById(R.id.release_date_tv);
        mThumbnailImageView = (ImageView) findViewById(R.id.detail_iv);

        Intent intent = getIntent();
        String movieTitle = intent.getStringExtra("movieTitle");
        String movieDescription = intent.getStringExtra("description");
        String usersRating = intent.getStringExtra("usersRating");
        String releaseDate = intent.getStringExtra("releaseDate");
        String thumbnail = intent.getStringExtra("thumbnail");

        Uri uri = Uri.parse(basePicasso + thumbnail);
        Picasso.with(this).load(uri).into(mThumbnailImageView);

        mTitleTextView.setText(movieTitle);
        mDescriptionTextView.setText(movieDescription);
        mUsersRatingTextView.setText(usersRating);
        mRealeaseDateTextView.setText(releaseDate );
    }
}
