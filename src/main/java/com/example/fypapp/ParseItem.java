package com.example.fypapp;

import com.example.fypapp.model.FeedbackEntry;

public class ParseItem {
    private String title;
    private boolean isFavorited;
    private String feedback;
    private float positivePercentage;
    private boolean isPopular;
    private FeedbackEntry feedbackEntry;

    public ParseItem() {
    }

    public ParseItem(String title, float positivePercentage) {
        this.title = title;
        this.isFavorited = false;
        this.positivePercentage = positivePercentage;
    }

    public ParseItem(String title, boolean isFavorited) {
        this.title = title;
        this.isFavorited = isFavorited;
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
        this.isPopular = isPopular;
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
        return getPositivePercentage() > 70;
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
}
