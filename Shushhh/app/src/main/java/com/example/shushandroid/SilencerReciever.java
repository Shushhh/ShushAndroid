package com.example.shushandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import java.util.Objects;

public class SilencerReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm", "Run");
        /*
         * If the user mentions a location, perform GeoFencing processing here *
         */
        if (intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE) != null) {
            String scheduleType = Objects.requireNonNull(intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE));
            String toggleKey = intent.getStringExtra(ShushQueryScheduler.TOGGLE_KEY);
            if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_REPEAT)) {
                /*
                    incorporate another alarm manager with location frequency setting
                    Check for location with geofences and then determine whether or not to ring or silence
                 */
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "RING");
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "SILENT");
                }
                Log.i("Alarm Message", "Location Repeat");

            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_NO_REPEAT)) {
                // check toggle with geofences and then ring or silent based on location (if within vicinity -> silent and if not -> ring)
                Log.i("Alarm Message", "Location No Repeat");
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "RING");
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "SILENT");
                }
                Log.i("Alarm Message", "Time Repeat");
            } else if (scheduleType.equals(ShushQueryScheduler.Key.TIME_NO_REPEAT)) {
                if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.RING)) {
                    Log.i("Alarm Toggle", "RING");
                } else if (toggleKey != null && toggleKey.equals(ShushQueryScheduler.Key.SILENT)) {
                    Log.i("Alarm Toggle", "SILENT");
                }
                Log.i("Alarm Message", "Time No Repeat");
            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_TIME_NO_REPEAT)) {
                Log.i("Alarm Message", "Location Time No Repeat");
                // check toggle with geofences and then ring or silent based on location (if within vicinity -> silent and if not -> ring)
            } else if (scheduleType.equals(ShushQueryScheduler.Key.LOCATION_TIME_REPEAT)) {
                /*
                    incorporate another alarm manager with location frequency setting
                    Check for location with geofences and then determine whether or not to ring or silence
                 */
                Log.i("Alarm Message", "Location Time Repeat");
            }
        }
    }

    public void setRinger() {

    }

}
