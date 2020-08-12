package com.example.shushandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

public class SilencerReciever extends BroadcastReceiver {

    private AudioManager audioManager;
    private SharedPreferenceManager sharedPreferenceManager;
    private double hours;
    private Integer toggleState;
    public static int index = 0;

    public static long interval = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferenceManager = new SharedPreferenceManager(context);
        hours = sharedPreferenceManager.retrieveLocationInterval();
        toggleState = sharedPreferenceManager.retrieveToggleState();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        interval = (long) (hours * 60 * 60 * 1000);

        /*
         * If the user mentions a location, perform GeoFencing processing here *
         */
        if (intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE) != null) {
            String scheduleType = Objects.requireNonNull(intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE));
            String toggleKey = intent.getStringExtra(ShushQueryScheduler.TOGGLE_KEY);
            if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_NO_REPEAT)) {
                if (GeofenceBroadcastReceiver.isEntered()) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    Log.i("Alarm Toggle", "Location Repeat - SILENT");
                } else {
                    audioManager.setRingerMode(toggleState);
                    Log.i("Alarm Toggle", "Location Repeat - RING");
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_REPEAT)) {
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
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - RING");
                    audioManager.setRingerMode(toggleState);
                }
            }
        }
    }

}
