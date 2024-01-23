package com.example.fypapp;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FitbitApiTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "FitbitApiTask";
    private FitbitApiListener listener;

    public FitbitApiTask(FitbitApiListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String accessToken = params[0];
        String currentDate = getCurrentDate();

        try {
            URL url = new URL("https://api.fitbit.com/1.2/user/-/sleep/date/" + currentDate + ".json");
          //  URL url = new URL("https://api.fitbit.com/1.2/user/-/sleep/date/2023-12-01.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set up the request headers
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // Get the response code
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                // Handle error response
                Log.e(TAG, "Error response code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making API request", e);
            return null;
        }
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            // Parse the JSON response using a JSON parsing library (e.g., Gson)
            // Update UI or store data as needed
            listener.onApiSuccess(result);
        } else {
            // Handle the error
            listener.onApiError();
        }
    }

    // Interface to communicate API results to the caller
    public interface FitbitApiListener {
        void onApiSuccess(String result);

        void onApiError();
    }

}
