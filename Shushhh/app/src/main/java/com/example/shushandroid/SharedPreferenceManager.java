package com.example.shushandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    private SharedPreferences sharedPreferences;
    private static Context context;

    public SharedPreferenceManager (Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static double retrieveLocationInterval () {
        String s = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getString(context.getResources().getString(R.string.settings_radio_string), null);
        if (s != null) {
            if (s.charAt(0) == 1) {
                return 1;
            } else if (s.charAt(0) == 2) {
                return 2;
            } else if (s.charAt(0) == 3) {
                return 0.5;
            } else return 0;
        } else return 0;
    }
}
