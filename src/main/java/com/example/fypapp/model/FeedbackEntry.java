package com.example.fypapp.model;
public class FeedbackEntry {
    private String feedback;
    private float sentimentScore;

    // Default constructor
    public FeedbackEntry() {
        // Default constructor required for Firebase
    }

    // Constructor with parameters
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
}
