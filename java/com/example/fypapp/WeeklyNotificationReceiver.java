package com.example.fypapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class WeeklyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("WEEKLY_MOOD_NOTIFICATION")) {
            showNotification(context, "Weekly Mood Chart Available", "Your weekly mood chart is ready to view.");
        } else if (intent.getAction() != null && intent.getAction().equals("WEEKLY_SLEEP_NOTIFICATION")) {
            showNotification(context, "Weekly Sleep Chart Available", "Your weekly sleep chart is ready to view.");
        }
    }

    private void showNotification(Context context, String title, String message) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "weekly_notifications")
                    .setSmallIcon(R.drawable.notify)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
