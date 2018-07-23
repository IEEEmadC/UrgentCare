package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.peterbrayo.urgentcare.VolunteersMapView.LOCATION_PERMISSION_REQUEST_CODE;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        String KEY_WITHOUT_REPLY = "key_withoutPic_reply";

        if(remoteInput != null){
            CharSequence charSequence = remoteInput.getCharSequence(KEY_WITHOUT_REPLY);

            if (charSequence != null) {
                String reply = charSequence.toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("replies").child("reply");
                ref.setValue(reply);
            }
        }

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
        String message = "Accident has happened here";
        double latitude = Double.longBitsToDouble(sharedPreferences.getLong("latitude",Double.doubleToRawLongBits( 0.3656516)));
        double longitude =  Double.longBitsToDouble(sharedPreferences.getLong("longitude",Double.doubleToRawLongBits( 32.5685592)));

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(message));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
