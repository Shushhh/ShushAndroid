package com.example.shushandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SilencerReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm", "Run");
        /*
         * If the user mentions a location, perform GeoFencing processing here *
         */
    }
}
