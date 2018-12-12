package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.peterbrayo.urgentcare.MapFragment.LOCATION_PERMISSION_REQUEST_CODE;

public class AccidentMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        setUpMap();

        String LOCATION = "location";
        sharedPreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference().child("accidentLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String latitude = dataSnapshot.child("latitude").getValue().toString();
                String longitude = dataSnapshot.child("longitude").getValue().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("latitude",latitude);
                editor.putString("longitude", longitude);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String message = "Accident has happened here";
        double latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0.3656516"));
        double longitude =  Double.parseDouble(sharedPreferences.getString("longitude", "32.5685592"));

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(location).title(message));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        else{
            // this will display the blue dot on the map representing your current location
            mMap.setMyLocationEnabled(true);
        }
    }
}
