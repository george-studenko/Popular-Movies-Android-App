package com.georgestudenko.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.georgestudenko.popularmovies.Data.FavoriteMovieContract.FavoriteMovieEntry;

/**
 * Created by george on 06/02/2017.
 */

public class FavoriteMovieProvider extends ContentProvider {
    final int LIST_OF_MOVIES=100;
    final int SINGLE_MOVIE=101;

    FavoriteMovieDbHelper dbHelper;

    UriMatcher mUriMatcher;

    private UriMatcher buildUriMatcher(){
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(FavoriteMovieContract.AUTHORITY,FavoriteMovieContract.FAVORITE_MOVIE_PATH,LIST_OF_MOVIES);
        mUriMatcher.addURI(FavoriteMovieContract.AUTHORITY,FavoriteMovieContract.FAVORITE_MOVIE_PATH+"/#",SINGLE_MOVIE);

        return  mUriMatcher;

    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteMovieDbHelper(getContext(),FavoriteMovieDbHelper.DATABASE_NAME,null,FavoriteMovieDbHelper.DATABASE_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mUriMatcher = buildUriMatcher();
        int match = mUriMatcher.match(uri);
        System.out.println("match: " + match);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor returningCursor = null;
        switch (match){
            case SINGLE_MOVIE:
                String id = uri.getLastPathSegment();
                returningCursor = db.query(FavoriteMovieEntry.TABLE_NAME,projection,FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ", new String[] {id},null,null,sortOrder);
                break;
            case LIST_OF_MOVIES:
                returningCursor = db.query(FavoriteMovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation uri:" + uri);
        }

        returningCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returningCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mUriMatcher = buildUriMatcher();
        int match = mUriMatcher.match(uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (match) {
            case(SINGLE_MOVIE):
                long id= db.insert(FavoriteMovieEntry.TABLE_NAME, null, values);
                if(id>0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieEntry.FAVORITE_MOVIE_CONTENT_URI, values.getAsLong(FavoriteMovieEntry.COLUMN_MOVIE_ID));
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mUriMatcher = buildUriMatcher();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int deletedRows=0;
        switch (match){
            case SINGLE_MOVIE:
                String id = uri.getLastPathSegment();
                deletedRows = db.delete(FavoriteMovieEntry.TABLE_NAME,FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ",new String[]{id});
                break;
            case LIST_OF_MOVIES:
                deletedRows = db.delete(FavoriteMovieEntry.TABLE_NAME,null,null);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation when trying to delete URI: "+ uri);
        }

        if (deletedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        mUriMatcher = buildUriMatcher();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int updatedRows=0;
        switch (match){
            case SINGLE_MOVIE:
                updatedRows = db.update(FavoriteMovieEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation when trying to delete URI: "+ uri);
        }
        if (updatedRows > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return updatedRows;
    }
}
