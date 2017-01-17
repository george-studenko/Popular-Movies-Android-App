package com.georgestudenko.popularmovies.Utils;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;

import com.georgestudenko.popularmovies.MainActivity;
import com.georgestudenko.popularmovies.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


/**
 * Created by george on 17/01/2017.
 */

public class TheMovieDBQueryTask extends AsyncTask<Object,Void,String> {
    private MainActivity mCallerActivity;

    @Override
    protected String doInBackground(Object... param) {
        URL url=(URL)param[0];
        mCallerActivity = (MainActivity) param[1];
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
        if(s!=null) {
            JSONObject movieData = null;
            try {
                movieData = new JSONObject(s);
            } catch (JSONException ex) {
                System.out.println("ERROR onPostExcecute");
                ex.printStackTrace();
            }
            mCallerActivity.mMovieAdapter.setMoviesData(movieData);
        }else{
            String errorMessage= Resources.getSystem().getString(R.string.error);
            System.out.println("ERROR MESSAGE ====> "+ errorMessage);
        }
    }
}