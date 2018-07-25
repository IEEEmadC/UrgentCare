package com.example.peterbrayo.urgentcare;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReplyActivity extends AppCompatActivity {

    private Button send;
    private Button cancel;
    private EditText text;
    SharedPreferences sharedPreferences;
    String LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        sharedPreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("volunteers").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("imageUrl").getValue().toString();
                String contact = dataSnapshot.child("contact").getValue().toString();

                editor.putString("replyContact", contact);
                editor.putString("replyName", name);
                editor.putString("replyImage", image);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        send = findViewById(R.id.buttonSend);
        cancel = findViewById(R.id.buttonCancel);
        text = findViewById(R.id.sendFeedback);

        final String name = sharedPreferences.getString("replyName", "Anonymous");
        final String contact = sharedPreferences.getString("replyContact", "0700000000");
        final String image = sharedPreferences.getString("replyImage", "");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("replies");
                String reply = text.getText().toString();
                if(!image.equals("")){
                    ref.setValue(new ReplyModel(name, contact, image,reply));

                }
                else{
                    ref.setValue(new ReplyModel(name, contact,null,reply));
                }

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
