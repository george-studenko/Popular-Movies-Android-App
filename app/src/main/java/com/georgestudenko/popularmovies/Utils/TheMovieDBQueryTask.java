package com.georgestudenko.popularmovies.Utils;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.georgestudenko.popularmovies.Data.FavoriteMovieContract.FavoriteMovieEntry;
import com.georgestudenko.popularmovies.Models.Movie;
import com.georgestudenko.popularmovies.UI.MainActivity;
import com.georgestudenko.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.host;

/**
 * Created by george on 17/01/2017.
 */

public class TheMovieDBQueryTask extends AsyncTask<Object,Void,List<Movie>> {
    private MainActivity mMainActivityCallerActivity;
    private String mUrl;
    private final String POPULAR_MOVIE_LIST_URL="http://api.themoviedb.org/3/movie/popular";
    private final String TOP_RATED_MOVIE_LIST_URL="http://api.themoviedb.org/3/movie/top_rated";

    @Override
    protected List<Movie> doInBackground(Object... param) {
        URL url = (URL) param[0];

        mUrl= url.toString();

        mMainActivityCallerActivity = (MainActivity) param[1];
        List<Movie> movies = new ArrayList<Movie>();
        try {
            if(mUrl.contains(POPULAR_MOVIE_LIST_URL) || mUrl.contains(TOP_RATED_MOVIE_LIST_URL)) {

                try{
                    JSONArray moviesDataJSONArray = new JSONObject(NetworkUtils.getResponseFromHttpUrl(url)).getJSONArray("results");

                    for(int i=0; i<moviesDataJSONArray.length();i++){
                        JSONObject movie = moviesDataJSONArray.getJSONObject(i);
                        Uri posterUri = NetworkUtils.buildPosterURL(movie.getString("poster_path").replace("/",""));

                        String movieId =movie.getString("id");
                        Long movieIdLong = Long.parseLong(movieId);
                        String trailersAPIUrl = NetworkUtils.buildResourcesURL(movieId,"videos");
                        String reviewsAPIUrl = NetworkUtils.buildResourcesURL(movieId,"reviews");

                        Movie currentMovie = new Movie(
                                movieIdLong,
                                movie.getString("original_title"),
                                movie.getString("overview"),
                                movie.getString("release_date"),
                                movie.getString("vote_average"),
                                trailersAPIUrl,
                                reviewsAPIUrl,
                                posterUri,
                                movie.getString("poster_path").replace("/",""),
                                "",
                                "");

                        movies.add(currentMovie);
                    }

                }catch (JSONException ex){
                    ex.printStackTrace();
                }
            }else {
                ContentResolver contentResolver = mMainActivityCallerActivity.getContentResolver();
                Cursor cursor = contentResolver.query(FavoriteMovieEntry.FAVORITE_MOVIE_CONTENT_URI, null, null, null, null);
                cursor.moveToFirst();
                        while (cursor.moveToNext()) {
                            Uri posterUri = Uri.parse(cursor.getString(FavoriteMovieEntry.COLUMN_POSTER_INDEX));

                            Movie currentMovie = new Movie(
                                    cursor.getLong(FavoriteMovieEntry.COLUMN_ID_INDEX),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_TITLE_INDEX),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_SYNOPSIS_INDEX),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_RELEASE_DATE_INDEX),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_RATING_INDEX),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_TRAILER_URL_INDEX),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_REVIEWS_URL_INDEX),
                                    posterUri,
                                    cursor.getString(FavoriteMovieEntry.COLUMN_POSTER_INDEX).replace("/",""),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_BIG_POSTER_INDEX),
                                    cursor.getString(FavoriteMovieEntry.COLUMN_REVIEWS_INDEX));

                            movies.add(currentMovie);
                    }
                        cursor.close();
            }

        }catch (IOException ex){
            System.out.println("ERROR GETTING RAWDATA: ");
            ex.printStackTrace();
        }
        return movies;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if(movies!=null) {
            mMainActivityCallerActivity.mMovieAdapter.setMoviesData(movies);
        }else{
            String errorMessage= Resources.getSystem().getString(R.string.error);
            System.out.println("ERROR MESSAGE ====> "+ errorMessage);
        }
    }
}