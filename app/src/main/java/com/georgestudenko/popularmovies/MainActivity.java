package com.georgestudenko.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.georgestudenko.popularmovies.Adapters.MovieAdapter;
import com.georgestudenko.popularmovies.Utils.NetworkUtils;
import com.georgestudenko.popularmovies.Utils.TheMovieDBQueryTask;
import com.georgestudenko.popularmovies.databinding.MainActivityBinding;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener{

    /***********************************************************************************************
     * Please register and create an API Key here https://www.themoviedb.org/ and replace it in    *
     * the apiKey variable below:                                                                  *
     * ********************************************************************************************/

    private final String apiKey = BuildConfig.API_KEY;

    private final String scheme="http";
    private final String secureScheme="https://";
    private final String host = "api.themoviedb.org";
    private final String apiVersion="3";
    private final String type="movie";
    public static final String SORT_BY_TOP_RATED ="top_rated";
    public static final String SORT_BY_POPULAR ="popular";
    public static final String SORT_BY_FAVORITES ="userFavoriteMovies";

    private static String currentSortBy="popular";
    private String currentPage="1";

    public MovieAdapter mMovieAdapter;
    public MainActivityBinding mBinding;

    public void setSortBy(String value){
        currentSortBy = value;
    }
    public static String getSortBy(){return currentSortBy;}

    public void p(String text){
        System.out.println(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.main_activity);

        int columns= getNumberOfColumns(this,180);

        GridLayoutManager layoutManager = new GridLayoutManager(this,columns);
        layoutManager.setAutoMeasureEnabled(true);
        mBinding.rvImages.setLayoutManager(layoutManager);
        mBinding.rvImages.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mBinding.rvImages.setAdapter(mMovieAdapter);
        if(NetworkUtils.isConnected(this.getApplicationContext())) {
            hideNoInternetError();
            new TheMovieDBQueryTask().execute(buildURL(scheme, host, apiVersion, type, currentSortBy, apiKey, currentPage), this);
        }else{
            hideNoInternetError();
            Toast.makeText(this.getApplication(),this.getString(R.string.error),Toast.LENGTH_SHORT).show();
            setSortBy(SORT_BY_FAVORITES);
            this.setTitle(R.string.my_favorite_movies);
            new TheMovieDBQueryTask().execute(buildURL(scheme, host, apiVersion, type, currentSortBy, apiKey, currentPage), this);
        }
    }
    public void showNoInternetError(){
        mBinding.tvNoInternetError.setVisibility(View.VISIBLE);
        mBinding.goToFavoritesButton.setVisibility(View.VISIBLE);
    }
    public void hideNoInternetError(){
        mBinding.tvNoInternetError.setVisibility(View.INVISIBLE);
        mBinding.goToFavoritesButton.setVisibility(View.INVISIBLE);
    }

    public static int getNumberOfColumns(Context context, int divider) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        float dpWidth = display.widthPixels / display.density;
        int columns = (int) (dpWidth / divider);
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

        if (id == R.id.action_favorite_movies) {
            openFavorites(null);
            return true;
        }

        if(NetworkUtils.isConnected(this.getApplicationContext())) {
            hideNoInternetError();

            if (id == R.id.action_sort_by_most_popular) {
                setSortBy(SORT_BY_POPULAR);
                this.setTitle(R.string.title_sorted_by_popular);
                new TheMovieDBQueryTask().execute(buildURL(scheme,host,apiVersion,type,currentSortBy,apiKey,currentPage), this);
                return true;
            }

            if (id == R.id.action_sort_by_top_rated) {
                setSortBy(SORT_BY_TOP_RATED);
                this.setTitle(R.string.title_sorted_by_top_rated);
                new TheMovieDBQueryTask().execute(buildURL(scheme,host,apiVersion,type,currentSortBy,apiKey,currentPage), this);
                return true;
            }
        }else{
            if (id == R.id.action_sort_by_most_popular) {
                this.setTitle(R.string.title_sorted_by_popular);
            }
            if (id == R.id.action_sort_by_top_rated) {
                this.setTitle(R.string.title_sorted_by_top_rated);
            }
            mBinding.rvImages.removeAllViewsInLayout();
            showNoInternetError();
            Toast.makeText(this.getApplication(),this.getString(R.string.error),Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(this,DetailsActivity.class);
        try{
            intent.putExtra("title", movie.getString("original_title"));

            String posterPath=movie.getString("poster_path").replace("/", "");
            Uri posterUri=mMovieAdapter.buildPosterURL(posterPath);
            String poster=posterUri.toString();
            intent.putExtra("poster", poster);
            intent.putExtra("rawPoster", posterPath);

            intent.putExtra("release", movie.getString("release_date"));
            intent.putExtra("vote", movie.getString("vote_average"));
            intent.putExtra("overview", movie.getString("overview"));
            intent.putExtra("id", movie.getString("id"));

            String trailersAPIUrl = secureScheme+host+"/"+apiVersion+"/"+type+"/"+movie.getString("id")+"/videos?api_key="+apiKey;
            String reviewsAPIUrl = secureScheme+host+"/"+apiVersion+"/"+type+"/"+movie.getString("id")+"/reviews?api_key="+apiKey;
            intent.putExtra("trailersUrl", trailersAPIUrl);
            intent.putExtra("reviewsUrl", reviewsAPIUrl);

            if(getSortBy()==SORT_BY_FAVORITES && !NetworkUtils.isConnected(getApplicationContext())){
                intent.putExtra("reviews", movie.getString("reviews"));
                intent.putExtra("bigPoster", movie.getString("bigPoster"));
            }

        }catch (JSONException ex){
            ex.printStackTrace();
        }
        startActivity(intent);
    }

    public void openFavorites(View view) {
        hideNoInternetError();
        setSortBy(SORT_BY_FAVORITES);
        this.setTitle(R.string.my_favorite_movies);
        this.setTitle(R.string.my_favorite_movies);
        new TheMovieDBQueryTask().execute(buildURL(scheme,host,apiVersion,type,currentSortBy,apiKey,currentPage), this);
    }
}
