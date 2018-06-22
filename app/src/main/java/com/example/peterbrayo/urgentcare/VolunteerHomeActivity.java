package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VolunteerHomeActivity extends AppCompatActivity {
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
          user = FirebaseAuth.getInstance().getCurrentUser();

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //user = FirebaseAuth.getInstance().getCurrentUser();
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
            //Do something

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) VolunteerHomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Initialize a new instance of LayoutInflater service
          //  LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

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
            // Initialize a new instance of popup window
           // mPopupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            //Get the devices screen density to calculate correct pixel sizes
            float density=VolunteerHomeActivity.this.getResources().getDisplayMetrics().density;
            // create a focusable PopupWindow with the given layout and correct size
            mPopupWindow = new PopupWindow(customView, (int)density*240, (int)density*285, true);

            // Set an elevation value for popup window
            // Call requires API level 21
            if(Build.VERSION.SDK_INT>=21){
                mPopupWindow.setElevation(5.0f);
            }

            // Get a reference for the custom view close button
            Button saveEmail = (Button) customView.findViewById(R.id.saveNewEmail);
            final EditText newEmail = (EditText)customView.findViewById(R.id.editEmail);
            final ProgressBar progressBar2 = (ProgressBar)customView.findViewById((R.id.progressBar2));

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

            return true;
        } else if (id == R.id.activity_b) {
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

    public void signOut() {
        auth.signOut();
    }

}
