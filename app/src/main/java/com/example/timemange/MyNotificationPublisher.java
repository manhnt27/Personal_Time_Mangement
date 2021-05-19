package com.example.timemange;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyNotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";
    private static final String CHANNEL_ID = "Channel_Id";
    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.i("Notify Receive",intent.getStringExtra("Activity_Name"));
        String name = intent.getStringExtra("Activity_Name");
        String type = intent.getStringExtra("Activity_Type");

        String notifyText = (type.equals("Event") ? name : ("It's time to take " + name));
        String notifyTitle = (type.equals("Event") ? "Upcoming Event" : ("New Habit"));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(notifyTitle)
                .setContentText(notifyText)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        /*Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);*/
        notificationManager.notify(String.valueOf(System.currentTimeMillis()), 0,builder.build());
    }


}