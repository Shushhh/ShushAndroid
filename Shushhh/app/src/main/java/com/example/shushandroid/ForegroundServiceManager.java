package com.example.shushandroid;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.sql.SQLOutput;

public class ForegroundServiceManager extends Service {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent deleteIntent = new Intent (this, ForegroundServiceManager.class);
        deleteIntent.setAction("DELETE");
        PendingIntent pendingIntent1 = PendingIntent.getService(this, 1, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(this, "service")
                .setContentTitle("Shush Background Service")
                .setSmallIcon(R.drawable.ic_shush_notif_icon)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.BigTextStyle().bigText("Shush checks and updates your ringer settings according to the time and location settings. Keep the service running to ensure best results."))
                .addAction(R.drawable.ic_cross, "Cancel Service", pendingIntent1).build();

        startForeground(1, notification);


        if (intent.getAction() != null) {
            if (intent.getAction().equals("DELETE")) {
                stopForeground(true);
                stopSelf();
            }
        }

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
