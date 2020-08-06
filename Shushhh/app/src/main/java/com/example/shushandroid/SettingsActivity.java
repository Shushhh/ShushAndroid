package com.example.shushandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    private Button saveButton;
    private RadioButton radioButton30, radioButton60, radioButton120;
    private String resultString;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferenceEditor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveButton = findViewById(R.id.saveButton);
        radioButton30 = findViewById(R.id.radio_button_30);
        radioButton60 = findViewById(R.id.radio_button_60);
        radioButton120 = findViewById(R.id.radio_button_120);

        saveButton.setOnClickListener(view -> {

            if (radioButton30.isChecked()) {
                resultString = getResources().getString(R.string.thirty_minutes);
            } else if (radioButton60.isChecked()) {
                resultString = getResources().getString(R.string.one_hour);
            } else if (radioButton120.isChecked()) {
                resultString = getResources().getString(R.string.two_hours);
            }

            sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            sharedPreferenceEditor = sharedPreferences.edit();
            sharedPreferenceEditor.putString(getResources().getString(R.string.settings_radio_string), resultString);
            if (sharedPreferenceEditor.commit()) {
                Log.i("Result" ,"To");
                Intent intent = new Intent();
                setResult(21, intent);
                finish();
            }
        });
    }
}