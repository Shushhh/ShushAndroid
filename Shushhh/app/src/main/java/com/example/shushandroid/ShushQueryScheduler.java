package com.example.shushandroid;

import android.app.AlarmManager;

import java.util.ArrayList;

public class ShushQueryScheduler {

    public ShushQueryScheduler () {

    }

    public void schedule (ArrayList<ShushObject> shushObjectArrayList) {
        for (ShushObject shushObject: shushObjectArrayList) {
            if (shushObject.getDate().equals(ShushObject.Key.NULL)) { // only location setting with possible repeats

            }
        }
    }

}
