package com.georgestudenko.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.georgestudenko.popularmovies.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener{

    /***********************************************************************************************
     * Please register and create an API Key here https://www.themoviedb.org/ and replace it in    *
     * the apiKey variable below:                                                                  *
     * ********************************************************************************************/

    private final String apiKey="INSERT_YOUR_API_KEY_HERE";

    private final String scheme="http";
    private final String host = "api.themoviedb.org";
    private final String apiVersion="3";
    private final String type="movie";
    public static final String sortByTopRated="top_rated";
    public static final String sortByPopular="popular";

    private String currentSortBy="popular";
    private String currentPage="1";

    private FrameLayout mGrid;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    public void setSortBy(String value){
        currentSortBy=value;
    }

    public String getSortBy(){
        return currentSortBy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mGrid = (FrameLayout) findViewById(R.id.gridView);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_images);
        int columns= getNumberOfColumns(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this,columns);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        new TheMovieDBQueryTask().execute(buildURL(scheme,host,apiVersion,type,currentSortBy,apiKey,currentPage));
    }

    public static int getNumberOfColumns(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        float dpWidth = display.widthPixels / display.density;
        int columns = (int) (dpWidth / 180);
        return columns;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_most_popular) {
            setSortBy(sortByPopular);
            this.setTitle(R.string.title_sorted_by_popular);
            new TheMovieDBQueryTask().execute(buildURL(scheme,host,apiVersion,type,currentSortBy,apiKey,currentPage));
            return true;
        }

        if (id == R.id.action_sort_by_top_rated) {
            setSortBy(sortByTopRated);
            this.setTitle(R.string.title_sorted_by_top_rated);
            new TheMovieDBQueryTask().execute(buildURL(scheme,host,apiVersion,type,currentSortBy,apiKey,currentPage));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private URL buildURL(String scheme, String host,String apiVersion,String type, String sortBy, String apiKey, String page){
        URL URL= null;
        Uri.Builder builder= new Uri.Builder();
        builder.scheme(scheme)
                .authority(host)
                .appendPath(apiVersion)
                .appendPath(type)
                .appendPath(sortBy)
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("page",page)
                .build();
        try {
            URL = new URL(builder.build().toString());
        }catch (MalformedURLException ex){
            System.out.println("ERROR PARSING URL: ");
            ex.printStackTrace();
        }
        return URL;
    }

    @Override
    public void onPosterClick(int itemIndex) {
        JSONObject movie = mMovieAdapter.getMovieDetails(itemIndex);
        Intent intent = new Intent(this,Details.class);
        try{
            intent.putExtra("title", movie.getString("original_title"));

            String posterPath=movie.getString("poster_path").replace("/", "");
            Uri posterUri=mMovieAdapter.buildPosterURL(posterPath);
            String poster=posterUri.toString();
            intent.putExtra("poster", poster);

            intent.putExtra("release", movie.getString("release_date"));
            intent.putExtra("vote", movie.getString("vote_average"));
            intent.putExtra("overview", movie.getString("overview"));


        }catch (JSONException ex){

        }

        startActivity(intent);
    }

    public class TheMovieDBQueryTask extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... param) {
            URL url=param[0];
            String jsonData= null;
            try {
                jsonData= NetworkUtils.getResponseFromHttpUrl(url);
            }catch (IOException ex){
                System.out.println("ERROR GETTING RAWDATA: ");
                ex.printStackTrace();
            }
            return jsonData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject movieData=null;
            try {
                movieData = new JSONObject(s);
            }
            catch (JSONException ex){
                System.out.println("ERROR onPostExcecute");
                ex.printStackTrace();
            }
            mMovieAdapter.setMoviesData(movieData);
        }
    }
}
