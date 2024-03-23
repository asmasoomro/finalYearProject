package com.example.fypapp.network;
import com.example.fypapp.model.AccessTokenResponse;
import com.example.fypapp.model.SleepResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.Query;

public interface FitbitApi {
    @GET("/1.2/user/{userId}/sleep/date/{date}.json")
    Call<SleepResponse> getSleepData(
            @Path("userId") String userId,
            @Path("date") String date,
            @Header("Authorization") String accessToken
    );

    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<AccessTokenResponse> getAccessToken(
            @Field("code") String authorizationCode,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType
    );
}

