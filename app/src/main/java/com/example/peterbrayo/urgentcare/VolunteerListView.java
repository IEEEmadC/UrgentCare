package com.example.peterbrayo.urgentcare;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class VolunteerListView extends AppCompatActivity {
    DatabaseReference ref;
    RecyclerView rv;
    ArrayList<RecyclerviewUser> arrayList;
    //DataSnapshot dataSnapsht;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        rv = findViewById(R.id.recycler_view);
        arrayList = new ArrayList<>();

        Log.i("rv: ", rv.toString());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rv.setLayoutManager(mLayoutManager);


        ref = FirebaseDatabase.getInstance().getReference();
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("children: ", "has" + dataSnapshot.getChildrenCount());
                UsersFromFirebase usersFromFirebase = new UsersFromFirebase(dataSnapshot);
                arrayList = usersFromFirebase.getUsersFromFirebase();
                Log.i("onchildchanged", arrayList.get(0).getContact());
                rv.setAdapter(new RecyclerAdapter(VolunteerListView.this, arrayList));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    UsersFromFirebase usersFromFirebase = new UsersFromFirebase(dataSnapshot);
                    arrayList = usersFromFirebase.getUsersFromFirebase();
                    Log.i("onchildchanged", arrayList.get(0).getContact());
                    rv.setAdapter(new RecyclerAdapter(VolunteerListView.this, arrayList));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
