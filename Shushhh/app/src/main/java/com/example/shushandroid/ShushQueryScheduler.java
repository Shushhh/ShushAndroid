package com.example.shushandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
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

    public static final String SCHEDULE_TYPE = "SCHEDULE_TYPE";

    public static class Key {
        public static final String LOCATION_REPEAT = "LOCATION_REPEAT";
        public static final String LOCATION_NO_REPEAT = "LOCATION_NO_REPEAT";
        public static final String LOCATION_TIME_REPEAT = "LOCATION_TIME_REPEAT";
        public static final String LOCATION_TIME_NO_REPEAT = "LOCATION_TIME_NO_REPEAT";
        public static final String TIME_REPEAT = "TIME_REPEAT";
        public static final String TIME_NO_REPEAT = "TIME_NO_REPEAT";

    }

    private Context context;
    private double hours;
    private SharedPreferenceManager sharedPreferenceManager;
    private int id = 0;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void schedule (ArrayList<ShushObject> shushObjectArrayList) throws ParseException {
        id = 0;

        for (ShushObject shushObject: shushObjectArrayList) {
            Log.i("Alarm", shushObject.toString());
            if (shushObject.getDate().equals(ShushObject.Key.NULL)) { // only location setting with possible repeats

                if (!shushObject.getRep().isEmpty()) { // no time -> location at a certain interval

                    for (String day: getDaysFromRep(shushObject.getRep())) {

                        Calendar[] calendars = getSelectedDayCalendars(shushObject.getDate(), shushObject.getTime(), day);
                        AlarmManager fromAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(context, SilencerReciever.class);
                        intent.putExtra(SCHEDULE_TYPE, Key.LOCATION_REPEAT);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
                        fromAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars[0].getTimeInMillis(), (7 * 24 * 60 * 60 * 1000), pendingIntent); // set to silent

                        id++;

                        AlarmManager toAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, id, intent, 0);
                        toAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars[1].getTimeInMillis() + (long) (hours * 60 * 60 * 1000), (7 * 24 * 60 * 60 * 1000), pendingIntent2); // set to ring

                        Log.i("Alarm Data", calendars[0].getTime().toString() + " || " + calendars[1].getTime().toString());
                        Log.i("Alarm Schedule", "Location repeat executing...");

                        id++;

                    }


                } else {
                    //everyday just check every x minutes
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, SilencerReciever.class);
                    intent.putExtra(SCHEDULE_TYPE, Key.LOCATION_NO_REPEAT);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (long) ((hours * 60 * 60 * 1000)), pendingIntent);
                    Log.i("Alarm Schedule", "Location no repeat executing...");
                    id++;
                    // perform GeoFencing processing in SilencerReceiver
                }
            } else if (shushObject.getLocation().equals(ShushObject.Key.NULL)) {
                if (!shushObject.getRep().isEmpty()) {
                    for (String day: getDaysFromRep(shushObject.getRep())) {
                        Calendar[] calendars = getSelectedDayCalendars(shushObject.getDate(), shushObject.getTime(), day.toString());
                        AlarmManager fromAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(context, SilencerReciever.class);
                        intent.putExtra(SCHEDULE_TYPE, Key.TIME_REPEAT);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
                        fromAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars[0].getTimeInMillis(), (7 * 24 * 60 * 60 * 1000), pendingIntent); // set to silent

                        id++;

                        AlarmManager toAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, id, intent, 0);
                        toAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars[1].getTimeInMillis(), (7 * 24 * 60 * 60 * 1000), pendingIntent2); // set to ring

                        Log.i("Alarm Schedule", "Time repeat " + id);

                        id++;
                    }
                } else {
                    Calendar[] calendars = getSelectedDayCalendars(shushObject.getDate(), shushObject.getTime(), shushObject.getRep());
                    AlarmManager fromAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, SilencerReciever.class);
                    intent.putExtra(SCHEDULE_TYPE, Key.TIME_NO_REPEAT);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
                    fromAlarmManager.set(AlarmManager.RTC_WAKEUP, calendars[0].getTimeInMillis(), pendingIntent); // set to silent

                    id++;

                    AlarmManager toAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, id, intent, 0);
                    toAlarmManager.set(AlarmManager.RTC_WAKEUP, calendars[1].getTimeInMillis(), pendingIntent2); // set to ring

                    id++;
                }
            }
        }
    }

    public ArrayList<String> getDaysFromRep (String repString) {
        char[] repArray = repString.toCharArray();
        ArrayList<String> days = new ArrayList();
        for (int index = 0; index < repArray.length; index++) {
            String day = "";
            if (Character.isUpperCase(repString.toCharArray()[index])) {
                if (index == repArray.length - 1) {
                    day = "" + repArray[index];
                } else {
                    if (Character.isLowerCase(repString.toCharArray()[index + 1])) {
                        day = "" + repArray[index] + repArray[index + 1];
                    } else {
                        day = "" + repArray[index];
                    }
                }
            }
            days.add(day);
        }
        return days;
    }

    //first just time

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Calendar[] getSelectedDayCalendars (String dateString, String timeString, String dayString) throws ParseException {
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

        if (!dateString.equals(ShushObject.Key.NULL)) {
            date = dateFormat.parse(dateString);
        } else {
            date = new Date();
        }

        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int index = 0;
        for (Character character: timeString.toCharArray()) {
            if (character == '-') {
                time1String = timeString.substring(0, index - 1);
                time2String = timeString.substring(index + 2);
            }
            index = index + 1;
        }

        if (!time1String.equals(ShushObject.Key.NULL) && !time2String.equals(ShushObject.Key.NULL)) {
            time1 = timeFormat.parse(time1String);
            time2 = timeFormat.parse(time2String);
        } else {
            time1 = new Date();
            time2 = new Date();
        }

        if (time1 != null && time2 != null && date != null) {
            LocalTime localTime1 = time1.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime localTime2 = time2.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            if (localDate != null) {
                if (dayString != null && !dayString.equals("")) {
                    if (checkSimilarDays(localDate, dayString)) {
                        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    } else {
                        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(TemporalAdjusters.next(getDayOfWeek(dayString)));
                    }
                } else {
                    localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
            }

            calendarFrom.set(Calendar.YEAR, localDate.getYear());
            calendarFrom.set(Calendar.MONTH, localDate.getMonthValue() - 1);
            calendarFrom.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
            calendarFrom.set(Calendar.HOUR_OF_DAY, localTime1.getHour());
            calendarFrom.set(Calendar.MINUTE, localTime1.getMinute());

            Log.i("Alarm Calendar", "" + localDate.getDayOfMonth());

            calendarTo.set(Calendar.YEAR, localDate.getYear());
            calendarTo.set(Calendar.MONTH, localDate.getMonthValue() - 1);
            calendarTo.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
            calendarTo.set(Calendar.HOUR_OF_DAY, localTime2.getHour());
            calendarTo.set(Calendar.MINUTE, localTime2.getMinute());

        }

        return new Calendar[] {calendarFrom, calendarTo};
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkSimilarDays (LocalDate localDate, String dayString) {
        if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY && dayString.equals("Sn")) {
            return true;
        } else if (localDate.getDayOfWeek() == DayOfWeek.MONDAY && dayString.equals("M")) {
            return true;
        } else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY && dayString.equals("T")) {
            return true;
        } else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY && dayString.equals("W")) {
            return true;
        } else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY && dayString.equals("R")) {
            return true;
        } else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY && dayString.equals("F")) {
            return true;
        } else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY && dayString.equals("St")) {
            return true;
        } else return false;
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
