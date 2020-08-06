package com.example.shushandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;

public class ShushQueryScheduler {

    private Context context;
    private double hours;

    public ShushQueryScheduler (Context context) {
        this.context = context;
        this.hours = SharedPreferenceManager.retrieveLocationInterval();
    }

    /*
    location, time, repeat
    location, time -> that particular day
    location, repeat
    time, repeat
    location -> everyday
    time -> only that day
     */

    public void schedule (ArrayList<ShushObject> shushObjectArrayList) {
        for (ShushObject shushObject: shushObjectArrayList) {
            if (shushObject.getDate().equals(ShushObject.Key.NULL)) { // only location setting with possible repeats
                if (!shushObject.getRep().isEmpty()) { // no time -> location at a certain interval
                    for (Character day: shushObject.getRep().toCharArray()) {

                    }
                } else {
                    //everyday just check every x minutes
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, BroadcastReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (long) ((hours * 60 * 60 * 1000)), (long) ((hours * 60 * 60 * 1000)), pendingIntent);
                }
            } else if (shushObject.getDate().equals(ShushObject.Key.NULL)) { // only time setting with possible repeats

            }
        }
    }

//    private Calendar getCurrentDayMillis () {
//
//    }

}
