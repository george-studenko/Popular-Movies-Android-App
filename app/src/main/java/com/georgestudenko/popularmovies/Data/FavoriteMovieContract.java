package com.georgestudenko.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by george on 06/02/2017.
 */

public class FavoriteMovieContract {
    public static final String AUTHORITY = "com.georgestudenko.popularmovies";
    public static final String SCHEME = "content://";
    public static final String BASE_CONTENT = SCHEME + AUTHORITY;

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public static final String FAVORITE_MOVIE_PATH = "favoriteMovie";


    public static final class FavoriteMovieEntry implements BaseColumns{

        public static final Uri FAVORITE_MOVIE_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(FAVORITE_MOVIE_PATH)
                .build();

        public static final String TABLE_NAME = "favoriteMovie";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_REVIEWS_URL = "reviewsURL";
        public static final String COLUMN_TRAILER_URL = "trailersURL";
        public static final String COLUMN_REVIEWS_CONTENT = "reviews";
        public static final String COLUMN_BIG_POSTER = "bigPoster";


        public static final int COLUMN_ID_INDEX = 0;
        public static final int COLUMN_MOVIES_ID_INDEX = 1;
        public static final int COLUMN_SYNOPSIS_INDEX = 2;
        public static final int COLUMN_RATING_INDEX = 3;
        public static final int COLUMN_RELEASE_DATE_INDEX = 4;
        public static final int COLUMN_POSTER_INDEX = 5;
        public static final int COLUMN_TITLE_INDEX = 6;
        public static final int COLUMN_REVIEWS_URL_INDEX = 7;
        public static final int COLUMN_TRAILER_URL_INDEX = 8;
        public static final int COLUMN_REVIEWS_INDEX = 9;
        public static final int COLUMN_BIG_POSTER_INDEX = 10;

   }

}
