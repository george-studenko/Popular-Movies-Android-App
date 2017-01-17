package com.georgestudenko.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class Details extends AppCompatActivity {
    TextView mTitle;
    ImageView mPoster;
    TextView mRelease;
    TextView mOverview;
    TextView mRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        mTitle=(TextView) findViewById(R.id.title);
        mPoster=(ImageView) findViewById(R.id.poster);
        mRelease=(TextView) findViewById(R.id.release);
        mOverview=(TextView) findViewById(R.id.overview);
        mRating=(TextView) findViewById(R.id.votes);

        if(intent.hasExtra("title")){
            mTitle.setText(intent.getStringExtra("title"));
            Uri posterUri= Uri.parse(intent.getStringExtra("poster"));
            Picasso.with(getApplicationContext()).load(posterUri).into(mPoster);

            String release=intent.getStringExtra("release");//.substring(0,4);
            mRelease.setText(release);
            mOverview.setText(intent.getStringExtra("overview"));
            mRating.setText(intent.getStringExtra("vote")+"/10");
        }
    }


}
