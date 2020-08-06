package com.example.shushandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ShushQueryScheduler {

    private Context context;
    private double hours;
    private SharedPreferenceManager sharedPreferenceManager;

    public ShushQueryScheduler (Context context) {
        this.context = context;
        this.sharedPreferenceManager = new SharedPreferenceManager(context);
        this.hours = sharedPreferenceManager.retrieveLocationInterval();
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
                    Log.i("Alarm", "Active " + hours);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, BroadcastReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (long) ((hours * 60 * 60 * 1000)), (long) ((hours * 60 * 60 * 1000)), pendingIntent);
                }
            } else if (shushObject.getLocation().equals(ShushObject.Key.NULL)) { // only time setting with possible repeats

            }
        }
    }

    //first just time

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Calendar getCurrentDayMillis (@Nullable String dateString, @Nullable String timeString, @Nullable String dayString) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(ShushDialog.DateFormatStringKey.dateFormatString, Locale.getDefault()); // check for other countries
        SimpleDateFormat timeFormat = new SimpleDateFormat(ShushDialog.DateFormatStringKey.timeFormatString, Locale.getDefault());
        String time1String = null;
        String time2String = null;
        Date date = null;
        Date time1 = null;
        Date time2 = null;

        if (dateString != null)
            date = dateFormat.parse(dateString);

        int index = 0;
        for (Character character: timeString.toCharArray()) {
            if (character == '-') {
                time1String = timeString.substring(0, index - 1);
                time2String = timeString.substring(index + 2);
            }
            index = index + 1;
        }

        if (time1String != null)
            time1 = timeFormat.parse(time1String);
        if (time2String != null)
            time2 = timeFormat.parse(time2String);

        if (time1 != null && time2 != null && date != null) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localTime1 = time1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localTime2 = time2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            calendar.set(Calendar.YEAR, localDate.getYear());
            calendar.set(Calendar.MONTH, localDate.getMonthValue());
            calendar.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
            calendar.set(Calendar.HOUR, localTime1.getYear());

        }
        return null;
    }

}
