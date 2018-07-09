package com.example.peterbrayo.urgentcare;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;


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

import java.util.HashMap;


public class VolunteersMapView extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = VolunteersMapView.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
//    FirebaseUser user;
//    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteers_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        auth = FirebaseAuth.getInstance();
//        if(user != null){
//            auth.signOut();
//        }


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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMaxZoomPreference(16);
        Log.i(TAG, "onMapReady: ");
        loginToFirebase();
    }

    private void subscribeToUpdates() {
        Log.i(TAG, "subscribeToUpdates()");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_path));
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
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
       // VolunteerLocation volLocation = dataSnapshot.getValue(VolunteerLocation.class);
        // HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
//        double lat = Double.parseDouble(value.get("latitude").toString());
//        double lng = Double.parseDouble(value.get("lo\ngitude").toString());
//
//        LatLng location = new LatLng(lat, lng);
        Log.i(TAG, "dataSnapshot.getKey(): "+ dataSnapshot.getChildrenCount());
        LatLng location;

       // if(dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
         //   location = new LatLng(volLocation.getLatitude(), volLocation.getLongitude());

        if(dataSnapshot.child(getString(R.string.firebase_path)).exists())
            Log.i("if", "this is the if");
            location = new LatLng(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));

//        else {
//            Log.i("else","this is the else");
//            location = new LatLng(0.3661242, 32.4653001);
//        }
       // }
       // LatLng location = new LatLng(0.3661242, 0.3661242);
        // if data not in Hashmap
       // if (!mMarkers.containsKey(key)) {
            //create new marker
        //location = new LatLng(0.3661242, 0.3661242);
        Log.i(TAG, "markerOptions");
            MarkerOptions markerOptions = new MarkerOptions();

            //set marker properties
           // markerOptions.title(key);
            markerOptions.position(location);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

            // add marker to map and store in mMarkers-Hashmap
           // Marker marker = mMap.addMarker(markerOptions);
        mMap.addMarker(markerOptions);
          // mMarkers.put(key, marker);
       // } else {
            // else get data and set location on map
          //  mMarkers.get(key).setPosition(location);
      //  }

        // create boundaries for markers
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker marker : mMarkers.values()) {
//            builder.include(marker.getPosition());
//        }
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    public void displayMakers(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_path));
        double lat = Double.parseDouble(ref.child("latitude").toString());
        double lon = Double.parseDouble(ref.child("longitude").toString());

        LatLng location = new LatLng(lat,lon);



        MarkerOptions markerOptions = new MarkerOptions();

        //set marker properties
        // markerOptions.title(key);
        markerOptions.position(location);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

    }
}
