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

    public Double retrieveLocationInterval () {
        String s = this.sharedPreferences.getString(context.getResources().getString(R.string.settings_radio_string), null);
        if (s != null) {
            if (s.charAt(0) == '5') {
                return 5.0/60.0;
            } else if (s.charAt(0) == '1') {
                return 15.0/60.0;
            } else if (s.charAt(0) == '3') {
                return 0.5;
            } else return 5.0/60.0;
        } else return 5.0/60.0;
    }

}
