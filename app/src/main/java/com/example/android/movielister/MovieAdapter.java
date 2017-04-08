package com.example.android.movielister;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by Adeogo on 4/7/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    Context context;
    private JSONArray movieArray;
    private final MovieAdapterOnclickHandler mClickHandler;

    public interface MovieAdapterOnclickHandler{
        void voidMethod(JSONObject param);
    }
 public MovieAdapter( MovieAdapterOnclickHandler movieAdapterOnclickHandler){
     mClickHandler = movieAdapterOnclickHandler;
 }


    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
     public final TextView mMovieTextView;
     public final ImageView mMovieImageView;

     public MovieAdapterViewHolder(View itemView) {
         super(itemView);
         mMovieTextView = (TextView) itemView.findViewById(R.id.tv_item);
         mMovieImageView = (ImageView) itemView.findViewById(R.id.iv_item);
         itemView.setOnClickListener(this);
     }

        @Override
        public void onClick(View view) {
            JSONObject jsonObject = null;
            int adapterPosition = getAdapterPosition();
            try {
                jsonObject = movieArray.getJSONObject(adapterPosition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mClickHandler.voidMethod(jsonObject);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context =  parent.getContext();
        int layoutForListItem = R.layout.list_item_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String movieTitle = null;
        String moviePosterStringUrl = null;
        String basePicasso = "http://image.tmdb.org/t/p/w185/";

        try {
            JSONObject newJSObject = movieArray.getJSONObject(position);
            movieTitle = newJSObject.getString("original_title");
            moviePosterStringUrl = newJSObject.getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse(basePicasso + moviePosterStringUrl);
        Log.v("ooknibxi", uri.toString());
        holder.mMovieTextView.setText(movieTitle);
        Picasso.with(context).load(uri).into(holder.mMovieImageView);


    }

    @Override
    public int getItemCount() {
        if(null == movieArray){
            return 0;
        }
        return movieArray.length();
    }


    public void setMovieData(JSONArray mMovieArray){
        movieArray = mMovieArray;
        notifyDataSetChanged();
    }
}
