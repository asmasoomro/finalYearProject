package com.example.fypapp;
import com.example.fypapp.Feedback;
public class CustomActivity {
    private String name;
    private String description;
    private boolean isPopular;

    public CustomActivity() {
    }

    // Constructor with parameters
    public CustomActivity(String name, String description, boolean isPopular) {
        this.name = name;
        this.description = description;
        this.isPopular = isPopular;
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPopular() {
        return isPopular;
    }
    public void setPopular(boolean popular) {
        isPopular = popular;
    }

}
