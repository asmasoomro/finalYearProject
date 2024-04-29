package com.example.fypapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

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
import java.util.Calendar;
import java.util.List;

public class WeeklySleep extends AppCompatActivity implements FitbitApiTask.FitbitApiListener {

    private BarChart barChart;
    private TextView dateTextView;
    private BottomNavigationView bottomNavigationView;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_sleep);

        bottomNavigationView = findViewById(R.id.bottom_navigation1);
        barChart = findViewById(R.id.barChartWeekly);
        dateTextView = findViewById(R.id.dateTextViewWeekly);
        bottomNavigationView.setItemIconTintList(null);

        createNotificationChannel();
        scheduleWeeklyNotifications();
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Weekly Notifications";
            String description = "Receive weekly mood chart and sleep tracker notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("weekly_notifications", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weekly_notifications")
                    .setSmallIcon(R.drawable.notify)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
        } else {
            // Handle the case where permission is not granted
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, PERMISSION_REQUEST_CODE);
    }

    private void scheduleWeeklyNotifications() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, WeeklyNotificationReceiver.class);
        notificationIntent.setAction("WEEKLY_MOOD_NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to show the notification
                showNotification("Title", "Your notification message");
            } else {
                // Permission denied, show a message or handle the case accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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

        showNotification("Weekly Sleep Log", "Your weekly sleep data has been updated."); // Show notification
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
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            //Fixing the bug to get the currents days
            int index = ((int) value + (today - Calendar.SATURDAY) - 2) % 7;
            if (index < 0) index += 7;
            switch (index) {
                case 0:
                    return "Sat";
                case 1:
                    return "Sun";
                case 2:
                    return "Mon";
                case 3:
                    return "Tue";
                case 4:
                    return "Wed";
                case 5:
                    return "Thu";
                case 6:
                    return "Fri";
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




