package com.example.fypapp;
public class Mood {
    private float positivePercentage;
    private float negativePercentage;

    // Empty constructor for Firebase
    public Mood() {
    }

    public Mood(float positivePercentage, float negativePercentage) {
        this.positivePercentage = positivePercentage;
        this.negativePercentage = negativePercentage;
    }

    public float getPositivePercentage() {
        return positivePercentage;
    }

    public void setPositivePercentage(float positivePercentage) {
        this.positivePercentage = positivePercentage;
    }

    public float getNegativePercentage() {
        return negativePercentage;
    }

    public void setNegativePercentage(float negativePercentage) {
        this.negativePercentage = negativePercentage;
    }
}
