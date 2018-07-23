package com.example.peterbrayo.urgentcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

public class ReplyActivity extends AppCompatActivity {

    private Button send;
    private Button cancel;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        send = findViewById(R.id.buttonSend);
        cancel = findViewById(R.id.buttonCancel);
        text = findViewById(R.id.sendFeedback);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reply = text.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("replies").child("reply").setValue(reply);
                startActivity(new Intent(ReplyActivity.this, AccidentMapActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReplyActivity.this, AccidentMapActivity.class));
            }
        });

    }
}
