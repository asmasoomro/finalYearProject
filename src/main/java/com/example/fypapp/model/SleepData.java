package com.example.fypapp.model;
import com.google.gson.annotations.SerializedName;

public class SleepData {

    @SerializedName("dateOfSleep")
    private String dateOfSleep;

    @SerializedName("duration")
    private int duration;

    // Other fields and methods as needed

    public String getDateOfSleep() {
        return dateOfSleep;
    }

    public int getDuration() {
        return duration;
    }
}