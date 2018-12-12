package com.example.peterbrayo.urgentcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotificationReply extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_reply);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String KEY_REPLY = "key_reply";

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if(remoteInput != null){
            String reply = remoteInput.getCharSequence(KEY_REPLY).toString();
        }
    }
}
