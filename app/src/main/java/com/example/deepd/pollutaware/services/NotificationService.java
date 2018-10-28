package com.example.deepd.pollutaware.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.deepd.pollutaware.R;
import com.example.deepd.pollutaware.activities.MainActivity;
import com.example.deepd.pollutaware.activities.SplashActivity;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends IntentService {

    int notificationId;
    final String CHANNEL_ID = "myChannel";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        for (; ; ) {
            Toast.makeText(this, "arey bhai!", Toast.LENGTH_SHORT).show();
            sendNotification();
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        }
    }

    int getRandomAQI() {
        return (int)(Math.random() * (450 - 85) + 85);
    }

    public void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String largeText = "Rabindra Bharati University, Kolkata: " + getRandomAQI()
                + "\nVictoria Memorial, Kolkata: " + getRandomAQI()
                + "\nGhusuri, Howrah: " + getRandomAQI()
                + "\nPadma Pukur, Howrah: " + getRandomAQI();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pin)
                .setContentTitle("PollutAware AQI Alert")
                .setContentText("Click to view AQI levels near you, expand for details")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(largeText))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId++, mBuilder.build());
    }
}