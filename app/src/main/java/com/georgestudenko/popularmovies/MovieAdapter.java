package com.georgestudenko.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by george on 15/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ImageViewHolder> {

    private JSONObject moviesData;
    private String imageScheme="http";
    private String imageHost="image.tmdb.org";
    private String imageFragmentT="t";
    private String imageFragmentP="p";
    private String imageSize="w342";

    private final ItemClickListener mListener;

    public interface  ItemClickListener{
        void onPosterClick (int itemIndex);
    }

    public MovieAdapter(ItemClickListener listener){
        mListener=listener;
    }

    public JSONObject getMovieDetails(int position){
        JSONObject movie = null;
        try {
            if (moviesData != null) {
                JSONArray items = moviesData.getJSONArray("results");
                movie = items.getJSONObject(position);
            }
        }catch (JSONException ex){

        }
        return  movie;
    }

    public void setMoviesData(JSONObject data){
        moviesData=data;
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.image_grid_item,parent,false);
        ImageViewHolder viewHolder= new ImageViewHolder(view);

        return viewHolder;
    }

    public Uri buildPosterURL(String posterURL){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(imageScheme)
                .authority(imageHost)
                .appendPath(imageFragmentT)
                .appendPath(imageFragmentP)
                .appendPath(imageSize)
                .appendPath(posterURL);
        return  builder.build();
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        try {
            if(moviesData!=null) {
                JSONArray items = moviesData.getJSONArray("results");
                JSONObject item = items.getJSONObject(position);
                String posterURL = item.getString("poster_path").replace("/", "");

                Uri poster=buildPosterURL(posterURL);

                Picasso.with(holder.imageHolder.getContext()).load(poster)//.networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.imageHolder);
            }
        }
        catch (JSONException ex){
            Log.d("onBindViewHolder",ex.toString());
            ex.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        long id=0;
        try {
            if(moviesData!=null) {
                JSONArray items = moviesData.getJSONArray("results");
                JSONObject item = items.getJSONObject(position);
                id = item.getLong("id");
            }
        }
        catch (JSONException ex){
            Log.d("getItemId",ex.toString());
            ex.printStackTrace();
        }
        return 0;// moviesData.length();
    }

    @Override
    public int getItemCount() {
        int count=0;
        try {
            if(moviesData!=null) {
                JSONArray items = moviesData.getJSONArray("results");
                count = items.length();
            }
        }
        catch (JSONException ex){

        }
        return count;
    }

    public void fillMoviesDataArray(JSONObject data){
            moviesData = data;
            notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        ImageView imageHolder;
        ProgressBar progressBar;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageHolder = (ImageView) itemView.findViewById(R.id.image_item);
            progressBar =  (ProgressBar) itemView.findViewById(R.id.progress_bar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onPosterClick(getAdapterPosition());

        }
    }
}
