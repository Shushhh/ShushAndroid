package com.example.shushandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
    public LocationReceiver () {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Location", "Processed");
    }
}
