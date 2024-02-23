package com.example.fypapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeeklySleep extends AppCompatActivity implements FitbitApiTask.FitbitApiListener {

    private Toolbar toolbar;
    private LineChart lineChart;
    private TextView dateTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_sleep);

        toolbar = findViewById(R.id.toolbarWeekly);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.weekly);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        lineChart = findViewById(R.id.lineChartWeekly);
        dateTextView = findViewById(R.id.dateTextViewWeekly);

        fetchWeeklySleepData();
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
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < sleepScores.size(); i++) {
            entries.add(new Entry(i, sleepScores.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weekly Sleep Data");
        dataSet.setColors(getSleepStageColors());
        dataSet.setDrawValues(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setValueFormatter(new YAxisValueFormatter());

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        dateTextView.setText("Weekly Sleep Log");
    }

    private class YAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.valueOf((int) value);
        }
    }

    private class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
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



