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
        if (intent.getStringExtra(ShushQueryScheduler.Key.LOCATION_REPEAT) != null) {
            Log.i("Alarm Data", Objects.requireNonNull(intent.getStringExtra(ShushQueryScheduler.Key.LOCATION_REPEAT)));
        }
    }
}
