package com.example.peterbrayo.urgentcare;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.Random;

public class UsersFromFirebase {

    private DataSnapshot dataSnapshot;

    UsersFromFirebase(DataSnapshot dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }


    public ArrayList<RecyclerviewUser> getUsersFromFirebase() {
        ArrayList<RecyclerviewUser> arrayList = new ArrayList<>();
        RecyclerviewUser user;
        String imageUrl;
        Iterable<DataSnapshot> users = dataSnapshot.getChildren();
        for (DataSnapshot ds : users) {
            Log.i("children", ds.getKey());
            String name = ds.child("name").getValue().toString();
            String contact = ds.child("contact").getValue().toString();
            int rating = new Random().nextInt(6);

            if(ds.child("imageUrl").exists()) {
                imageUrl = ds.child("imageUrl").getValue().toString();
                user = new RecyclerviewUser(name, contact, rating, imageUrl);
            }
            else {
                user = new RecyclerviewUser(name, contact, rating, "");
            }
            arrayList.add(user);
        }
        return arrayList;
    }
}
