package com.georgestudenko.popularmovies.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by george on 06/03/2017.
 */

public class Movie implements Parcelable {
    private String mId;
    private String mTitle;
    private String mOverview;
    private String mRelease;
    private String mVote_average;
    private String mTrailersUrl;
    private String mReviewsUrl;
    private Uri mPoster;
    private String mRawPoster;
    private String mBigPoster;

    // Default constructor
    public Movie(){ }

    // Full constructor
    public Movie(String mId, String mTitle, String mOverview, String mRelease, String mVote_average, String mTrailersUrl, String mReviewsUrl, Uri mPoster, String mRawPoster, String mBigPoster) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mOverview = mOverview;
        this.mRelease = mRelease;
        this.mVote_average = mVote_average;
        this.mTrailersUrl = mTrailersUrl;
        this.mReviewsUrl = mReviewsUrl;
        this.mPoster = mPoster;
        this.mRawPoster = mRawPoster;
        this.mBigPoster = mBigPoster;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getRelease() {
        return mRelease;
    }

    public void setRelease(String mRelease) {
        this.mRelease = mRelease;
    }

    public String getVote_average() {
        return mVote_average;
    }

    public void setVote_average(String mVote_average) {
        this.mVote_average = mVote_average;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getTrailersUrl() {
        return mTrailersUrl;
    }

    public void setTrailersUrl(String mTrailersUrl) {
        this.mTrailersUrl = mTrailersUrl;
    }

    public String getReviewsUrl() {
        return mReviewsUrl;
    }

    public void setReviewsUrl(String mReviewsUrl) {
        this.mReviewsUrl = mReviewsUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getBigPoster() {
        return mBigPoster;
    }

    public void setBigPoster(String mBigPoster) {
        this.mBigPoster = mBigPoster;
    }

    public Uri getPoster() {
        return mPoster;
    }

    public void setPoster(Uri mPoster) {
        this.mPoster = mPoster;
    }

    public String getRawPoster() {
        return mRawPoster;
    }

    public void setRawPoster(String mRawPoster) {
        this.mRawPoster = mRawPoster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mOverview);
        dest.writeString(this.mRelease);
        dest.writeString(this.mVote_average);
        dest.writeString(this.mTrailersUrl);
        dest.writeString(this.mReviewsUrl);
        dest.writeParcelable(this.mPoster, flags);
        dest.writeString(this.mRawPoster);
        dest.writeString(this.mBigPoster);
    }

    protected Movie(Parcel in) {
        this.mId = in.readString();
        this.mTitle = in.readString();
        this.mOverview = in.readString();
        this.mRelease = in.readString();
        this.mVote_average = in.readString();
        this.mTrailersUrl = in.readString();
        this.mReviewsUrl = in.readString();
        this.mPoster = in.readParcelable(Uri.class.getClassLoader());
        this.mRawPoster = in.readString();
        this.mBigPoster = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
