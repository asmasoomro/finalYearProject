package com.example.fypapp;
import android.os.Parcel;

import android.os.Parcelable;
import android.util.Log;

import com.example.fypapp.model.FeedbackEntry;

public class ParseItem implements Parcelable {
    private String title;
    private boolean isFavorited;
    private String feedback;
    private float positivePercentage;
    private boolean isPopular;
    private boolean isPositiveFeedback;
    private FeedbackEntry feedbackEntry;

    public ParseItem() {
    }


    protected ParseItem(Parcel in) {
        title = in.readString();
        isFavorited = in.readByte() != 0;
        feedback = in.readString();
        positivePercentage = in.readFloat();
        isPopular = in.readByte() != 0;
        feedbackEntry = in.readParcelable(FeedbackEntry.class.getClassLoader());
    }

    public static final Creator<ParseItem> CREATOR = new Creator<ParseItem>() {
        @Override
        public ParseItem createFromParcel(Parcel in) {
            return new ParseItem(in);
        }

        @Override
        public ParseItem[] newArray(int size) {
            return new ParseItem[size];
        }
    };

    public ParseItem(String title, float positivePercentage) {
        this.title = title;
        this.isFavorited = false;
        this.positivePercentage = positivePercentage;
    }

    public ParseItem(String title, boolean isFavorited) {
        this.title = title;
        this.isFavorited = isFavorited;
    }
    public ParseItem(String title, float positivePercentage, boolean isPositiveFeedback, boolean isPopular, FeedbackEntry feedbackEntry) {
        this.title = title;
        this.positivePercentage = positivePercentage;
        this.isPositiveFeedback = isPositiveFeedback;
        this.isPopular = isPopular;
        this.feedbackEntry = feedbackEntry;
    }


    public ParseItem(String title, boolean isFavorited, String feedback, float positivePercentage) {
        this.title = title;
        this.isFavorited = isFavorited;
        this.feedback = feedback;
        this.positivePercentage = positivePercentage;
    }

    public ParseItem(String title, boolean isFavorited, String feedback, float positivePercentage, boolean isPopular) {
        this.title = title;
        this.isFavorited = isFavorited;
        this.feedback = feedback;
        this.positivePercentage = positivePercentage;
        this.isPopular = isPopular();

        Log.d("ParseItem", "Title: " + getTitle() + ", Positive Percentage: " + getPositivePercentage() + ", isPopular: " + isPopular());
    }

    public ParseItem(String title, float positivePercentage, boolean isPopular) {
        this.title = title;
        this.isFavorited = false;
        this.feedback = null;
        this.positivePercentage = positivePercentage;
        this.isPopular = isPopular;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFeedbackPositive(boolean isPositiveFeedback) {
        this.isPositiveFeedback = isPositiveFeedback;
    }

    public boolean isPositiveFeedback() {
        return isPositiveFeedback;
    }
    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public float getPositivePercentage() {
        return positivePercentage;
    }

    public void setPositivePercentage(float positivePercentage) {
        this.positivePercentage = positivePercentage;
    }

    public boolean isPopular() {
        return isPopular;
    }


    public void setPopular(boolean popular) {
        isPopular = popular;
    }



    public FeedbackEntry getFeedbackEntry() {
        return feedbackEntry;
    }

    public void setFeedbackEntry(FeedbackEntry feedbackEntry) {
        this.feedbackEntry = feedbackEntry;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeByte((byte) (isFavorited ? 1 : 0));
        dest.writeString(feedback);
        dest.writeFloat(positivePercentage);
        dest.writeByte((byte) (isPopular ? 1 : 0));
        dest.writeParcelable(feedbackEntry, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}

