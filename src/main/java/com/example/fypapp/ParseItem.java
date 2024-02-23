package com.example.fypapp;

public class ParseItem {
    private String title;
    private boolean isFavorited;
    private String feedback;

    public ParseItem() {
    }

    public ParseItem(String title) {
        this.title = title;
        this.isFavorited = false;
    }

    public ParseItem(String title, boolean isFavorited, String feedback) {
        this.title = title;
        this.isFavorited = isFavorited;
        this.feedback = feedback;
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
}
