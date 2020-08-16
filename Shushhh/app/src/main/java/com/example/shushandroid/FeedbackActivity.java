package com.example.shushandroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {
    EditText subjectEditText, messageText;
    TextView toEmailText;
    Button sendButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toEmailText = findViewById(R.id.to_edittext);
        subjectEditText = findViewById(R.id.subject_edittext);
        messageText = findViewById(R.id.message_edittext);
        sendButton = findViewById(R.id.sendButton);

        messageText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        messageText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        sendButton.setOnClickListener(view -> sendMail());
    }

    private void sendMail() {
        String recipientList = toEmailText.getText().toString();
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
