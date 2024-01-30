package com.example.fypapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            URL url;
            if (params.length > 1 && params[1] != null && params[1].equals("weekly")) {
                // Fetch sleep logs for the past 7 days
                String sevenDaysAgoDate = getPastDate(6);
                url = new URL("https://api.fitbit.com/1.2/user/-/sleep/list.json" +
                        "?afterDate=" + sevenDaysAgoDate +
                        "&sort=desc&offset=0&limit=30");
            } else {
                // Fetch sleep log for the current day
                url = new URL("https://api.fitbit.com/1.2/user/-/sleep/date/" + currentDate + ".json");
            }

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
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

    private String getPastDate(int daysAgo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return sdf.format(calendar.getTime());
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            listener.onApiSuccess(result);
        } else {
            // Handle the error
            listener.onApiError();
        }
    }

    public interface FitbitApiListener {
        void onApiSuccess(String result);

        void onApiError();
    }
}

