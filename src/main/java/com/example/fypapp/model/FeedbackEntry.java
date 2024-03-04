package com.example.fypapp.model;
public class FeedbackEntry {
    private String feedback;
    private float sentimentScore;
    private float positivePercentage;
    private boolean isPositiveFeedback;

    // Default constructor
    public FeedbackEntry() {
        // Default constructor required for Firebase
    }
    public FeedbackEntry(String feedback, float sentimentScore, float positivePercentage, boolean isPositiveFeedback) {
        this.feedback = feedback;
        this.sentimentScore = sentimentScore;
        this.positivePercentage = positivePercentage;
        this.isPositiveFeedback = isPositiveFeedback;
    }

   // public FeedbackEntry(String feedback, float sentimentScore, float isPositiveFeedback) {
     //   this.feedback = feedback;
       // this.sentimentScore = sentimentScore;
        //this.isPositiveFeedback = isPositiveFeedback;
    //}

    public FeedbackEntry(String feedback, float sentimentScore) {
        this.feedback = feedback;
        this.sentimentScore = sentimentScore;
    }

    // Getter and setter methods
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
        return positivePercentage > 70;
    }
}
