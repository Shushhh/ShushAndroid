package com.example.shushandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.sql.SQLOutput;

public class ForegroundServiceManager extends Service {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this, "service")
                .setContentTitle("Service")
                .setContentText("Test")
                .setSmallIcon(R.drawable.ic_shush_notif_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        try {
            ShushQueryScheduler.schedule(new DatabaseManager(getApplicationContext()).retrieveWithCursor(), getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
