package com.example.fypapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fypapp.Adapter.SleepDataAdapter;
import com.example.fypapp.model.AccessTokenResponse;
import com.example.fypapp.model.SleepData;
import com.example.fypapp.model.SleepResponse;
import com.example.fypapp.network.FitbitApi;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SleepTracker extends AppCompatActivity {

    private static final int FITBIT_AUTH_REQUEST_CODE = 101;
    private static final String CLIENT_ID = "23RN96";
    private static final String CLIENT_SECRET = "faa17f0cd7b0625a70c7ca145666be7f";
    private static final String REDIRECT_URI = "http://localhost";
    private static final String FITBIT_API_BASE_URL = "https://api.fitbit.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);

        if (!isFitbitAuthenticated()) {
            startFitbitAuthentication();
        } else {
            fetchDataFromFitbit();
        }
    }

    private boolean isFitbitAuthenticated() {
        SharedPreferences sharedPreferences = getSharedPreferences("fitbit_auth", Context.MODE_PRIVATE);

        String accessToken = sharedPreferences.getString("fitbit_access_token", null);

        return accessToken != null;
    }


    private void startFitbitAuthentication() {
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.setVisibility(View.VISIBLE);

        String fitbitAuthUrl = buildFitbitAuthorizationUrl();
        webView.loadUrl(fitbitAuthUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if(url.startsWith(REDIRECT_URI)){
                    handleFitbitAuthorizationCallback(url);
                    return true;
                }
                Log.d("WebView", "Loading URL: " + request.getUrl().toString());

                if (request.getUrl().toString().startsWith(REDIRECT_URI)) {
                    // Handle Fitbit authorization callback
                    handleFitbitAuthorizationCallback(request.getUrl().toString());
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WebView", "Page started loading: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "Page finished loading: " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("WebView", "Error loading page: " + error.getDescription());
            }
        });
    }

    private String buildFitbitAuthorizationUrl() {
        String encodedRedirectUri = Uri.encode(REDIRECT_URI);
        return "https://www.fitbit.com/oauth2/authorize" +
                "?client_id=" + CLIENT_ID +
                "&redirect_uri=" + encodedRedirectUri +
                "&response_type=code" +
                "&scope=sleep";
    }

    private void handleFitbitAuthorizationCallback(String callbackUrl) {
        String authorizationCode = extractAuthorizationCode(callbackUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FITBIT_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FitbitApi fitbitApi = retrofit.create(FitbitApi.class);
        Call<AccessTokenResponse> accessTokenCall = fitbitApi.getAccessToken(
                authorizationCode,
                CLIENT_ID,
                CLIENT_SECRET,
                REDIRECT_URI,
                "authorization_code"
        );

        accessTokenCall.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccessTokenResponse> call, @NonNull Response<AccessTokenResponse> response) {
                if (response.isSuccessful()) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    String accessToken = accessTokenResponse.getAccessToken();

                    saveAccessTokenToStorage(accessToken);

                    fetchDataFromFitbit();
                } else {
                    // Handle token request error
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessTokenResponse> call, @NonNull Throwable t) {
                // Handle network error
            }
        });
    }

    private String extractAuthorizationCode(String callbackUrl) {
        try {
            Uri uri = Uri.parse(callbackUrl);
            String authorizationCode = uri.getQueryParameter("code");

            if (authorizationCode != null && !authorizationCode.isEmpty()) {
                return authorizationCode;
            } else {
                Log.e("Authorization Code", "Authorization code not found in the callback URL");
                return null;
            }
        } catch (Exception e) {
            Log.e("URL Parsing Exception", "Error parsing callback URL: " + e.getMessage());
            return null;
        }
    }


    private void saveAccessTokenToStorage(String accessToken) {
        SharedPreferences preferences = getSharedPreferences("FitbitPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();
    }


    private void fetchDataFromFitbit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FITBIT_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FitbitApi fitbitApi = retrofit.create(FitbitApi.class);
        String accessToken = getAccessToken();
        String authorizationHeader = "Bearer " + accessToken;

        String userId = "BRS5RD";
        String currentDate = getCurrentDate();
       // String date = "2024-03-13";
        // Use the obtained user ID in the API call
        Call<SleepResponse> call = fitbitApi.getSleepData(userId, currentDate, authorizationHeader);

        call.enqueue(new Callback<SleepResponse>() {
            @Override
            public void onResponse(Call<SleepResponse> call, Response<SleepResponse> response) {
                if (response.isSuccessful()) {
                    SleepResponse sleepData = response.body();
                    TextView sleepDataTextView = findViewById(R.id.sleepDataTextView);

                    Log.d("SleepTracker", "Sleep data size: " + sleepData.getSleepDataList().size());

                    if (sleepData != null && sleepData.getSleepDataList() != null && !sleepData.getSleepDataList().isEmpty()) {
                        SleepData firstSleepEntry = sleepData.getSleepDataList().get(0);
                        String displayText = "Date: " + firstSleepEntry.getDateOfSleep() +
                                "\nDuration: " + firstSleepEntry.getDuration() + " minutes";

                        sleepDataTextView.setText(displayText);

                        setupRecyclerView(sleepData.getSleepDataList());
                    } else {
                        sleepDataTextView.setText("No sleep data available");
                    }
                } else {
                    // Handle API error
                }
            }

            @Override
            public void onFailure(Call<SleepResponse> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private String getUserId() {
        SharedPreferences preferences = getSharedPreferences("FitbitPreferences", MODE_PRIVATE);
        return preferences.getString("userId", null);
    }


    private String getAccessToken() {
        SharedPreferences preferences = getSharedPreferences("FitbitPreferences", MODE_PRIVATE);
        return preferences.getString("accessToken", null);
    }

    private void setupRecyclerView(List<SleepData> sleepDataList) {
        RecyclerView sleepRecyclerView = findViewById(R.id.sleepRecyclerView);
        SleepDataAdapter sleepDataAdapter = new SleepDataAdapter(SleepTracker.this, sleepDataList);
        sleepRecyclerView.setLayoutManager(new LinearLayoutManager(SleepTracker.this));
        sleepRecyclerView.setAdapter(sleepDataAdapter);
        sleepDataAdapter.notifyDataSetChanged();
    }
}
