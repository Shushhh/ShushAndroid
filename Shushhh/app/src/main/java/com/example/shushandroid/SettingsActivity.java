package com.example.shushandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;

import java.text.ParseException;

public class SettingsActivity extends AppCompatActivity {

    private Button saveButton;
    private RadioButton radioButton30, radioButton60, radioButton120;
    private String intervalString;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferenceEditor;
    private SharedPreferenceManager sharedPreferenceManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveButton = findViewById(R.id.saveButton);
        radioButton30 = findViewById(R.id.radio_button_30);
        radioButton60 = findViewById(R.id.radio_button_60);
        radioButton120 = findViewById(R.id.radio_button_120);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        if (sharedPreferenceManager.retrieveLocationInterval() == 0.5) {
            radioButton30.setChecked(true);
        } else if (sharedPreferenceManager.retrieveLocationInterval() == 1) {
            radioButton60.setChecked(true);
        } else if (sharedPreferenceManager.retrieveLocationInterval() == 2) {
            radioButton120.setChecked(true);
        }

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        saveButton.setOnClickListener(view -> {

            if (radioButton30.isChecked()) {
                intervalString = getResources().getString(R.string.five_minutes);
            } else if (radioButton60.isChecked()) {
                intervalString = getResources().getString(R.string.fifteen_minutes);
            } else if (radioButton120.isChecked()) {
                intervalString = getResources().getString(R.string.thirty_minutes);
            }

            sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            sharedPreferenceEditor = sharedPreferences.edit();
            sharedPreferenceEditor.putString(getResources().getString(R.string.settings_radio_string), intervalString);
            if (sharedPreferenceEditor.commit()) {
                try {
                    ShushQueryScheduler.schedule(new DatabaseManager(this).retrieveWithCursor(), this);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }
}