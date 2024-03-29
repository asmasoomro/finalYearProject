package com.example.fypapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.Firebase;
import android.graphics.Color;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeeklyMood extends AppCompatActivity {
private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_mood);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //if user is logged in
        if (currentUser != null) {
            // Get the user ID
            String userId = currentUser.getUid();
            fetchWeeklyMoodsFromDatabase(userId);
        } else {
           //if user not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchWeeklyMoodsFromDatabase(String userId) {
        DatabaseReference userMoodsRef = FirebaseDatabase.getInstance().getReference("user_moods").child(userId).child("moods");

        userMoodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> positiveEntries = new ArrayList<>();
                List<Entry> negativeEntries = new ArrayList<>();

                long currentTime = System.currentTimeMillis();

                for (DataSnapshot moodSnapshot : snapshot.getChildren()) {
                    Mood mood = moodSnapshot.getValue(Mood.class);

                    if (mood != null && moodSnapshot.getKey() != null) {
                        long moodTimestamp = Long.parseLong(moodSnapshot.getKey());
                        float positivePercentage = mood.getPositivePercentage();
                        float negativePercentage = mood.getNegativePercentage();

                        if (currentTime - moodTimestamp < 7 * 24 * 60 * 60 * 1000) {
                            // Create separate entries for positive and negative percentages
                            positiveEntries.add(new Entry(positiveEntries.size() + 1, positivePercentage));
                            negativeEntries.add(new Entry(negativeEntries.size() + 1, negativePercentage));
                        }
                    }
                }

                showWeeklyMoodChart(positiveEntries, negativeEntries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Toast.makeText(WeeklyMood.this, "Failed to fetch data from database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWeeklyMoodChart(List<Entry> positiveEntries, List<Entry> negativeEntries) {
        LineChart lineChart = findViewById(R.id.lineChart);

        lineChart.clear();
        LineData lineData = new LineData();

        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7); // Set the number of labels to display

        xAxis.setValueFormatter(new DayAxisValueFormatter());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setValueFormatter(new PercentAxisValueFormatter());

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Create LineDataSet for positive entries
        LineDataSet positiveDataSet = new LineDataSet(positiveEntries, "Positive Mood");
        positiveDataSet.setColor(getResources().getColor(android.R.color.holo_green_light));
        positiveDataSet.setLineWidth(2f);
        positiveDataSet.setCircleColor(getResources().getColor(android.R.color.holo_green_light));
        positiveDataSet.setCircleRadius(4f);
        positiveDataSet.setDrawCircleHole(false);

        // Create LineDataSet for negative entries
        LineDataSet negativeDataSet = new LineDataSet(negativeEntries, "Negative Mood");
        negativeDataSet.setColor(getResources().getColor(android.R.color.holo_red_light));
        negativeDataSet.setLineWidth(2f);
        negativeDataSet.setCircleColor(getResources().getColor(android.R.color.holo_red_light));
        negativeDataSet.setCircleRadius(4f);
        negativeDataSet.setDrawCircleHole(false);

        // Add LineDataSets to LineData
        lineData.addDataSet(positiveDataSet);
        lineData.addDataSet(negativeDataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();

        String message = "Weekly Mood Chart displayed";
        textView = findViewById(R.id.textViewMessage);
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public class PercentAxisValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            // Format the value as a percentage
            return String.format(Locale.getDefault(), "%.0f%%", value);
        }
    }

    public class DayAxisValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int dayOfWeek = (int) value;
            switch (dayOfWeek) {
                case 0:
                    return "Sun";
                case 1:
                    return "Mon";
                case 2:
                    return "Tue";
                case 3:
                    return "Wed";
                case 4:
                    return "Thurs";
                case 5:
                    return "Fri";
                case 6:
                    return "Sat";
                default:
                    return "";
            }
        }
    }
}

