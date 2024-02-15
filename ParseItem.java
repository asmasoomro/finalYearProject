package com.example.fypapp;

public class ParseItem {
    private String title;
    private boolean isFavorited;

    public ParseItem() {
    }

    public ParseItem(String title) {
        this.title = title;
        this.isFavorited = false;
    }

    public ParseItem(String title, boolean isFavorited) {
        this.title = title;
        this.isFavorited = isFavorited;
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
}
