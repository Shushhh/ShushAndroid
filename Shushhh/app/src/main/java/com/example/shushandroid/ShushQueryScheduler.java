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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
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
    time, repeat -> only that time repeated on specified days
        alarm manager for from time to activate silent with repeat for that day
        alarm manager for to time to deactivate silent with repeat for that day
            Repeat this for all days
    location -> everyday
    time -> only that time
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
                    Intent intent = new Intent(context, SilencerReciever.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (long) ((hours * 60 * 60 * 1000)), pendingIntent);
                    // perform GeoFencing processing in SilencerReceiver
                }
            } else if (shushObject.getLocation().equals(ShushObject.Key.NULL)) { // only time setting with possible repeats

            }
        }
    }

    //first just time

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Calendar[] getCurrentDayMillis (@Nullable String dateString, @Nullable String timeString, @Nullable String dayString) throws ParseException {
        Calendar calendarFrom = Calendar.getInstance();
        Calendar calendarTo = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(ShushDialog.DateFormatStringKey.dateFormatString, Locale.getDefault()); // check for other countries
        SimpleDateFormat timeFormat = new SimpleDateFormat(ShushDialog.DateFormatStringKey.timeFormatString, Locale.getDefault());
        String time1String = null;
        String time2String = null;
        Date date = null;
        Date time1 = null;
        Date time2 = null;
        LocalDate localDate = null;

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
            LocalTime localTime1 = time1.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime localTime2 = time2.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            if (dayString != null && !dayString.equals(""))
                localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(TemporalAdjusters.next(getDayOfWeek(dayString)));
            else
                localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            calendarFrom.set(Calendar.YEAR, localDate.getYear());
            calendarFrom.set(Calendar.MONTH, localDate.getMonthValue() - 1);
            calendarFrom.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth() - 1);
            calendarFrom.set(Calendar.HOUR, localTime1.getHour());
            calendarFrom.set(Calendar.MINUTE, localTime1.getMinute());

            calendarTo.set(Calendar.YEAR, localDate.getYear());
            calendarTo.set(Calendar.MONTH, localDate.getMonthValue() - 1);
            calendarTo.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth() - 1);
            calendarTo.set(Calendar.HOUR, localTime2.getHour());
            calendarTo.set(Calendar.MINUTE, localTime2.getMinute());

        }
        return new Calendar[] {calendarFrom, calendarTo};
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private DayOfWeek getDayOfWeek (String dayString) {
        if (dayString != null) {
            switch (dayString) {
                case "Sn":
                    return DayOfWeek.SUNDAY;
                case "M":
                    return DayOfWeek.MONDAY;
                case "T":
                    return DayOfWeek.TUESDAY;
                case "W":
                    return DayOfWeek.WEDNESDAY;
                case "R":
                    return DayOfWeek.THURSDAY;
                case "F":
                    return DayOfWeek.FRIDAY;
                case "St":
                    return DayOfWeek.SATURDAY;
                default:
                    return null;
            }
        } else return null;
    }

}
