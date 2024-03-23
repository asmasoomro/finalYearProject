package com.example.fypapp.model;
import android.os.Parcel;
import android.os.Parcelable;

public class FeedbackEntry implements Parcelable {

    private String feedback;
    private String activityName;
    private float sentimentScore;
    private float positivePercentage;
    private float negativePercentage;
    private boolean positiveFeedback;
    private boolean isPositiveFeedback;
    private boolean isPopular;

    // Default constructor
    public FeedbackEntry() {
        // Default constructor required for Firebase
    }

    public FeedbackEntry(String activityName, float positivePercentage, float negativePercentage, boolean positiveFeedback) {
        this.activityName = activityName;
        this.positivePercentage = positivePercentage;
        this.negativePercentage = negativePercentage;
        this.positiveFeedback = positiveFeedback;
    }

    public FeedbackEntry(String feedback, float sentimentScore) {
        this.feedback = feedback;
        this.sentimentScore = sentimentScore;
    }
    public FeedbackEntry(String feedback, float sentimentScore, float positivePercentage) {
        this.feedback = feedback;
        this.sentimentScore = sentimentScore;
        this.positivePercentage = positivePercentage;
        this.isPositiveFeedback = positivePercentage > 70.0f; // Determine if it's positive feedback based on the percentage
        this.isPopular = positivePercentage > 70.0f; // Change the threshold as needed
    }


    // Getter and setter methods

    // Parcelable implementation
    protected FeedbackEntry(Parcel in) {
        feedback = in.readString();
        sentimentScore = in.readFloat();
        positivePercentage = in.readFloat();
        isPositiveFeedback = in.readByte() != 0;
        isPopular = in.readByte() != 0;
    }

    public static final Creator<FeedbackEntry> CREATOR = new Creator<FeedbackEntry>() {
        @Override
        public FeedbackEntry createFromParcel(Parcel in) {
            return new FeedbackEntry(in);
        }

        @Override
        public FeedbackEntry[] newArray(int size) {
            return new FeedbackEntry[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(feedback);
        dest.writeFloat(sentimentScore);
        dest.writeFloat(positivePercentage);
        dest.writeByte((byte) (isPositiveFeedback ? 1 : 0));
        dest.writeByte((byte) (isPopular ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public float getPositivePercentage() {
        return positivePercentage;
    }

    public void setPositivePercentage(float positivePercentage) {
        this.positivePercentage = positivePercentage;
    }

    public boolean isPositiveFeedback() {
        return isPositiveFeedback;
    }

    public void setPositiveFeedback(boolean positiveFeedback) {
        isPositiveFeedback = positiveFeedback;
    }

    public float calculatePositivePercentage() {
        return positivePercentage; // Return the stored positive percentage directly
    }

    public boolean isPopular() {
        return isPopular;
    }

    public String getActivityName() {
        return activityName != null ? activityName : feedback;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public float getNegativePercentage() {
        return negativePercentage;
    }

    public void setNegativePercentage(float negativePercentage) {
        this.negativePercentage = negativePercentage;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public float getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(float sentimentScore) {
        this.sentimentScore = sentimentScore;
    }
}
