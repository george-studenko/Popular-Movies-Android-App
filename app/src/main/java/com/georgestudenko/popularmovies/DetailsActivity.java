package com.georgestudenko.popularmovies;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.georgestudenko.popularmovies.Data.FavoriteMovieContract;
import com.georgestudenko.popularmovies.Data.FavoriteMovieContract.FavoriteMovieEntry;
import com.georgestudenko.popularmovies.Utils.NetworkUtils;
import com.georgestudenko.popularmovies.databinding.DetailsActivityBinding;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class DetailsActivity extends AppCompatActivity {
    boolean mIsFavoriteMovie;
    String mMovieId;
    String mYoutubeVideoBaseUrl="https://www.youtube.com/watch?v=";
    String mYoutubeThumbnailBaseUrl="http://img.youtube.com/vi/#ID#/hqdefault.jpg";
    DetailsActivityBinding mBinding;
    String mPoster;
    String mBigPoster;
    Uri mPosterUri;
    String mReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviews="";
        mBinding = DataBindingUtil.setContentView(this,R.layout.details_activity);
        Intent intent = getIntent();

        if(intent.hasExtra("title")){
            mMovieId = intent.getStringExtra("id");
            mBinding.title.setText(intent.getStringExtra("title"));
            mPoster= intent.getStringExtra("poster");

            System.out.println("POSTER:  "+ mPoster);
            mPosterUri= Uri.parse(mPoster);
            Picasso.with(getApplicationContext()).load(mPosterUri).networkPolicy(NetworkPolicy.OFFLINE).into(mBinding.poster);

            String release=intent.getStringExtra("release");
            mBinding.release.setText(release);
            mBinding.overview.setText(intent.getStringExtra("overview"));
            mBinding.votes.setText(intent.getStringExtra("vote")+"/10");

            if (MainActivity.getSortBy()==MainActivity.SORT_BY_FAVORITES && !NetworkUtils.isConnected(getApplicationContext())){
                String thumbUrl = getIntent().getStringExtra("bigPoster");
                Picasso.with(getApplicationContext()).load(thumbUrl).networkPolicy(NetworkPolicy.OFFLINE).into(mBinding.bigTopPoster);
                mBinding.reviews.loadDataWithBaseURL(null, getIntent().getStringExtra("reviews"), getString(R.string.html_mime), getString(R.string.utf8), null);
                System.out.println("REVIEWS: " + getIntent().getStringExtra("reviews"));

                mBinding.trailersLabel.setVisibility(View.INVISIBLE);

            }
            else if(NetworkUtils.isConnected(getApplicationContext())){
                getTrailers(intent.getStringExtra("trailersUrl"));
                getReviews(intent.getStringExtra("reviewsUrl"));
            }

            mIsFavoriteMovie = isMarkedAsFavorite(mMovieId);

            if(mIsFavoriteMovie){
                mBinding.favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            }else {
                mBinding.favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
            }
        }
    }

    private boolean isMarkedAsFavorite (String movieId){
        ContentResolver contentResolver = getContentResolver();
        boolean isFavorite = false;
        Uri uri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.FAVORITE_MOVIE_CONTENT_URI, Long.parseLong(movieId));
        try {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.getCount()>0) isFavorite = true;
            cursor.close();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return isFavorite;
    }

    public void toogleFavorite(View view) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.FAVORITE_MOVIE_CONTENT_URI,Long.parseLong(mMovieId));

        if(mIsFavoriteMovie){
            contentResolver.delete(uri,null,null);
            mIsFavoriteMovie = false;
            Toast.makeText(getApplicationContext(), mBinding.title.getText().toString() + " deleted from favorites", Toast.LENGTH_LONG).show();
            mBinding.favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
        }else {
            try {
                ContentValues cv = new ContentValues();
                cv.put(FavoriteMovieEntry.COLUMN_MOVIE_ID, mMovieId);
                cv.put(FavoriteMovieEntry.COLUMN_MOVIE_SYNOPSIS, mBinding.overview.getText().toString());
                cv.put(FavoriteMovieEntry.COLUMN_MOVIE_RATING, mBinding.votes.getText().toString());
                cv.put(FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, mBinding.release.getText().toString());
                cv.put(FavoriteMovieEntry.COLUMN_MOVIE_POSTER, getIntent().getStringExtra("rawPoster"));
                cv.put(FavoriteMovieEntry.COLUMN_TITLE, mBinding.title.getText().toString());
                cv.put(FavoriteMovieEntry.COLUMN_BIG_POSTER, mBigPoster);
                cv.put(FavoriteMovieEntry.COLUMN_REVIEWS_CONTENT, mReviews);
                cv.put(FavoriteMovieEntry.COLUMN_REVIEWS_URL, getIntent().getStringExtra("reviewsUrl"));
                cv.put(FavoriteMovieEntry.COLUMN_TRAILER_URL, getIntent().getStringExtra("trailersUrl"));
                Uri insertedUri = contentResolver.insert(uri, cv);

                mIsFavoriteMovie = true;
                Toast.makeText(getApplicationContext(), mBinding.title.getText().toString() + " added to favorites!", Toast.LENGTH_LONG).show();

                mBinding.favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            }
            catch (Exception ex){
                System.out.println("ERROR ON GETTING INSERTED DATA: ");
                ex.printStackTrace();
            }
        }
    }

        public void getTrailers(String apiUrl){
            RequestQueue queue = Volley.newRequestQueue(this);
            final Context context = getApplicationContext();
            mBinding.trailers.setColumnCount(MainActivity.getNumberOfColumns(context,200));
            StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonString = new JSONObject(response);
                                JSONArray trailers =  jsonString.getJSONArray("results");

                                // TODO: implement dynamic titles
                                boolean hasTrailers=false;
                                boolean hasTeasers = false;
                                boolean hasMultipleTrailers=false;
                                boolean hasMultipleTeasers = false;

                                for (int i = 0 ; i < trailers.length() ; i++) {
                                    JSONObject trailer = new JSONObject(trailers.getString(i));
                                    final String videoUrl=mYoutubeVideoBaseUrl+trailer.getString("key");

                                    final String videoCaption=trailer.getString("name");
                                    final String videoType=trailer.getString("type");

                                    String thumbUrl=mYoutubeThumbnailBaseUrl.replace("#ID#",trailer.getString("key"));

                                    LinearLayout trailerContainer = new LinearLayout(context);
                                    trailerContainer.setOrientation(LinearLayout.VERTICAL);

                                    ImageView trailerImageView = new ImageView(context);
                                    trailerImageView.setPadding(20,20,20,20);
                                    Picasso.with(context).load(Uri.parse(thumbUrl)).into(trailerImageView);
                                    if (i==0){
                                        mBigPoster= thumbUrl.replace("hqdefault","maxresdefault");
                                        if(NetworkUtils.isConnected(context)){
                                            new Picasso.Builder(context)
                                                    .downloader(new OkHttpDownloader(context, Integer.MAX_VALUE))
                                                    .build()
                                                    .load(Uri.parse(mBigPoster))
                                                    .into(mBinding.bigTopPoster);
                                        }else {
                                            Picasso.with(context).load(Uri.parse(mBigPoster)).into(mBinding.bigTopPoster);
                                        }
                                    }

                                    trailerImageView.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                Intent launchTrailer = new Intent(Intent.ACTION_VIEW ,Uri.parse(videoUrl));
                                                                                startActivity(launchTrailer);
                                                                            }
                                                                        });

                                    TextView trailerCaption = new TextView(context);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    trailerCaption.setLayoutParams(params);
                                    trailerCaption.setText(videoCaption);
                                    trailerCaption.setTextColor(getResources().getColor(R.color.colorBlack));
                                    trailerCaption.setGravity(Gravity.CENTER);

                                    trailerContainer.addView(trailerImageView);
                                    trailerContainer.addView(trailerCaption);

                                            mBinding.trailers.addView(trailerContainer);
                                }
                            }catch (JSONException ex){
                                ex.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), R.string.error_loading_trailers, Toast.LENGTH_LONG).show();
                }
            });
            queue.add(stringRequest);
        }

   public void getReviews(String apiUrl){
        RequestQueue queue = Volley.newRequestQueue(this);
        final Context context = getApplicationContext();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonString = new JSONObject(response);
                            JSONArray reviews =  jsonString.getJSONArray("results");
                            mReviews="";
                            if (reviews.length()==0){
                                mBinding.reviews.loadDataWithBaseURL(null, getString(R.string.no_reviews),  getString(R.string.html_mime), getString(R.string.utf8), null);
                            } else {
                                for (int i = 0; i < reviews.length(); i++) {
                                    JSONObject trailer = new JSONObject(reviews.getString(i));

                                    final String author = trailer.getString("author");
                                    final String review = trailer.getString("content");

                                    mReviews = mReviews + getString(R.string.author) + author + getString(R.string.review) + review.replace("\n", "<br/>") + getString(R.string.review_separator);
                                }

                                mBinding.reviews.loadDataWithBaseURL(null, mReviews, getString(R.string.html_mime), getString(R.string.utf8), null);
                            }
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.error_loading_reviews, Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }
}
