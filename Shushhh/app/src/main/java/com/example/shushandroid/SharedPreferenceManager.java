package com.example.shushandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceManager (Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public double retrieveLocationInterval () {
        String s = this.sharedPreferences.getString(context.getResources().getString(R.string.settings_radio_string), null);
        if (s != null) {
            if (s.charAt(0) == '1') {
                return 1;
            } else if (s.charAt(0) == '2') {
                return 2;
            } else if (s.charAt(0) == '3') {
                return 0.5;
            } else return 0;
        } else return 0;
    }
}
