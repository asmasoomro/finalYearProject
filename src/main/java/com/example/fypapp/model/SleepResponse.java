package com.example.fypapp.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SleepResponse {

    @SerializedName("sleep")
    private List<SleepData> sleepDataList;

    public List<SleepData> getSleepDataList() {
        return sleepDataList;
    }
}

