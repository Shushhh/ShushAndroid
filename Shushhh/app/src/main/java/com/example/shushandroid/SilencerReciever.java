package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

public class SilencerReciever extends BroadcastReceiver {

    private AudioManager audioManager;
    private SharedPreferenceManager sharedPreferenceManager;
    private double hours;
    public static int index = 0;
    public static int firstTime = 1;
    public static int count = 0;

    public static long interval = 0;

    public static ArrayList<Double> latitudes = new ArrayList<>();
    public static ArrayList<Double> longitudes = new ArrayList<>();
    public static ArrayList<Double> radii = new ArrayList<>();
    public static Integer total = 0;

    public static ArrayList<Boolean> locationStatuses = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferenceManager = new SharedPreferenceManager(context);
        hours = sharedPreferenceManager.retrieveLocationInterval();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        interval = (long) (hours * 60 * 60 * 1000);

        Log.i("Count", intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE));

        if (intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE).equals(ShushQueryScheduler.Key.TIME_NO_REPEAT))
            Log.i("Count", "IN");

        /*
         * If the user mentions a location, perform GeoFencing processing here *
         */

        if (intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE) != null) {
            String scheduleType = Objects.requireNonNull(intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE));
            String toggleKey = intent.getStringExtra(ShushQueryScheduler.TOGGLE_KEY);
            if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_NO_REPEAT)) {
                if (count == 0) {
                    if (total > 0) {

                        Log.i("index comparison", index + "|" + total);

                        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                        LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {

                                if (count == 0) {
                                    Location setLocation = new Location("Current Location");
                                    setLocation.setLatitude(latitudes.get(index));
                                    setLocation.setLongitude(longitudes.get(index));

                                    Log.i("Location LatCurrentInfo", latitudes.get(index).toString());
                                    Log.i("Location LngCurrentInfo", longitudes.get(index).toString());

                                    if (setLocation.distanceTo(location) < radii.get(index)) {
                                        Log.d("Location status", "Silent");
                                        locationStatuses.add(true);
                                    } else {
                                        Log.d("Location status", "Ring");
                                        locationStatuses.add(false);
                                    }

                                    Log.i("values", "items in list: " + locationStatuses.toString());

                                    if (locationStatuses.contains(true)) {
                                        Log.i("Final result", "Silent");
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                    } else {
                                        Log.i("Final result", "Ring");
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    }

                                    Log.i("Location Lat ShushInfo", latitudes.get(index).toString());
                                    Log.i("Location Lng ShushInfo", longitudes.get(index).toString());
                                    Log.i("Location Rad ShushInfo", radii.get(index).toString());

                                    Log.i("Location Info Distance", radii.get(index) + " | " + setLocation.distanceTo(location));
                                    locationManager.removeUpdates(this);
                                    Log.i("index", index + "|" + total);
                                    index++;

                                    if (index == total) {
                                        locationStatuses.clear();
                                        index = 0;
                                    }
                                }
                            }
                        };
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            } else {
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
                            }
                        }
                    }
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time Repeat - RING");
                    count--;
                    if (count > 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        ShushQueryScheduler.isCurrentTime = true;
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        ShushQueryScheduler.isCurrentTime = false;
                    }
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    count++;
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_NO_REPEAT)) {
                Log.i("Alarm IN", "no repeat");
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time No Repeat - RING");
                    count--;
                    if (count > 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        ShushQueryScheduler.isCurrentTime = true;
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        ShushQueryScheduler.isCurrentTime = false;
                    }
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time No Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    ShushQueryScheduler.isCurrentTime = true;
                    count++;
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_TIME_NO_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    ShushQueryScheduler.isCurrentTime = true;
                    count++;
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - RING");
                    count--;
                    if (count > 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        ShushQueryScheduler.isCurrentTime = true;
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        ShushQueryScheduler.isCurrentTime = false;
                    }
                }
            }
        }
    }

    public void checkRadius (double latitude, double longitude, double radius) {

    }


}
