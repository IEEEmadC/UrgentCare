package com.example.peterbrayo.urgentcare;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.Context.LOCATION_SERVICE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    String url = "https://e-charger-196317.firebaseio.com/";
    DatabaseReference ref;
    RecyclerView rv;
    ArrayList<RecyclerviewUser> arrayList;
    Button sendNotifications;
    Button sendPhoto;
    DatabaseReference notificationsRef;
    SharedPreferences sharedPreferences;


    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
//        FirebaseMessaging.getInstance().subscribeToTopic("replyNotifications");

        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationsRef = FirebaseDatabase.getInstance().getReference().child("textNotifications").child("txt");
        String LOCATION = "location";
        sharedPreferences = getActivity().getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getContext(), "Please enable location services", Toast.LENGTH_SHORT).show();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            LocationRequest request = new LocationRequest();

            //Specify how app should request the device’s location//
            request.setInterval(10000);
            request.setFastestInterval(5000);

            //Get the most accurate location data available//
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getActivity());

            //...then request location updates//
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    //Get a reference to the database, to perform read and write operations//
                    Location location = locationResult.getLastLocation();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("currentLatitude", Double.doubleToRawLongBits(location.getLatitude()));
                    editor.putLong("currentLongitude", Double.doubleToRawLongBits(location.getLongitude()));
                    editor.apply();
                    AccidentLocationModel locationModel = new AccidentLocationModel(location.getLatitude(),location.getLongitude());
                    FirebaseDatabase.getInstance().getReference().child("accidentLocation").setValue(locationModel);

                }
            }, null);

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

        //this will store the users
        arrayList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = view.findViewById(R.id.recycler_view);
        sendNotifications = view.findViewById(R.id.sendNotifications);
        sendPhoto = view.findViewById(R.id.sendPhoto);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);


        ref = FirebaseDatabase.getInstance().getReferenceFromUrl(url).child("volunteers");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("children: ", "has" + dataSnapshot.getChildrenCount());

                //get users from Firebase
                UsersFromFirebase usersFromFirebase = new UsersFromFirebase(dataSnapshot);

                //store users in arraylist which will be passed to RecylerAdapter
                arrayList = usersFromFirebase.getUsersFromFirebase();
                Log.i("onchildchanged", arrayList.get(0).getContact());

                //set adapter for recyler view(rv) where data will be got from
                rv.setAdapter(new RecyclerAdapter(getActivity(), arrayList));
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
                                                     Toast.makeText(getContext(), "Notification Message Sent Successfully", Toast.LENGTH_SHORT).show();
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

    public void onLaunchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == VictimActivity.RESULT_OK) {
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
        FirebaseDatabase.getInstance().getReferenceFromUrl(url).child("imageNotifications").child("imageUrl").setValue(imageEncoded);
    }
}
