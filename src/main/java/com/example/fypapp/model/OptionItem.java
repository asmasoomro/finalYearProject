package com.example.fypapp.model;
public class OptionItem {
    private int imageResource;
    private String optionName;

    public OptionItem(int imageResource, String optionName) {
        this.imageResource = imageResource;
        this.optionName = optionName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getOptionName() {
        return optionName;
    }
}
