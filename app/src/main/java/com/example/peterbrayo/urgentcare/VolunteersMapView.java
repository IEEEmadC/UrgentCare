package com.example.peterbrayo.urgentcare;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class VolunteersMapView extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = VolunteersMapView.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteers_map_view);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //user = FirebaseAuth.getInstance().getCurrentUser();
        getMenuInflater().inflate(R.menu.subscriber_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.volunteer_list_view){
            startActivity(new Intent(VolunteersMapView.this, VolunteerListView.class));
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(16);
        Log.i(TAG, "onMapReady: ");
        loginToFirebase();
    }

    private void subscribeToUpdates() {
        Log.i(TAG, "subscribeToUpdates()");
        String path = getString(R.string.firebase_path);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {

                Log.i("getRef()", ref.toString());
                //Log.i("refParent", ref.getParent().toString());
//                Log.i("getRef()", ref.getKey());
                if(dataSnapshot.exists())
                    setMarker(dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Log.i(TAG, "onChildChanged");
                if(dataSnapshot.exists())
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        // Authenticate with Firebase and subscribe to updates
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    subscribeToUpdates();
                    //displayMakers();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {



        Log.i(TAG, "dataSnapshot.getKey(): " + dataSnapshot.getChildrenCount());
        LatLng location;


        Iterable<DataSnapshot> users = dataSnapshot.getChildren();

        for (DataSnapshot ds : users) {
            Log.i("children",ds.getKey());

                double lat = Double.parseDouble(ds.child("location").child("latitude").getValue().toString());
                double lon = Double.parseDouble(ds.child("location").child("longitude").getValue().toString());
                String name = ds.child("name").getValue().toString();

            location = new LatLng(lat, lon);

            Log.i(TAG, "markerOptions");
            MarkerOptions markerOptions = new MarkerOptions();

            //set marker properties
            markerOptions.title(name).alpha(0.7f);
            markerOptions.position(location);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

            // add marker to map
            mMap.addMarker(markerOptions);
            mMarkers.put(name,  mMap.addMarker(markerOptions));

        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }


}
