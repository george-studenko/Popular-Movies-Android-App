package com.georgestudenko.popularmovies.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.georgestudenko.popularmovies.Models.Movie;
import com.georgestudenko.popularmovies.UI.MainActivity;
import com.georgestudenko.popularmovies.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by george on 15/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ImageViewHolder> {

    private List<Movie> mMovies;


    private final ItemClickListener mListener;

    public interface  ItemClickListener{
        void onPosterClick (int itemIndex);
    }

    public MovieAdapter(ItemClickListener listener){
        mListener=listener;
    }

    public Movie getMovieDetails(int position){
        Movie movie = null;
            if (mMovies != null) {
                movie = mMovies.get(position);
            }
        return  movie;
    }

    public void setMoviesData(List<Movie> movies){
        mMovies=movies;
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

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
            if(mMovies!=null) {
                Movie item = mMovies.get(position);
                Uri poster = item.getPoster();

                if(MainActivity.getSortBy()== MainActivity.SORT_BY_FAVORITES){
                    Picasso.with(holder.imageHolder.getContext()).load(poster).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imageHolder);
                }else{
                    Picasso.with(holder.imageHolder.getContext()).load(poster).into(holder.imageHolder);
                }
            }
        }

    @Override
    public long getItemId(int position) {
        long id=0;
            if(mMovies!=null) {
                Movie item = mMovies.get(position);
                id = item.getId();
            }
        return id;
    }

    @Override
    public int getItemCount() {
        return (mMovies==null)? 0 : mMovies.size();
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
