package com.example.shushandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("test", "test");
            setResult(10, intent);
            finish();
        });
    }
}