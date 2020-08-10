package com.example.shushandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Objects;

public class SilencerReciever extends BroadcastReceiver {

    private AudioManager audioManager;
    private SharedPreferenceManager sharedPreferenceManager;
    private double hours;
    private Integer toggleState;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferenceManager = new SharedPreferenceManager(context);
        hours = sharedPreferenceManager.retrieveLocationInterval();
        toggleState = sharedPreferenceManager.retrieveToggleState();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        /*
         * If the user mentions a location, perform GeoFencing processing here *
         */
        if (intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE) != null) {
            String scheduleType = Objects.requireNonNull(intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE));
            String toggleKey = intent.getStringExtra(ShushQueryScheduler.TOGGLE_KEY);
            if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_REPEAT)) {

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent locationIntent = new Intent (context, LocationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, locationIntent, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (long)(hours * 60 * 60 * 1000), pendingIntent);
                // check inside location receiver for geofences

            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_NO_REPEAT)) {
                // check toggle with geofences and then ring or silent based on location (if within vicinity -> silent and if not -> ring)
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_REPEAT)) {
                Log.i("Alarm", "Time Repeat");
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time Repeat - RING");
                    audioManager.setRingerMode(toggleState);
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_NO_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time No Repeat - RING");
                    audioManager.setRingerMode(toggleState);
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time No Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_TIME_NO_REPEAT)) {
                Log.i("Alarm Message", "Location Time No Repeat");
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - RING");
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - SILENT");
                }
                // check toggle with geofences and then ring or silent based on location (if within vicinity -> silent and if not -> ring)
            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_TIME_REPEAT)) {

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent locationIntent = new Intent (context, LocationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, locationIntent, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (long) (hours * 60 * 60 * 1000), pendingIntent);
            }
        }
    }

    public void setRinger() { // will do after implementing permissions

    }

}
