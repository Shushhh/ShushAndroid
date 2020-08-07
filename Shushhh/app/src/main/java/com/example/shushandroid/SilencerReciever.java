package com.example.shushandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
            String extra = Objects.requireNonNull(intent.getStringExtra(ShushQueryScheduler.SCHEDULE_TYPE));
            if (extra.equals(ShushQueryScheduler.Key.LOCATION_REPEAT)) {
                Log.i("Alarm Message", "location repeat");
            } else if (extra.equals(ShushQueryScheduler.Key.LOCATION_NO_REPEAT)) {
                Log.i("Alarm Message", "Location No Repeat");
            } else if (extra.equals(ShushQueryScheduler.Key.TIME_REPEAT)) {
                Log.i("Alarm Message", "Time Repeat");
            } else if (extra.equals(ShushQueryScheduler.Key.TIME_NO_REPEAT)) {
                Log.i("Alarm Message", "Time No Repeat");
            }
        }
    }
}
