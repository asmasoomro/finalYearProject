package com.example.fypapp;

public class feedback1 {
    private String feelingsBefore;
    private String feelingsDuring;
    private String feelingsAfter;
    public feedback1(){

    }

    public feedback1(String feelingsBefore, String feelingsDuring, String feelingsAfter) {
        this.feelingsBefore = feelingsBefore;
        this.feelingsDuring = feelingsDuring;
        this.feelingsAfter = feelingsAfter;
    }

    public String getFeelingsBefore() {
        return feelingsBefore;
    }

    public void setFeelingsBefore(String feelingsBefore) {
        this.feelingsBefore = feelingsBefore;
    }

    public String getFeelingsDuring() {
        return feelingsDuring;
    }

    public void setFeelingsDuring(String feelingsDuring) {
        this.feelingsDuring = feelingsDuring;
    }

    public String getFeelingsAfter() {
        return feelingsAfter;
    }

    public void setFeelingsAfter(String feelingsAfter) {
        this.feelingsAfter = feelingsAfter;
    }
}
