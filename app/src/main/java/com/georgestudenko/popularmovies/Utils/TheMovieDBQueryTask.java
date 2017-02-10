package com.georgestudenko.popularmovies.Utils;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;

import com.georgestudenko.popularmovies.Data.FavoriteMovieContract.FavoriteMovieEntry;
import com.georgestudenko.popularmovies.DetailsActivity;
import com.georgestudenko.popularmovies.MainActivity;
import com.georgestudenko.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by george on 17/01/2017.
 */

public class TheMovieDBQueryTask extends AsyncTask<Object,Void,String> {
    private MainActivity mMainActivityCallerActivity;
    private String mUrl;
    private final String POPULAR_MOVIE_LIST_URL="http://api.themoviedb.org/3/movie/popular";
    private final String TOP_RATED_MOVIE_LIST_URL="http://api.themoviedb.org/3/movie/top_rated";

    @Override
    protected String doInBackground(Object... param) {
        URL url = (URL) param[0];
        mUrl = url.toString();
        mMainActivityCallerActivity = (MainActivity) param[1];

        String moviesData= null;
        try {
            if(mUrl.contains(POPULAR_MOVIE_LIST_URL) || mUrl.contains(TOP_RATED_MOVIE_LIST_URL)) {
                moviesData = NetworkUtils.getResponseFromHttpUrl(url);
            }else {
                JSONArray favoriteMoviesJSON = new JSONArray();
                JSONObject movie = new JSONObject();
                JSONObject finalMoviesJSONObject = new JSONObject();

                ContentResolver contentResolver = mMainActivityCallerActivity.getContentResolver();
                Cursor cursor = contentResolver.query(FavoriteMovieEntry.FAVORITE_MOVIE_CONTENT_URI, null, null, null, null);
                cursor.moveToFirst();

                    try {
                        while (cursor.moveToNext()) {
                            movie = new JSONObject();
                            movie.put("id", cursor.getString(FavoriteMovieEntry.COLUMN_MOVIES_ID_INDEX));
                            movie.put("overview", cursor.getString(FavoriteMovieEntry.COLUMN_SYNOPSIS_INDEX));
                            movie.put("release_date", cursor.getString(FavoriteMovieEntry.COLUMN_RELEASE_DATE_INDEX));
                            movie.put("vote_average", cursor.getString(FavoriteMovieEntry.COLUMN_RATING_INDEX));
                            movie.put("poster_path", cursor.getString(FavoriteMovieEntry.COLUMN_POSTER_INDEX));
                            movie.put("original_title", cursor.getString(FavoriteMovieEntry.COLUMN_TITLE_INDEX));
                            movie.put("reviews", cursor.getString(FavoriteMovieEntry.COLUMN_REVIEWS_INDEX));
                            movie.put("bigPoster", cursor.getString(FavoriteMovieEntry.COLUMN_BIG_POSTER_INDEX));
                            favoriteMoviesJSON.put(movie);
                    }
                        cursor.close();
                        finalMoviesJSONObject.put("results",favoriteMoviesJSON);
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }
                moviesData = finalMoviesJSONObject.toString();
            }

        }catch (IOException ex){
            System.out.println("ERROR GETTING RAWDATA: ");
            ex.printStackTrace();
        }
        return moviesData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        if(s!=null) {
            JSONObject movieData = null;
            try {
                movieData = new JSONObject(s);
                    mMainActivityCallerActivity.mMovieAdapter.setMoviesData(movieData);
            } catch (JSONException ex) {
                System.out.println("ERROR onPostExcecute");
                ex.printStackTrace();
            }
        }else{
            String errorMessage= Resources.getSystem().getString(R.string.error);
            System.out.println("ERROR MESSAGE ====> "+ errorMessage);
        }
    }
}