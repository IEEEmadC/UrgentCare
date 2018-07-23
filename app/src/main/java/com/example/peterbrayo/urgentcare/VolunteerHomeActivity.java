package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;


public class VolunteerHomeActivity extends AppCompatActivity {
    private PopupWindow mPopupWindow;
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    DatabaseReference ref;
    TextView userName;
    TextView userEmail;
    TextView userContact;
    TextView userLocation;
    ImageView editPhoto;
    ImageView profilePic;
    private static final int REQUEST_IMAGE_CAPTURE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        userName = findViewById(R.id.name);
        userEmail = findViewById(R.id.email);
        userContact = findViewById(R.id.contact);
        userLocation = findViewById(R.id.location);
        profilePic = findViewById(R.id.profile);
        editPhoto = findViewById(R.id.edit_photo);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        final String userID = user.getUid();

        //get database reference
        ref = FirebaseDatabase.getInstance().getReference().child("volunteers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("childrenz", "dataSnapshot children: " + dataSnapshot.getChildrenCount());
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1: iterable) {
                    Log.i("Has child?", dataSnapshot1.getValue().toString() + "");
                }
                String name = dataSnapshot.child(userID).child("name").getValue().toString();
                String email = dataSnapshot.child(userID).child("email").getValue().toString();
                String contact = dataSnapshot.child(userID).child("contact").getValue().toString();

                userName.setText(name);
                userEmail.setText(email);
                userContact.setText(contact);
                userLocation.setText(R.string.kampala);

                if(dataSnapshot.child(userID).child("imageUrl").exists()) {
                    String imageUrl = dataSnapshot.child(userID).child("imageUrl").getValue().toString();
                    Bitmap imageBitmap = decodeFromFirebaseBase64(imageUrl);
                    profilePic.setImageBitmap(getResizedBitmap(imageBitmap, 200,200));
                }

            }

//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Log.i("onChildChanged", dataSnapshot.getChildrenCount()+"");
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
@Override
public void onCancelled(DatabaseError databaseError) {
    System.out.println("The read failed: " + databaseError.getCode());
}
        });


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(VolunteerHomeActivity.this, LoginActivity.class));
                    finish();
                }
            }



    };

    auth.addAuthStateListener(authListener);
    editPhoto.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onLaunchCamera();
        }
    });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // user = FirebaseAuth.getInstance().getCurrentUser();
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.activity_a) {

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) VolunteerHomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View customView = inflater.inflate(R.layout.change_email,null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */

            //Get the devices screen density to calculate correct pixel sizes
            float density=VolunteerHomeActivity.this.getResources().getDisplayMetrics().density;
            // create a focusable PopupWindow with the given layout and correct size
            mPopupWindow = new PopupWindow(customView, (int)density*240, (int)density*285, true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);

            // Set an elevation value for popup window
            // Call requires API level 21
            if(Build.VERSION.SDK_INT>=21){
                mPopupWindow.setElevation(5.0f);
            }

            /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
            // Finally, show the popup window at the center location of root relative layout
            mPopupWindow.showAtLocation(customView, Gravity.CENTER,0,0);

            // Get a reference for the custom view close button
            Button saveEmail = customView.findViewById(R.id.saveNewEmail);
            final EditText newEmail = customView.findViewById(R.id.editEmail);
            final ProgressBar progressBar2 = customView.findViewById((R.id.progressBar2));

            // Set a click listener for the popup window close button
            saveEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Dismiss the popup window
                    progressBar2.setVisibility(View.VISIBLE);
                    if (user != null && !newEmail.getText().toString().trim().equals("")) {
                        user.updateEmail(newEmail.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(VolunteerHomeActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                            signOut();
                                            progressBar2.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(VolunteerHomeActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                            progressBar2.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    } else if (newEmail.getText().toString().trim().equals("")) {
                        newEmail.setError("Enter email");
                        progressBar2.setVisibility(View.GONE);
                    }

                    mPopupWindow.dismiss();
                }
            });



            return true;
        }
        else if(id == R.id.chat_item){
            Intent intent = new Intent(VolunteerHomeActivity.this, ChatActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.activity_b) {
            //Do something
            return true;
        } else if (id == R.id.activity_c) {
            //Do something
            signOut();
            return true;
        } else if(id == R.id.activity_d){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getUid();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == VolunteerHomeActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePic.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap, userID);
        }
    }

    public void signOut() {
        auth.signOut();
    }

    public void onLaunchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap, String userID) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("volunteers")
                .child(userID).child("imageUrl");
        dr.setValue(imageEncoded);
    }

    public static Bitmap decodeFromFirebaseBase64(String image){
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }


    //method to scale image on ImageView
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}
