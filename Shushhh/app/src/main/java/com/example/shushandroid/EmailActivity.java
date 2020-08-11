package com.example.shushandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EmailActivity extends AppCompatActivity {
    EditText toEditText, subjectEditText, messageText;
    Button sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toEditText = findViewById(R.id.to_edittext);
        subjectEditText = findViewById(R.id.subject_edittext);
        messageText = findViewById(R.id.message_edittext);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> sendMail());
    }

    private void sendMail() {
        String recipientList = toEditText.getText().toString();
        String[] recipients = recipientList.split(",");

        String subject = subjectEditText.getText().toString();
        String message = messageText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        //makes sure it only opens email clients
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Choose an Email Client"));
    }
}
