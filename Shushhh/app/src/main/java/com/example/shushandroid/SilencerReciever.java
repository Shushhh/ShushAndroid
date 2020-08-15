package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private Integer toggleState;
    public static int index = 0;

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
        toggleState = sharedPreferenceManager.retrieveToggleState();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        interval = (long) (hours * 60 * 60 * 1000);

        /*
         * If the user mentions a location, perform GeoFencing processing here *
         */
        int count = 0;

        final boolean[] silentChecker = {false};
        int[] locationcount = {0};
        int[] locationcount2 = {0};



        /*

        List<ShushObject> locationList = new ArrayList<>();

        locationcount2[0]++;
                locationList.add(shushObject);
                Log.d("test", locationList.toString());
                Log.i("Run", "run");

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {

                        Log.i("Listener", "listener");

                        Location setLocation = new Location("Current Location");
                        setLocation.setLatitude(shushObject.getLatLng().latitude);
                        setLocation.setLongitude(shushObject.getLatLng().longitude);

                        if (setLocation.distanceTo(location) < Double.parseDouble(shushObject.getRadius().substring(0, shushObject.getRadius().length() - 1))) {
                            silentChecker[0] = true;
                            Log.d("test", "silent");

                            System.out.println("SILENT");
                        } else {
                            if (silentChecker[0] == true) {
                                locationcount[0]++;
                                Log.d("test", "silent");
                                //Set ringer to silent
                                if (locationcount[0] == locationcount2[0]) {
                                    locationcount[0] = 0;
                                    silentChecker[0] = false;
                                    Log.d("size", "size: " + locationcount2[0]);
                                    Log.d("test", "ring");
                                    //Set ringer to ring
                                }
                            } else {
                                silentChecker[0] = false;
                                Log.d("test", "ring");
                                //Set ringer to ring
                            }
                            System.out.println(setLocation.distanceTo(location));
                        }

                        if (shushObject.equals(locationList.get(locationList.size() - 1))) {
                            locationcount[0] = 0;
                        }
                        Log.d("test", "size of list: " + locationList.size());
                        Log.d("test", "size of arraylist" + shushObjectArrayList.size());
                    }
                };
                locationManager.removeUpdates(locationListener);

         */

        if (intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE) != null) {
            String scheduleType = Objects.requireNonNull(intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE));
            String toggleKey = intent.getStringExtra(ShushQueryScheduler.TOGGLE_KEY);
            if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_NO_REPEAT)) {

                if (index == total) {
                    index = 0;
                    Log.i("index", "In");
                }

                Log.i("index comparison", index + "|" + total);

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                        LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {

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

                                if (locationStatuses.contains(true)) {
                                    Log.i("Final result", "Silent");
                                } else {
                                    Log.i("Final result", "Ring");
                                }

                                Log.i("Location Lat ShushInfo", latitudes.get(index).toString());
                                Log.i("Location Lng ShushInfo", longitudes.get(index).toString());
                                Log.i("Location Rad ShushInfo", radii.get(index).toString());

                                Log.i("Location Info Distance", radii.get(index) + " | " + setLocation.distanceTo(location));
                                locationManager.removeUpdates(this);
                                Log.i("index", index + "");
                                index++;
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
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
                            }
                        }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time Repeat - RING");
                    count--;
                    if (count > 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else {
                        audioManager.setRingerMode(toggleState);
                    }
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    count++;
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_NO_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Time No Repeat - RING");
                    count--;
                    if (count > 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else {
                        audioManager.setRingerMode(toggleState);
                    }
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Time No Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    count++;
                }
            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_TIME_NO_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - SILENT");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    count++;
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "Location Time No Repeat - RING");
                    count--;
                    if (count > 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    } else {
                        audioManager.setRingerMode(toggleState);
                    }
                }
            }
        }
    }

    public void checkRadius (double latitude, double longitude, double radius) {

    }


}
