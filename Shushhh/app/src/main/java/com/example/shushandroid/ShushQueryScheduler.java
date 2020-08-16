package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
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
import java.util.List;
import java.util.Locale;

public class ShushQueryScheduler {

    public static final String SCHEDULE_TYPE = "SCHEDULE_TYPE";
    public static final String TOGGLE_KEY = "TOGGLE_KEY";

    public static boolean isCurrentTime = false;

    public static class Key {
        public static final String LOCATION_NO_REPEAT = "LOCATION_NO_REPEAT";
        public static final String LOCATION_TIME_NO_REPEAT = "LOCATION_TIME_NO_REPEAT";
        public static final String TIME_REPEAT = "TIME_REPEAT";
        public static final String TIME_NO_REPEAT = "TIME_NO_REPEAT";

        public static final String SILENT = "SILENT";
        public static final String RING = "RING";
    }

    private static double hours;
    private static SharedPreferenceManager sharedPreferenceManager;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void schedule (ArrayList<ShushObject> shushObjectArrayList, Context context) throws ParseException {

        int id = 0;

        sharedPreferenceManager = new SharedPreferenceManager(context);
        hours = sharedPreferenceManager.retrieveLocationInterval();

        Log.i("Test", "test");

        SilencerReciever.total = numberLocationShushObjects(shushObjectArrayList);
        SilencerReciever.index = 0;
        SilencerReciever.latitudes.clear();
        SilencerReciever.longitudes.clear();
        SilencerReciever.radii.clear();

        Log.i("Test", "test1");

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        for (int i = 0; i < shushObjectArrayList.size(); i++) {
            Log.i("ShushObject", shushObjectArrayList.get(i).toString());
            ShushObject shushObject = shushObjectArrayList.get(i);
            Log.i("shush", shushObject.toString());

            if (shushObject.getDate().equals(ShushObject.Key.NULL)) {

                SilencerReciever.locationStatuses.clear();
                Log.i("call","call");
                AlarmManager fromAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context.getApplicationContext(), SilencerReciever.class);
                intent.putExtra(SCHEDULE_TYPE, Key.LOCATION_NO_REPEAT);
                SilencerReciever.latitudes.add(shushObject.getLatLng().latitude);
                SilencerReciever.longitudes.add(shushObject.getLatLng().longitude);
                SilencerReciever.radii.add(Double.parseDouble(shushObject.getRadius().substring(0, shushObject.getRadius().length() - 1)));
                Log.i("Intent info", shushObject.getLatLng().latitude + " | " + shushObject.getLatLng().longitude + " | " + Double.parseDouble(shushObject.getRadius().substring(0, shushObject.getRadius().length() - 1)));
                Log.i("call","call1");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
                fromAlarmManager.cancel(pendingIntent);
                fromAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (long) (hours * 60 * 60 * 1000) / 5, pendingIntent);
                Log.i("call","call2");// set to silent
                id ++;

            } else if (shushObject.getLocation().equals(ShushObject.Key.NULL) || !shushObject.getLocation().equals(ShushObject.Key.NULL)) {
                /***************** DONE *******************/
                //LOCATION AND TIME REPEAT OR TIME REPEAT
                if (!shushObject.getRep().isEmpty()) {
                    for (String day: getDaysFromRep(shushObject.getRep())) {
                        Calendar[] calendars = getSelectedDayCalendars(shushObject.getDate(), shushObject.getTime(), day);
                        AlarmManager fromAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(context, SilencerReciever.class);
                        intent.putExtra(SCHEDULE_TYPE, Key.TIME_REPEAT);
                        intent.putExtra(TOGGLE_KEY, Key.SILENT);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
                        fromAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars[0].getTimeInMillis(), (7 * 24 * 60 * 60 * 1000), pendingIntent); // set to silent

                        id++;

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Intent intent2 = new Intent(context, SilencerReciever.class);
                        intent2.putExtra(SCHEDULE_TYPE, Key.TIME_REPEAT);
                        intent2.putExtra(TOGGLE_KEY, Key.RING);
                        AlarmManager toAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, id, intent2, 0);
                        toAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars[1].getTimeInMillis(), (7 * 24 * 60 * 60 * 1000), pendingIntent2); // set to ring

                        id++;
                    }
                } else {
                    /***************** DONE *******************/
                    //TIME NO REPEAT
                    Calendar[] calendars = getSelectedDayCalendars(shushObject.getDate(), shushObject.getTime(), shushObject.getRep());
                    AlarmManager fromAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, SilencerReciever.class);
                    if (shushObject.getLocation().equals(ShushObject.Key.NULL))
                        intent.putExtra(SCHEDULE_TYPE, Key.TIME_NO_REPEAT);
                    //LOCATION TIME NO REPEAT
                    else
                        intent.putExtra(SCHEDULE_TYPE, Key.LOCATION_TIME_NO_REPEAT);
                    intent.putExtra(TOGGLE_KEY, Key.SILENT);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(shushObject.getId()), intent, 0);
                    fromAlarmManager.set(AlarmManager.RTC_WAKEUP, calendars[0].getTimeInMillis(), pendingIntent); // set to silent

                    id++;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    AlarmManager toAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent2 = new Intent(context, SilencerReciever.class);
                    if (shushObject.getLocation().equals(ShushObject.Key.NULL))
                        intent2.putExtra(SCHEDULE_TYPE, Key.TIME_NO_REPEAT);
                    else
                        intent2.putExtra(SCHEDULE_TYPE, Key.LOCATION_TIME_NO_REPEAT);
                    intent2.putExtra(TOGGLE_KEY, Key.RING);
                    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, Integer.parseInt(shushObject.getId()) + 1, intent2, 0);
                    toAlarmManager.set(AlarmManager.RTC_WAKEUP, calendars[1].getTimeInMillis(), pendingIntent2); // set to ring

                    System.out.println(calendars[0] + "|" + calendars[1]);

                    id++;
                }
            }
        }
    }

    public static ArrayList<String> getDaysFromRep (String repString) {
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
                days.add(day);
            }
        }
        return days;
    }

    //first just time

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Calendar[] getSelectedDayCalendars (String dateString, String timeString, String dayString) throws ParseException {
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
    private static boolean checkSimilarDays (LocalDate localDate, String dayString) {
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
    public static DayOfWeek getDayOfWeek (String dayString) {
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

    public static boolean isTodayInRep (String repDay, String currentDay) {
        if (repDay.equals("Sn") && currentDay.equals("Sun"))
            return true;
        else if (repDay.equals("St") && currentDay.equals("Sat"))
            return true;
        else if (repDay.equals("R") && currentDay.equals("Thu"))
            return true;
        else return ("" + repDay.charAt(0)).equals("" + currentDay.charAt(0));
    }

    private static double getDistance(Location l1, Location l2) {

        double lat_a = l1.getLatitude();
        double lat_b = l2.getLatitude();
        double lng_a = l1.getLongitude();
        double lng_b = l2.getLongitude();

        float pk = (float) (180.f/Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public static int numberLocationShushObjects (ArrayList<ShushObject> shushObjects) {
        int i = 0;
        for (ShushObject shushObject: shushObjects) {
            if (!shushObject.getLocation().equals(ShushObject.Key.NULL)) {
                i++;
            }
        }
        return i;
    }

}
