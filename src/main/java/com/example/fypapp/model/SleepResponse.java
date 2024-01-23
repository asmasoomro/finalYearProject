package com.example.fypapp.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SleepResponse {

    @SerializedName("sleep")
    private List<SleepData> sleepDataList;

    // Other fields and methods as needed

    public List<SleepData> getSleepDataList() {
        return sleepDataList;
    }
}

