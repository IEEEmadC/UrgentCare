package com.example.peterbrayo.urgentcare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class VolunteerListView extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    DatabaseReference ref;
    RecyclerView rv;
    ArrayList<RecyclerviewUser> arrayList;
    Button sendNotifications;
    Button sendPhoto;
    DatabaseReference notificationsRef;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        FirebaseMessaging.getInstance().subscribeToTopic("replyNotifications");

        rv = findViewById(R.id.recycler_view);
        sendNotifications = findViewById(R.id.sendNotifications);
        sendPhoto = findViewById(R.id.sendPhoto);
        notificationsRef = FirebaseDatabase.getInstance().getReference().child("textNotifications").child("txt");
        String LOCATION = "location";
        sharedPreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            LocationRequest request = new LocationRequest();

        //Specify how app should request the device’s location//
            request.setInterval(10000);
            request.setFastestInterval(5000);

        //Get the most accurate location data available//
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        //...then request location updates//
                client.requestLocationUpdates(request, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

        //Get a reference to the database, to perform read and write operations//
                        Location location = locationResult.getLastLocation();
                        AccidentLocationModel locationModel = new AccidentLocationModel(location.getLatitude(),location.getLongitude());
                        FirebaseDatabase.getInstance().getReference().child("accidentLocation").setValue(locationModel);

                    }
                }, null);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

        arrayList = new ArrayList<>();

        Log.i("rv: ", rv.toString());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rv.setLayoutManager(mLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference().child("volunteers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("children: ", "has" + dataSnapshot.getChildrenCount());
                UsersFromFirebase usersFromFirebase = new UsersFromFirebase(dataSnapshot);
                arrayList = usersFromFirebase.getUsersFromFirebase();
                Log.i("onchildchanged", arrayList.get(0).getContact());
                rv.setAdapter(new RecyclerAdapter(VolunteerListView.this, arrayList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        final String message = "Accident has happened here. Please make it as soon as possible";

            sendNotifications.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {
                                                             notificationsRef.setValue(message);
                                                             Toast.makeText(VolunteerListView.this, "Notification Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                                     }
                                                 }
            );

            sendPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLaunchCamera();
                }
            });
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.volunteer_map_view){
            startActivity(new Intent(VolunteerListView.this, VolunteersMapView.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLaunchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == VolunteerHomeActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //Bitmap resizedBitmap = getResizedBitmap(imageBitmap,400,300);
            encodeBitmapAndSaveToSharedPreferences(imageBitmap);
        }
    }

    public void encodeBitmapAndSaveToSharedPreferences(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        //save image to firebase
        FirebaseDatabase.getInstance().getReference().child("imageNotifications").child("imageUrl").setValue(imageEncoded);
    }

}
