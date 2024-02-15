package com.example.fypapp.model;
import com.google.gson.annotations.SerializedName;

public class AccessTokenResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    // Other fields and methods as needed

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
