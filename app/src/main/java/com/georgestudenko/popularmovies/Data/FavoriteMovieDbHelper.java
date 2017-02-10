package com.georgestudenko.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.georgestudenko.popularmovies.Data.FavoriteMovieContract.FavoriteMovieEntry;

/**
 * Created by george on 06/02/2017.
 */

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popularMovies.db";
    public static final int DATABASE_VERSION = 5;

    public FavoriteMovieDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         final String CREATE_DB_QUERY =
                "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                        FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
                        FavoriteMovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL,"+
                        FavoriteMovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, "+
                        FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " DATE NOT NULL, " +
                        FavoriteMovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                        FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                        FavoriteMovieEntry.COLUMN_REVIEWS_URL + " TEXT NOT NULL," +
                        FavoriteMovieEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL," +
                        FavoriteMovieEntry.COLUMN_REVIEWS_CONTENT + " TEXT NOT NULL," +
                        FavoriteMovieEntry.COLUMN_BIG_POSTER + " TEXT NOT NULL);";

        db.execSQL(CREATE_DB_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
