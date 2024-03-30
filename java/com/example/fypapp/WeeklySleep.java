package com.example.fypapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class WeeklySleep extends AppCompatActivity implements FitbitApiTask.FitbitApiListener {

    private BarChart barChart;
    private TextView dateTextView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_sleep);

        bottomNavigationView = findViewById(R.id.bottom_navigation1);
        barChart = findViewById(R.id.barChartWeekly);
        dateTextView = findViewById(R.id.dateTextViewWeekly);
        bottomNavigationView.setItemIconTintList(null);

        fetchWeeklySleepData();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_back1) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_homePage) {
                    startActivity(new Intent(WeeklySleep.this, HomePage1.class));
                    Toast.makeText(WeeklySleep.this, "Home Page", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void fetchWeeklySleepData() {
        String fitbitAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyM1JWQkMiLCJzdWIiOiJCUlM1UkQiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJyc29jIHJhY3QgcnNldCBybG9jIHJ3ZWkgcmhyIHJwcm8gcm51dCByc2xlIiwiZXhwIjoxNzM3OTE1MTExLCJpYXQiOjE3MDYzNzkxMTF9.5vCuiUuQt7QiyNmzoVIeEjLpuBrDkrg6AaTdwizqK9M";
        new FitbitApiTask(this).execute(fitbitAccessToken, "weekly");
    }

    @Override
    public void onApiSuccess(String result) {
        try {
            JSONObject responseObject = new JSONObject(result);
            JSONArray sleepDataArray = responseObject.getJSONArray("sleep");

            List<Integer> sleepScores = new ArrayList<>();
            for (int i = 0; i < sleepDataArray.length(); i++) {
                JSONObject sleepDataObject = sleepDataArray.getJSONObject(i);

                int sleepScore = sleepDataObject.getInt("efficiency");

                sleepScores.add(sleepScore);
            }

            updateChart(sleepScores);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApiError() {
    }

    private void updateChart(List<Integer> sleepScores) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < sleepScores.size(); i++) {
            entries.add(new BarEntry(i, sleepScores.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Sleep Data");
        dataSet.setColors(getSleepStageColors());
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setValueFormatter(new YAxisValueFormatter());

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        dateTextView.setText("Weekly Sleep Log");
    }

    private class YAxisValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {
        @Override
        public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
            return String.valueOf((int) value);
        }
    }

    private class XAxisValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {
        @Override
        public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
            int index = (int) value;
            switch (index) {
                case 0:
                    return "Mon";
                case 1:
                    return "Tue";
                case 2:
                    return "Wed";
                case 3:
                    return "Thu";
                case 4:
                    return "Fri";
                case 5:
                    return "Sat";
                case 6:
                    return "Sun";
                default:
                    return "";
            }
        }
    }

    private List<Integer> getSleepStageColors() {
        List<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(android.R.color.holo_red_light));
        colors.add(getResources().getColor(android.R.color.holo_blue_light));
        colors.add(getResources().getColor(android.R.color.holo_green_light));
        colors.add(getResources().getColor(android.R.color.holo_purple));
        colors.add(getResources().getColor(android.R.color.holo_orange_light));
        colors.add(getResources().getColor(android.R.color.black));
        colors.add(getResources().getColor(android.R.color.holo_green_dark));
        return colors;
    }
}




