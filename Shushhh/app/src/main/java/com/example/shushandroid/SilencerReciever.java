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
            if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_REPEAT)) {

                Log.i("Alarm", "LOcation repeat");

                int x = intent.getIntExtra("DayInt", 0);
                String day = intent.getStringExtra("DayString");

                Calendar calendarFrom = Calendar.getInstance();
                Calendar calendarTo = Calendar.getInstance();
                Date date = new Date();
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime localTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd");

                if (Integer.parseInt(dateFormat.format(date)) == x) {
                    SilencerReciever.interval = (long) (hours/10 * 60 * 60 * 1000);
                    try {
                        ShushQueryScheduler.schedule(new DatabaseManager(context).retrieveWithCursor(), context);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    LocalDate nextLocalDate = localDate.with(TemporalAdjusters.next(ShushQueryScheduler.getDayOfWeek(day)));

                    calendarFrom.set(Calendar.YEAR, localDate.getYear());
                    calendarFrom.set(Calendar.MONTH, localDate.getMonthValue() - 1);
                    calendarFrom.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
                    calendarFrom.set(Calendar.HOUR_OF_DAY, localTime.getHour());
                    calendarFrom.set(Calendar.MINUTE, localTime.getMinute());

                    calendarTo.set(Calendar.YEAR, nextLocalDate.getYear());
                    calendarTo.set(Calendar.MONTH, nextLocalDate.getMonthValue() - 1);
                    calendarTo.set(Calendar.DAY_OF_MONTH, nextLocalDate.getDayOfMonth());

                    SilencerReciever.interval = calendarTo.getTimeInMillis() - calendarFrom.getTimeInMillis();

                    System.out.println(SilencerReciever.interval);

                    try {
                        ShushQueryScheduler.schedule(new DatabaseManager(context).retrieveWithCursor(), context);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                // check inside location receiver for geofences

            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_NO_REPEAT)) {
                // check toggle with geofences and then ring or silent based on location (if within vicinity -> silent and if not -> ring)
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time Repeat - RING");
                    audioManager.setRingerMode(toggleState);
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_NO_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time No Repeat - RING");
                    audioManager.setRingerMode(toggleState);
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time No Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

}
