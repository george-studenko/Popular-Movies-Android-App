package com.georgestudenko.popularmovies.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.georgestudenko.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by george on 15/01/2017.
 */

public class NetworkUtils {

    private final static String apiKey = BuildConfig.API_KEY;

    private final static String scheme="http";
    private final static String secureScheme="https://";
    private final static String host = "api.themoviedb.org";
    private final static String apiVersion="3";
    private final static String type="movie";


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
    public static URL buildMoviesURL(String sortBy, String page){
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

    public static String buildResourcesURL(String movieId, String resourceType){
        URL URL= null;
        Uri.Builder builder= new Uri.Builder();
        builder.scheme(secureScheme)
                .authority(host)
                .appendPath(apiVersion)
                .appendPath(type)
                .appendPath(movieId)
                .appendPath(resourceType)
                .appendQueryParameter("api_key", apiKey)
                .build();
        try {
            URL = new URL(builder.build().toString());
        }catch (MalformedURLException ex){
            System.out.println("ERROR PARSING URL: ");
            ex.printStackTrace();
        }
        return URL.toString();
    }
}
