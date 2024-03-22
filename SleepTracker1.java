package com.example.fypapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SleepTracker1 extends AppCompatActivity implements FitbitApiTask.FitbitApiListener {

    private BarChart barChart;
    private TextView dateTextView;
    private TextView efficiencyTextView;
    private TextView qualityStatusTextView;
   private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker1);

        barChart = findViewById(R.id.barChart);
        dateTextView = findViewById(R.id.dateTextView);
        efficiencyTextView = findViewById(R.id.efficiencyTextView);
        qualityStatusTextView = findViewById(R.id.qualityStatusTextView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());
        dateTextView.setText("Today's Date: " + todayDate);

        new FitbitApiTask(this).execute("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyM1JWQkMiLCJzdWIiOiJCUlM1UkQiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJyc29jIHJhY3QgcnNldCBybG9jIHJ3ZWkgcmhyIHJwcm8gcm51dCByc2xlIiwiZXhwIjoxNzM3OTE1MTExLCJpYXQiOjE3MDYzNzkxMTF9.5vCuiUuQt7QiyNmzoVIeEjLpuBrDkrg6AaTdwizqK9M");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_weekly) {
                    startActivity(new Intent(SleepTracker1.this, WeeklySleep.class));
                    Toast.makeText(SleepTracker1.this, "Weekly Sleep Log", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

        @Override
    public void onApiSuccess(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray sleepDataArray = jsonObject.getJSONArray("sleep");
            JSONObject sleepDataObject = sleepDataArray.getJSONObject(0);

            int efficiency = getSleepEfficiency(sleepDataObject);
            String sleepQualityStatus = getSleepQualityStatus(efficiency);

            efficiencyTextView.setText("Efficiency: " + efficiency);
            qualityStatusTextView.setText("Quality Status: " + sleepQualityStatus);

            JSONObject levelsObject = sleepDataObject.getJSONObject("levels");
            JSONObject summaryObject = levelsObject.getJSONObject("summary");

            int totalDuration = sleepDataObject.getInt("duration");

            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (String sleepStage : new String[]{"wake", "light", "deep", "rem"}) {
                int stageDuration = summaryObject.getJSONObject(sleepStage).getInt("minutes");
                float percentage = totalDuration > 0 ? (float) stageDuration / totalDuration * 100f : 0f;

                entries.add(new BarEntry(entries.size(), percentage));
                labels.add(getSleepStageLabel(sleepStage, stageDuration));
            }

            if (!entries.isEmpty()) {
                BarDataSet dataSet = new BarDataSet(entries, "Sleep Data");
                dataSet.setColors(getSleepStageColors());

                BarData barData = new BarData(dataSet);
                barChart.setData(barData);

                // Customize the bar chart
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new XAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);

                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.setGranularity(1f);
                leftAxis.setDrawGridLines(false);
                leftAxis.setValueFormatter(new YAxisValueFormatter());

                YAxis rightAxis = barChart.getAxisRight();
                rightAxis.setEnabled(false);

                PercentMarker percentMarker = new PercentMarker(getApplicationContext(), R.layout.custom_marker_view);
                barChart.setMarker(percentMarker);
                barChart.invalidate();
            } else {
                Log.e("SleepTracker", "No chart data available");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public int getSleepEfficiency(JSONObject sleepData) {
        try {
            if (sleepData.has("efficiency")) {
                int efficiency = sleepData.getInt("efficiency");
                Log.d("SleepTracker", "Efficiency: " + efficiency);
                return efficiency;
            }

            if (sleepData.has("sleep")) {
                JSONArray sleepArray = sleepData.getJSONArray("sleep");

                if (sleepArray.length() > 0) {
                    JSONObject firstSleepEntry = sleepArray.getJSONObject(0);

                    if (firstSleepEntry.has("efficiency")) {
                        int efficiency = firstSleepEntry.getInt("efficiency");
                        Log.d("SleepTracker", "Efficiency: " + efficiency);
                        return efficiency;
                    } else {
                        Log.e("SleepTracker", "No 'efficiency' key in sleep entry");
                    }
                } else {
                    Log.e("SleepTracker", "Empty sleep array");
                }
            } else {
                Log.e("SleepTracker", "No 'efficiency' or 'sleep' key in sleep data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("SleepTracker", "Error parsing sleep data: " + e.getMessage());
        }

        return -1;
    }

    private String getSleepQualityStatus(int efficiency) {
        if (efficiency >= 90) {
            return "Excellent";
        } else if (efficiency >= 80) {
            return "Very Good";
        } else if (efficiency >= 70) {
            return "Good";
        } else if (efficiency >= 60) {
            return "Fair";
        } else {
            return "Poor";
        }
    }

    private class PercentMarker extends MarkerView {

        private final TextView textView;
        private float percentage;

        public PercentMarker(Context context, int layoutResource) {
            super(context, layoutResource);
            textView = findViewById(R.id.tvContent);
        }
        public void setPercentage(float percentage){
            this.percentage = percentage;
            textView.setText(String.format(Locale.getDefault(), "%.2f%%", percentage));
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            setPercentage(e.getY());
            super.refreshContent(e, highlight);
        }



        @Override
        public MPPointF getOffset() {
            // Offset the marker position
            return new MPPointF(-(getWidth() / 2f), -getHeight());
        }
    }

    private String getSleepStageLabel(String sleepStage, int duration) {
        int hours = duration / 60;
        int minutes = duration % 60;
        float percentage = (float) duration / 360f * 100f; // Calculate percentage with two decimal places
        return String.format(Locale.getDefault(), "%s\n%d:%02d (%.2f%%)", sleepStage.toUpperCase(), hours, minutes, percentage);
    }


    @Override
    public void onApiError() {
        // Handle API error
    }

    private static class XAxisValueFormatter extends ValueFormatter {

        private final List<String> labels;

        XAxisValueFormatter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {
                return labels.get(index);
            }
            return "";
        }

        @Override
        public String getFormattedValue(float value) {
            // Show percentages at the top of each bar
            return String.format(Locale.getDefault(), "%.0f%%", value);
        }
    }

    private class YAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.format(Locale.getDefault(), "%.0f%%", value);
        }
    }

    private List<Integer> getSleepStageColors() {
        // Define colors for each sleep stage
        List<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(android.R.color.holo_red_light));   // Wake
        colors.add(getResources().getColor(android.R.color.holo_blue_light));  // Light
        colors.add(getResources().getColor(android.R.color.holo_green_light)); // Deep
        colors.add(getResources().getColor(android.R.color.holo_purple));      // REM
        return colors;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
      switch (item.getItemId()){
          case android.R.id.home:
              showWeeklySleepLog();
              return true;
          default:
              return super.onOptionsItemSelected(item);
      }
    }

    private void showWeeklySleepLog(){
        Intent intent = new Intent(this, WeeklySleep.class);
        startActivity(intent);
        Toast.makeText(this, "Weekly Sleep Log", Toast.LENGTH_SHORT).show();
    }
}
